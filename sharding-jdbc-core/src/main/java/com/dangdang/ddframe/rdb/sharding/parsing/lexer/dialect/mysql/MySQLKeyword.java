/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parsing.lexer.dialect.mysql;

import com.dangdang.ddframe.rdb.sharding.parsing.lexer.token.Keyword;

/**
 * MySQL词法关键词.
 * 
 * @author zhangliang 
 */
public enum MySQLKeyword implements Keyword {
    
    SHOW, 
    DUAL, 
    LIMIT, 
    OFFSET, 
    VALUE, 
    BEGIN, 
    TRUE, 
    FALSE, 
    FORCE, 
    ROW, 
    PARTITION, 
    DISTINCTROW, 
    KILL, 
    QUICK, 
    BINARY, 
    CACHE, 
    SQL_CACHE, 
    SQL_NO_CACHE, 
    SQL_SMALL_RESULT, 
    SQL_BIG_RESULT, 
    SQL_BUFFER_RESULT, 
    SQL_CALC_FOUND_ROWS, 
    LOW_PRIORITY, 
    HIGH_PRIORITY, 
    OPTIMIZE, 
    ANALYZE, 
    IGNORE, 
    IDENTIFIED
}
