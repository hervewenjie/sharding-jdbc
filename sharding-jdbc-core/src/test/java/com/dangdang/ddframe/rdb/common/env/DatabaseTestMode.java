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

package com.dangdang.ddframe.rdb.common.env;

import com.dangdang.ddframe.rdb.sharding.constant.DatabaseType;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseTestMode {
    
    ALL, LOCAL, TEST;
    
    public List<DatabaseType> databaseTypes() {
        List<DatabaseType> result = new ArrayList<>();
        if (ALL == this) {
            return Lists.newArrayList(DatabaseType.values());
        } else if (LOCAL == this) {
            result.add(DatabaseType.MySQL);
            result.add(DatabaseType.PostgreSQL);
            result.add(DatabaseType.H2);
        } else if (TEST == this) {
            result.add(DatabaseType.H2);
        }
        return result;
    }
}
