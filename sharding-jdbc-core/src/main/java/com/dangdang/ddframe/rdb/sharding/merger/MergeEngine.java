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

package com.dangdang.ddframe.rdb.sharding.merger;

import com.dangdang.ddframe.rdb.sharding.constant.DatabaseType;
import com.dangdang.ddframe.rdb.sharding.constant.OrderType;
import com.dangdang.ddframe.rdb.sharding.merger.groupby.GroupByMemoryResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.merger.groupby.GroupByStreamResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.merger.iterator.IteratorStreamResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.merger.limit.LimitDecoratorResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.merger.orderby.OrderByStreamResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dql.select.SelectStatement;
import com.dangdang.ddframe.rdb.sharding.util.SQLUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 分片结果集归并引擎.
 *
 * @author zhangliang
 */
public final class MergeEngine {
    
    private final DatabaseType databaseType;
    
    private final List<ResultSet> resultSets;
    
    private final SelectStatement selectStatement;
    
    private final Map<String, Integer> columnLabelIndexMap;
    
    public MergeEngine(final DatabaseType databaseType, final List<ResultSet> resultSets, final SelectStatement selectStatement) throws SQLException {
        this.databaseType = databaseType;
        this.resultSets = resultSets;
        this.selectStatement = selectStatement;
        columnLabelIndexMap = getColumnLabelIndexMap(resultSets.get(0));
    }
    
    private Map<String, Integer> getColumnLabelIndexMap(final ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        Map<String, Integer> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
            result.put(SQLUtil.getExactlyValue(resultSetMetaData.getColumnLabel(i)), i);
        }
        return result;
    }
    
    /**
     * 合并结果集.
     *
     * @return 归并完毕后的结果集
     * @throws SQLException SQL异常
     */
    public ResultSetMerger merge() throws SQLException {
        selectStatement.setIndexForItems(columnLabelIndexMap);
        return decorate(build());
    }
    
    private ResultSetMerger build() throws SQLException {
        if (!selectStatement.getGroupByItems().isEmpty() || !selectStatement.getAggregationSelectItems().isEmpty()) {
            if (selectStatement.isSameGroupByAndOrderByItems()) {
                return new GroupByStreamResultSetMerger(columnLabelIndexMap, resultSets, selectStatement, getNullOrderType());
            } else {
                return new GroupByMemoryResultSetMerger(columnLabelIndexMap, resultSets, selectStatement, getNullOrderType());
            }
        }
        if (!selectStatement.getOrderByItems().isEmpty()) {
            return new OrderByStreamResultSetMerger(resultSets, selectStatement.getOrderByItems(), getNullOrderType());
        }
        return new IteratorStreamResultSetMerger(resultSets);
    }
    
    private ResultSetMerger decorate(final ResultSetMerger resultSetMerger) throws SQLException {
        ResultSetMerger result = resultSetMerger;
        if (null != selectStatement.getLimit()) {
            result = new LimitDecoratorResultSetMerger(result, selectStatement.getLimit());
        }
        return result;
    }
    
    private OrderType getNullOrderType() {
        if (DatabaseType.MySQL == databaseType || DatabaseType.Oracle == databaseType || DatabaseType.H2 == databaseType) {
            return OrderType.ASC;
        }
        return OrderType.DESC;
    }
}
