/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License,Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.parsing.lexer.dialect.oracle;

import com.dangdang.ddframe.rdb.sharding.parsing.lexer.token.Keyword;

/**
 * Oracle词法关键词.
 * 
 * @author zhangliang 
 */
public enum OracleKeyword implements Keyword {
    
    LOCKED,
    COMMIT,
    CREATION,
    UPDATED,
    UPSERT,
    CONNECT_BY_ROOT,
    STORE,
    MERGE,
    PURGE,
    ROW,
    ROWS,
    IF,
    OF,
    GOTO,
    ONLY,
    AUTOMATIC,
    MAIN,
    PCTINCREASE,
    CHUNK,
    LIMIT,
    GROUPING,
    ROLLUP,
    CUBE,
    UNLIMITED,
    SIBLINGS,
    INCLUDE,
    EXCLUDE,
    PIVOT,
    UNPIVOT,
    EXCEPTION,
    EXCEPTIONS,
    ERRORS,
    DEFERRED,
    CONNECT,
    EXCLUSIVE,
    CACHE,
    NOCACHE,
    NAV,
    IDENTIFIED,
    VERSIONS,
    WAIT,
    NOWAIT,
    STORAGE,
    SAMPLE,
    CONTINUE,
    SYSDATE,
    TIMESTAMP,
    SEGMENT,
    PARTITION,
    SUBPARTITION,
    RETURN,
    RETURNING,
    REJECT,
    MAXTRANS,
    MINEXTENTS,
    MAXEXTENTS,
    BEGIN,
    START,
    SAVEPOINT,
    MATCHED,
    LOB,
    DIMENSION,
    FORCE,
    MODEL,
    FIRST,
    NEXT,
    LAST,
    SHARE,
    EXTRACT,
    NOCOMPRESS,
    MODE,
    RULES,
    INITIALLY,
    KEEP,
    KEEP_DUPLICATES,
    REFERENCE,
    SEED,
    SESSION,
    IGNORE,
    MEASURES,
    LOGGING,
    MAXSIZE,
    FLASH_CACHE,
    CELL_FLASH_CACHE,
    NOCYCLE,
    SKIP,
    NONE,
    NULLS,
    SINGLE,
    SCN,
    INITRANS,
    BLOCK,
    IMMEDIATE,
    SEQUENTIAL,
    PCTFREE,
    PRIOR,
    XML
}
