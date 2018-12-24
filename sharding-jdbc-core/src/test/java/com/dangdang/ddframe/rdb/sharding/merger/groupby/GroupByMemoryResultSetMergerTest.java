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

package com.dangdang.ddframe.rdb.sharding.merger.groupby;

import com.dangdang.ddframe.rdb.sharding.constant.AggregationType;
import com.dangdang.ddframe.rdb.sharding.constant.DatabaseType;
import com.dangdang.ddframe.rdb.sharding.constant.OrderType;
import com.dangdang.ddframe.rdb.sharding.merger.MergeEngine;
import com.dangdang.ddframe.rdb.sharding.merger.ResultSetMerger;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.context.OrderItem;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.context.selectitem.AggregationSelectItem;
import com.dangdang.ddframe.rdb.sharding.parsing.parser.statement.dql.select.SelectStatement;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GroupByMemoryResultSetMergerTest {
    
    private MergeEngine mergeEngine;
    
    private List<ResultSet> resultSets;
    
    private SelectStatement selectStatement;
    
    @Before
    public void setUp() throws SQLException {
        resultSets = Lists.newArrayList(mockResultSet(), mockResultSet(), mockResultSet());
        selectStatement = new SelectStatement();
        AggregationSelectItem aggregationSelectItem1 = new AggregationSelectItem(AggregationType.COUNT, "(*)", Optional.<String>absent());
        aggregationSelectItem1.setIndex(1);
        AggregationSelectItem aggregationSelectItem2 = new AggregationSelectItem(AggregationType.AVG, "(num)", Optional.<String>absent());
        aggregationSelectItem2.setIndex(2);
        AggregationSelectItem derivedAggregationSelectItem1 = new AggregationSelectItem(AggregationType.COUNT, "(num)", Optional.of("AVG_DERIVED_COUNT_0"));
        aggregationSelectItem2.setIndex(4);
        aggregationSelectItem2.getDerivedAggregationSelectItems().add(derivedAggregationSelectItem1);
        AggregationSelectItem derivedAggregationSelectItem2 = new AggregationSelectItem(AggregationType.SUM, "(num)", Optional.of("AVG_DERIVED_SUM_0"));
        aggregationSelectItem2.setIndex(5);
        aggregationSelectItem2.getDerivedAggregationSelectItems().add(derivedAggregationSelectItem2);
        selectStatement.getItems().add(aggregationSelectItem1);
        selectStatement.getItems().add(aggregationSelectItem2);
        selectStatement.getGroupByItems().add(new OrderItem(3, OrderType.ASC));
        selectStatement.getOrderByItems().add(new OrderItem(3, OrderType.DESC));
    }
    
    private ResultSet mockResultSet() throws SQLException {
        ResultSet result = mock(ResultSet.class);
        ResultSetMetaData resultSetMetaData = mock(ResultSetMetaData.class);
        when(result.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(5);
        when(resultSetMetaData.getColumnLabel(1)).thenReturn("COUNT(*)");
        when(resultSetMetaData.getColumnLabel(2)).thenReturn("AVG(num)");
        when(resultSetMetaData.getColumnLabel(3)).thenReturn("id");
        when(resultSetMetaData.getColumnLabel(4)).thenReturn("AVG_DERIVED_COUNT_0");
        when(resultSetMetaData.getColumnLabel(5)).thenReturn("AVG_DERIVED_SUM_0");
        return result;
    }
    
    @Test
    public void assertNextForResultSetsAllEmpty() throws SQLException {
        mergeEngine = new MergeEngine(DatabaseType.MySQL, resultSets, selectStatement);
        ResultSetMerger actual = mergeEngine.merge();
        assertFalse(actual.next());
    }
    
    @Test
    public void assertNextForSomeResultSetsEmpty() throws SQLException {
        mergeEngine = new MergeEngine(DatabaseType.MySQL, resultSets, selectStatement);
        when(resultSets.get(0).next()).thenReturn(true, false);
        when(resultSets.get(0).getObject(1)).thenReturn(20);
        when(resultSets.get(0).getObject(2)).thenReturn(0);
        when(resultSets.get(0).getObject(3)).thenReturn(2);
        when(resultSets.get(0).getObject(4)).thenReturn(2);
        when(resultSets.get(0).getObject(5)).thenReturn(20);
        when(resultSets.get(2).next()).thenReturn(true, true, false);
        when(resultSets.get(2).getObject(1)).thenReturn(20, 30);
        when(resultSets.get(2).getObject(2)).thenReturn(0);
        when(resultSets.get(2).getObject(3)).thenReturn(2, 3);
        when(resultSets.get(2).getObject(4)).thenReturn(2, 2, 3);
        when(resultSets.get(2).getObject(5)).thenReturn(20, 20, 30);
        ResultSetMerger actual = mergeEngine.merge();
        assertTrue(actual.next());
        assertThat((BigDecimal) actual.getValue(1, Object.class), is(new BigDecimal(30)));
        assertThat(((BigDecimal) actual.getValue(2, Object.class)).intValue(), is(10));
        assertThat((Integer) actual.getValue(3, Object.class), is(3));
        assertThat((BigDecimal) actual.getValue(4, Object.class), is(new BigDecimal(3)));
        assertThat((BigDecimal) actual.getValue(5, Object.class), is(new BigDecimal(30)));
        assertTrue(actual.next());
        assertThat((BigDecimal) actual.getValue(1, Object.class), is(new BigDecimal(40)));
        assertThat(((BigDecimal) actual.getValue(2, Object.class)).intValue(), is(10));
        assertThat((Integer) actual.getValue(3, Object.class), is(2));
        assertThat((BigDecimal) actual.getValue(4, Object.class), is(new BigDecimal(4)));
        assertThat((BigDecimal) actual.getValue(5, Object.class), is(new BigDecimal(40)));
        assertFalse(actual.next());
    }
}
