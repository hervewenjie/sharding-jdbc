/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.routing.router;

import com.codahale.metrics.Timer.Context;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.constant.DatabaseType;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.ShardingContext;
import com.dangdang.ddframe.rdb.sharding.metrics.MetricsContext;
import com.dangdang.ddframe.rdb.sharding.parsing.SQLParsingEngine;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.context.GeneratedKey;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.SQLStatement;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dml.insert.InsertStatement;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dql.select.SelectStatement;
import com.dangdang.ddframe.rdb.sharding.rewrite.SQLBuilder;
import com.dangdang.ddframe.rdb.sharding.rewrite.SQLRewriteEngine;
import com.dangdang.ddframe.rdb.sharding.routing.SQLExecutionUnit;
import com.dangdang.ddframe.rdb.sharding.routing.SQLRouteResult;
import com.dangdang.ddframe.rdb.sharding.routing.type.RoutingEngine;
import com.dangdang.ddframe.rdb.sharding.routing.type.RoutingResult;
import com.dangdang.ddframe.rdb.sharding.routing.type.TableUnit;
import com.dangdang.ddframe.rdb.sharding.routing.type.complex.CartesianDataSource;
import com.dangdang.ddframe.rdb.sharding.routing.type.complex.CartesianRoutingResult;
import com.dangdang.ddframe.rdb.sharding.routing.type.complex.CartesianTableReference;
import com.dangdang.ddframe.rdb.sharding.routing.type.complex.ComplexRoutingEngine;
import com.dangdang.ddframe.rdb.sharding.routing.type.simple.SimpleRoutingEngine;
import com.dangdang.ddframe.rdb.sharding.util.SQLLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 需要解析的SQL路由器.
 * 
 * @author zhangiang
 */
public final class ParsingSQLRouter implements SQLRouter {
    
    private final ShardingRule shardingRule;
    
    private final DatabaseType databaseType;
    
    private final boolean showSQL;
    
    private final List<Number> generatedKeys;
    
    public ParsingSQLRouter(final ShardingContext shardingContext) {
        shardingRule = shardingContext.getShardingRule();
        databaseType = shardingContext.getDatabaseType();
        showSQL = shardingContext.isShowSQL();
        generatedKeys = new LinkedList<>();
    }

    // use sql paring engine to parse sql
    @Override
    public SQLStatement parse(final String logicSQL, final int parametersSize) {
        SQLParsingEngine parsingEngine = new SQLParsingEngine(databaseType, logicSQL, shardingRule);
        Context context = MetricsContext.start("Parse SQL");
        SQLStatement result = parsingEngine.parse();
        if (result instanceof InsertStatement) {
            ((InsertStatement) result).appendGenerateKeyToken(shardingRule, parametersSize);
        }
        MetricsContext.stop(context);
        return result;
    }

    // do route
    @Override
    public SQLRouteResult route(final String logicSQL, final List<Object> parameters, final SQLStatement sqlStatement) {
        final Context context = MetricsContext.start("Route SQL");
        SQLRouteResult result = new SQLRouteResult(sqlStatement);
        // if insert, process key
        if (sqlStatement instanceof InsertStatement && null != ((InsertStatement) sqlStatement).getGeneratedKey()) {
            processGeneratedKey(parameters, (InsertStatement) sqlStatement, result);
        }
        RoutingResult routingResult = route(parameters, sqlStatement);
        SQLRewriteEngine rewriteEngine = new SQLRewriteEngine(shardingRule, logicSQL, sqlStatement);
        boolean isSingleRouting = routingResult.isSingleRouting();
        if (sqlStatement instanceof SelectStatement && null != ((SelectStatement) sqlStatement).getLimit()) {
            processLimit(parameters, (SelectStatement) sqlStatement, isSingleRouting);
        }
        SQLBuilder sqlBuilder = rewriteEngine.rewrite(!isSingleRouting);
        if (routingResult instanceof CartesianRoutingResult) {
            for (CartesianDataSource cartesianDataSource : ((CartesianRoutingResult) routingResult).getRoutingDataSources()) {
                for (CartesianTableReference cartesianTableReference : cartesianDataSource.getRoutingTableReferences()) {
                    result.getExecutionUnits().add(new SQLExecutionUnit(cartesianDataSource.getDataSource(), rewriteEngine.generateSQL(cartesianTableReference, sqlBuilder)));
                }
            }
        } else {
            for (TableUnit each : routingResult.getTableUnits().getTableUnits()) {
                result.getExecutionUnits().add(new SQLExecutionUnit(each.getDataSourceName(), rewriteEngine.generateSQL(each, sqlBuilder)));
            }
        }
        MetricsContext.stop(context);
        if (showSQL) {
            SQLLogger.logSQL(logicSQL, sqlStatement, result.getExecutionUnits(), parameters);
        }
        return result;
    }

    // route
    private RoutingResult route(final List<Object> parameters, final SQLStatement sqlStatement) {
        Collection<String> tableNames = sqlStatement.getTables().getTableNames();
        RoutingEngine routingEngine;
        // simple routing engine
        if (1 == tableNames.size() || shardingRule.isAllBindingTables(tableNames)) {
            routingEngine = new SimpleRoutingEngine(shardingRule, parameters, tableNames.iterator().next(), sqlStatement);
        }
        // complex routing engine
        else {
            // TODO 可配置是否执行笛卡尔积
            routingEngine = new ComplexRoutingEngine(shardingRule, parameters, tableNames, sqlStatement);
        }
        return routingEngine.route();
    }
    
    private void processGeneratedKey(final List<Object> parameters, final InsertStatement insertStatement, final SQLRouteResult sqlRouteResult) {
        GeneratedKey generatedKey = insertStatement.getGeneratedKey();
        if (parameters.isEmpty()) {
            sqlRouteResult.getGeneratedKeys().add(generatedKey.getValue());
        } else if (parameters.size() == generatedKey.getIndex()) {
            Number key = shardingRule.generateKey(insertStatement.getTables().getSingleTableName());
            parameters.add(key);
            setGeneratedKeys(sqlRouteResult, key);
        } else if (-1 != generatedKey.getIndex()) {
            setGeneratedKeys(sqlRouteResult, (Number) parameters.get(generatedKey.getIndex()));
        }
    }
    
    private void setGeneratedKeys(final SQLRouteResult sqlRouteResult, final Number generatedKey) {
        generatedKeys.add(generatedKey);
        sqlRouteResult.getGeneratedKeys().clear();
        sqlRouteResult.getGeneratedKeys().addAll(generatedKeys);
    }
    
    private void processLimit(final List<Object> parameters, final SelectStatement selectStatement, final boolean isSingleRouting) {
        boolean isNeedFetchAll = (!selectStatement.getGroupByItems().isEmpty() || !selectStatement.getAggregationSelectItems().isEmpty()) && !selectStatement.isSameGroupByAndOrderByItems();
        selectStatement.getLimit().processParameters(parameters, !isSingleRouting, isNeedFetchAll);
    }
}
