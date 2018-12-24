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

package com.dangdang.ddframe.rdb.sharding.parsing.parser.dialect.postgresql;

import com.dangdang.ddframe.rdb.sharding.parsing.lexer.dialect.postgresql.PostgreSQLKeyword;
import com.dangdang.ddframe.rdb.sharding.parsing.lexer.token.DefaultKeyword;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dml.delete.AbstractDeleteParser;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.AbstractSQLParser;

/**
 * PostgreSQL Delete语句解析器.
 *
 * @author zhangliang
 */
public final class PostgreSQLDeleteParser extends AbstractDeleteParser {
    
    public PostgreSQLDeleteParser(final AbstractSQLParser sqlParser) {
        super(sqlParser);
    }
    
    @Override
    protected void skipBetweenDeleteAndTable() {
        getSqlParser().skipIfEqual(DefaultKeyword.FROM);
        getSqlParser().skipIfEqual(PostgreSQLKeyword.ONLY);
    }
}
