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

package com.dangdang.ddframe.rdb.sharding.jdbc.core.connection;

import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.constant.SQLType;
import com.dangdang.ddframe.rdb.sharding.fixture.TestDataSource;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.ShardingContext;
import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.MasterSlaveDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public final class ShardingConnectionTest {
    
    private static final DataSource MASTER_DATA_SOURCE = new TestDataSource("test_ds_master");
    
    private static final DataSource SLAVE_DATA_SOURCE = new TestDataSource("test_ds_slave");
    
    private static final MasterSlaveDataSource MASTER_SLAVE_DATA_SOURCE = new MasterSlaveDataSource("test_ds", MASTER_DATA_SOURCE, Collections.singletonList(SLAVE_DATA_SOURCE));
    
    private static final String DS_NAME = "default";
    
    private ShardingConnection connection;
    
    @BeforeClass
    public static void init() {
        ((TestDataSource) SLAVE_DATA_SOURCE).setThrowExceptionWhenClosing(true);
    }
    
    @Before
    public void setUp() {
        Map<String, DataSource> dataSourceMap = new HashMap<>(1);
        dataSourceMap.put(DS_NAME, MASTER_SLAVE_DATA_SOURCE);
        DataSourceRule dataSourceRule = new DataSourceRule(dataSourceMap);
        ShardingRule rule = new ShardingRule.ShardingRuleBuilder().dataSourceRule(dataSourceRule)
                .tableRules(Collections.singleton(new  TableRule.TableRuleBuilder("test").dataSourceRule(dataSourceRule).build())).build();
        ShardingContext shardingContext = new ShardingContext(rule, null, null, false);
        connection = new ShardingConnection(shardingContext);
    }
    
    @After
    public void clear() {
        try {
            connection.close();
        } catch (final SQLException ignored) {
        }
    }
    
    @Test
    public void assertGetConnectionSelectThenUpdate() throws Exception {
        assertNotSame(connection.getConnection(DS_NAME, SQLType.DQL), connection.getConnection(DS_NAME, SQLType.DML));
    }
    
    @Test
    public void assertGetConnectionUpdateThenSelect() throws Exception {
        assertSame(connection.getConnection(DS_NAME, SQLType.DML), connection.getConnection(DS_NAME, SQLType.DQL));
    }
    
    @Test
    public void assertGetConnectionBothSelect() throws Exception {
        assertSame(connection.getConnection(DS_NAME, SQLType.DQL), connection.getConnection(DS_NAME, SQLType.DQL));
    }
    
    @Test
    public void assertGetConnectionBothUpdate() throws Exception {
        assertSame(connection.getConnection(DS_NAME, SQLType.DML), connection.getConnection(DS_NAME, SQLType.DML));
    }
    
    @Test
    public void assertGetConnectionMixed() throws Exception {
        Connection slaveConnection = connection.getConnection(DS_NAME, SQLType.DQL);
        Connection masterConnection = connection.getConnection(DS_NAME, SQLType.DML);
        assertNotSame(slaveConnection, masterConnection);
        assertNotSame(slaveConnection, connection.getConnection(DS_NAME, SQLType.DQL));
        assertNotSame(slaveConnection, connection.getConnection(DS_NAME, SQLType.DML));
        assertSame(masterConnection, connection.getConnection(DS_NAME, SQLType.DQL));
        assertSame(masterConnection, connection.getConnection(DS_NAME, SQLType.DML));
    }
    
    @Test
    public void assertRelease() throws Exception {
        Connection conn = connection.getConnection(DS_NAME, SQLType.DML);
        connection.release(conn);
        assertNotSame(conn, connection.getConnection(DS_NAME, SQLType.DML));
    }
}
