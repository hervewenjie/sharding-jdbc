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

package com.dangdang.ddframe.rdb.sharding.api.strategy.common;

import com.dangdang.ddframe.rdb.sharding.api.ShardingValue;
import com.dangdang.ddframe.rdb.sharding.api.strategy.fixture.TestMultipleKeysShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.api.strategy.fixture.TestSingleKeyShardingAlgorithm;
import com.dangdang.ddframe.rdb.sharding.routing.strategy.ShardingStrategy;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class ShardingStrategyTest {
    
    private final Collection<String> targets = Sets.newHashSet("1", "2", "3");
    
    @Test
    public void assertDoStaticShardingWithoutShardingColumns() {
        ShardingStrategy strategy = new ShardingStrategy(Sets.newHashSet("column"), null);
        assertThat(strategy.doStaticSharding(targets, Collections.<ShardingValue<?>>emptySet()), is(targets));
    }
    
    @Test
    public void assertDoStaticShardingForEqualSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doStaticSharding(targets, createShardingValues(new ShardingValue<>("logicTable", "column", "1"))), 
                is((Collection<String>) Sets.newHashSet("1")));
    }
    
    @Test
    public void assertDoStaticShardingForInSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doStaticSharding(targets, createShardingValues(new ShardingValue<>("logicTable", "column", Sets.newHashSet("1", "3")))), 
                is((Collection<String>) Sets.newHashSet("1", "3")));
    }
    
    @Test
    public void assertDoStaticShardingForBetweenSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doStaticSharding(targets, createShardingValues(new ShardingValue<>("logicTable", "column", Range.open("1", "3")))), 
                is((Collection<String>) Sets.newHashSet("1", "2", "3")));
    }
    
    @Test
    public void assertDoStaticShardingForMultipleKeys() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestMultipleKeysShardingAlgorithm());
        assertThat(strategy.doStaticSharding(targets, createShardingValues(new ShardingValue<>("logicTable", "column", "1"))), 
                is((Collection<String>) Sets.newHashSet("1", "2", "3")));
    }
    
    @Test(expected = IllegalStateException.class)
    public void assertDoDynamicShardingWithoutShardingColumns() {
        ShardingStrategy strategy = new ShardingStrategy(Sets.newHashSet("column"), null);
        strategy.doDynamicSharding(Collections.<ShardingValue<?>>emptySet());
    }
    
    @Test
    public void assertDoDynamicShardingForEqualSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doDynamicSharding(createShardingValues(new ShardingValue<>("logicTable", "column", "1"))), is((Collection<String>) Sets.newHashSet("1")));
    }
    
    @Test
    public void assertDoDynamicShardingForInSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doDynamicSharding(createShardingValues(new ShardingValue<>("logicTable", "column", Sets.newHashSet("1", "3")))), is((Collection<String>) Sets.newHashSet("1", "3")));
    }
    
    @Test
    public void assertDoDynamicShardingForBetweenSingleKey() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestSingleKeyShardingAlgorithm());
        assertThat(strategy.doDynamicSharding(createShardingValues(new ShardingValue<>("logicTable", "column", Range.open("1", "3")))), is((Collection<String>) Sets.newHashSet("1", "2", "3")));
    }
    
    @Test
    public void assertDoDynamicShardingForMultipleKeys() {
        ShardingStrategy strategy = new ShardingStrategy("column", new TestMultipleKeysShardingAlgorithm());
        assertThat(strategy.doDynamicSharding(createShardingValues(new ShardingValue<>("logicTable", "column", "1"))), is((Collection<String>) Collections.<String>emptySet()));
    }
    
    private Collection<ShardingValue<?>> createShardingValues(final ShardingValue<String> shardingValue) {
        Collection<ShardingValue<?>> result = new ArrayList<>(1);
        result.add(shardingValue);
        return result;
    }
}
