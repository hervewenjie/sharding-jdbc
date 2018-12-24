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

package com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dql.select;

import com.dangdang.ddframe.rdb.sharding.parsing.parser.AbstractSQLParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.mysql.MySQLParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.mysql.MySQLSelectParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.oracle.OracleParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.oracle.OracleSelectParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.postgresql.PostgreSQLParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.postgresql.PostgreSQLSelectParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.sqlserver.SQLServerParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.sqlserver.SQLServerSelectParser;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Select语句解析器工厂.
 *
 * @author zhangliang
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SelectParserFactory {
    
    /**
     * 创建Select语句解析器.
     * 
     * @param sqlParser SQL解析器
     * @return Select语句解析器
     */
    public static AbstractSelectParser newInstance(final AbstractSQLParser sqlParser) {
        if (sqlParser instanceof MySQLParser) {
            return new MySQLSelectParser(sqlParser);
        }
        if (sqlParser instanceof OracleParser) {
            return new OracleSelectParser(sqlParser);
        }
        if (sqlParser instanceof SQLServerParser) {
            return new SQLServerSelectParser(sqlParser);
        }
        if (sqlParser instanceof PostgreSQLParser) {
            return new PostgreSQLSelectParser(sqlParser);
        }
        throw new UnsupportedOperationException(String.format("Cannot support sqlParser class [%s].", sqlParser.getClass()));
    } 
}
