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

package com.dangdang.ddframe.rdb.sharding.parsing.lexer.dialect.mysql;

import com.dangdang.ddframe.rdb.sharding.parsing.lexer.analyzer.Dictionary;
import com.dangdang.ddframe.rdb.sharding.parsing.lexer.Lexer;

/**
 * MySQL词法解析器.
 *
 * @author zhangliang
 */
public final class MySQLLexer extends Lexer {
    
    private static Dictionary dictionary = new Dictionary(MySQLKeyword.values());
    
    public MySQLLexer(final String input) {
        super(input, dictionary);
    }
    
    @Override
    protected boolean isHintBegin() {
        return '/' == getCurrentChar(0) && '*' == getCurrentChar(1) && '!' == getCurrentChar(2);
    }
    
    @Override
    protected boolean isCommentBegin() {
        return '#' == getCurrentChar(0) || super.isCommentBegin();
    }
    
    @Override
    protected boolean isVariableBegin() {
        return '@' == getCurrentChar(0);
    }
}
