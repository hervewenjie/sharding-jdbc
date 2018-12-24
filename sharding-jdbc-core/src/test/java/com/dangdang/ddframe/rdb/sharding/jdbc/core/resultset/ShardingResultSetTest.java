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

package com.dangdang.ddframe.rdb.sharding.jdbc.core.resultset;

import com.dangdang.ddframe.rdb.sharding.merger.ResultSetMerger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class ShardingResultSetTest {
    
    @Mock
    private ResultSetMerger mergeResultSet;
    
    private ShardingResultSet shardingResultSet;
    
    @Before
    public void setUp() {
        shardingResultSet = new ShardingResultSet(Collections.singletonList(mock(ResultSet.class)), mergeResultSet);
    }
    
    @Test
    public void assertNext() throws SQLException {
        when(mergeResultSet.next()).thenReturn(true);
        assertTrue(shardingResultSet.next());
    }
    
    @Test
    public void assertWasNull() throws SQLException {
        assertFalse(shardingResultSet.wasNull());
    }
    
    @Test
    public void assertGetBooleanWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, boolean.class)).thenReturn(true);
        assertTrue(shardingResultSet.getBoolean(1));
    }
    
    @Test
    public void assertGetBooleanWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", boolean.class)).thenReturn(true);
        assertTrue(shardingResultSet.getBoolean("label"));
    }
    
    @Test
    public void assertGetByteWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, byte.class)).thenReturn((byte) 1);
        assertThat(shardingResultSet.getByte(1), is((byte) 1));
    }
    
    @Test
    public void assertGetByteWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", byte.class)).thenReturn((byte) 1);
        assertThat(shardingResultSet.getByte("label"), is((byte) 1));
    }
    
    @Test
    public void assertGetShortWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, short.class)).thenReturn((short) 1);
        assertThat(shardingResultSet.getShort(1), is((short) 1));
    }
    
    @Test
    public void assertGetShortWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", short.class)).thenReturn((short) 1);
        assertThat(shardingResultSet.getShort("label"), is((short) 1));
    }
    
    @Test
    public void assertGetIntWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, int.class)).thenReturn(1);
        assertThat(shardingResultSet.getInt(1), is(1));
    }
    
    @Test
    public void assertGetIntWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", int.class)).thenReturn((short) 1);
        assertThat(shardingResultSet.getInt("label"), is(1));
    }
    
    @Test
    public void assertGetLongWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, long.class)).thenReturn(1L);
        assertThat(shardingResultSet.getLong(1), is(1L));
    }
    
    @Test
    public void assertGetLongWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", long.class)).thenReturn(1L);
        assertThat(shardingResultSet.getLong("label"), is(1L));
    }
    
    @Test
    public void assertGetFloatWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, float.class)).thenReturn(1F);
        assertThat(shardingResultSet.getFloat(1), is(1F));
    }
    
    @Test
    public void assertGetFloatWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", float.class)).thenReturn(1F);
        assertThat(shardingResultSet.getFloat("label"), is(1F));
    }
    
    @Test
    public void assertGetDoubleWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, double.class)).thenReturn(1D);
        assertThat(shardingResultSet.getDouble(1), is(1D));
    }
    
    @Test
    public void assertGetDoubleWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", double.class)).thenReturn(1D);
        assertThat(shardingResultSet.getDouble("label"), is(1D));
    }
    
    @Test
    public void assertGetStringWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, String.class)).thenReturn("value");
        assertThat(shardingResultSet.getString(1), is("value"));
    }
    
    @Test
    public void assertGetStringWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", String.class)).thenReturn("value");
        assertThat(shardingResultSet.getString("label"), is("value"));
    }
    
    @Test
    public void assertGetBigDecimalWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, BigDecimal.class)).thenReturn(new BigDecimal("1"));
        assertThat(shardingResultSet.getBigDecimal(1), is(new BigDecimal("1")));
    }
    
    @Test
    public void assertGetBigDecimalWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", BigDecimal.class)).thenReturn(new BigDecimal("1"));
        assertThat(shardingResultSet.getBigDecimal("label"), is(new BigDecimal("1")));
    }
    
    @Test
    public void assertGetBigDecimalAndScaleWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, BigDecimal.class)).thenReturn(new BigDecimal("1"));
        assertThat(shardingResultSet.getBigDecimal(1, 10), is(new BigDecimal("1")));
    }
    
    @Test
    public void assertGetBigDecimalAndScaleWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", BigDecimal.class)).thenReturn(new BigDecimal("1"));
        assertThat(shardingResultSet.getBigDecimal("label", 10), is(new BigDecimal("1")));
    }
    
    @Test
    public void assertGetBytesWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, byte[].class)).thenReturn(new byte[] {(byte) 1});
        assertThat(shardingResultSet.getBytes(1), is(new byte[] {(byte) 1}));
    }
    
    @Test
    public void assertGetBytesWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", byte[].class)).thenReturn(new byte[] {(byte) 1});
        assertThat(shardingResultSet.getBytes("label"), is(new byte[] {(byte) 1}));
    }
    
    @Test
    public void assertGetDateWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, Date.class)).thenReturn(new Date(0L));
        assertThat(shardingResultSet.getDate(1), is(new Date(0L)));
    }
    
    @Test
    public void assertGetDateWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", Date.class)).thenReturn(new Date(0L));
        assertThat(shardingResultSet.getDate("label"), is(new Date(0L)));
    }
    
    @Test
    public void assertGetDateAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue(1, Date.class, calendar)).thenReturn(new Date(0L));
        assertThat(shardingResultSet.getDate(1, calendar), is(new Date(0L)));
    }
    
    @Test
    public void assertGetDateAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue("label", Date.class, calendar)).thenReturn(new Date(0L));
        assertThat(shardingResultSet.getDate("label", calendar), is(new Date(0L)));
    }
    
    @Test
    public void assertGetTimeWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, Time.class)).thenReturn(new Time(0L));
        assertThat(shardingResultSet.getTime(1), is(new Time(0L)));
    }
    
    @Test
    public void assertGetTimeWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", Time.class)).thenReturn(new Time(0L));
        assertThat(shardingResultSet.getTime("label"), is(new Time(0L)));
    }
    
    @Test
    public void assertGetTimeAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue(1, Time.class, calendar)).thenReturn(new Time(0L));
        assertThat(shardingResultSet.getTime(1, calendar), is(new Time(0L)));
    }
    
    @Test
    public void assertGetTimeAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue("label", Time.class, calendar)).thenReturn(new Time(0L));
        assertThat(shardingResultSet.getTime("label", calendar), is(new Time(0L)));
    }
    
    @Test
    public void assertGetTimestampWithColumnIndex() throws SQLException {
        when(mergeResultSet.getValue(1, Timestamp.class)).thenReturn(new Timestamp(0L));
        assertThat(shardingResultSet.getTimestamp(1), is(new Timestamp(0L)));
    }
    
    @Test
    public void assertGetTimestampWithColumnLabel() throws SQLException {
        when(mergeResultSet.getValue("label", Timestamp.class)).thenReturn(new Timestamp(0L));
        assertThat(shardingResultSet.getTimestamp("label"), is(new Timestamp(0L)));
    }
    
    @Test
    public void assertGetTimestampAndCalendarWithColumnIndex() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue(1, Timestamp.class, calendar)).thenReturn(new Timestamp(0L));
        assertThat(shardingResultSet.getTimestamp(1, calendar), is(new Timestamp(0L)));
    }
    
    @Test
    public void assertGetTimestampAndCalendarWithColumnLabel() throws SQLException {
        Calendar calendar = Calendar.getInstance();
        when(mergeResultSet.getCalendarValue("label", Timestamp.class, calendar)).thenReturn(new Timestamp(0L));
        assertThat(shardingResultSet.getTimestamp("label", calendar), is(new Timestamp(0L)));
    }
    
    @Test
    public void assertGetAsciiStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream(1, "Ascii")).thenReturn(inputStream);
        assertThat(shardingResultSet.getAsciiStream(1), is(inputStream));
    }
    
    @Test
    public void assertGetAsciiStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream("label", "Ascii")).thenReturn(inputStream);
        assertThat(shardingResultSet.getAsciiStream("label"), is(inputStream));
    }
    
    @Test
    public void assertGetUnicodeStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream(1, "Unicode")).thenReturn(inputStream);
        assertThat(shardingResultSet.getUnicodeStream(1), is(inputStream));
    }
    
    @Test
    public void assertGetUnicodeStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream("label", "Unicode")).thenReturn(inputStream);
        assertThat(shardingResultSet.getUnicodeStream("label"), is(inputStream));
    }
    
    @Test
    public void assertGetBinaryStreamWithColumnIndex() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream(1, "Binary")).thenReturn(inputStream);
        assertThat(shardingResultSet.getBinaryStream(1), is(inputStream));
    }
    
    @Test
    public void assertGetBinaryStreamWithColumnLabel() throws SQLException {
        InputStream inputStream = mock(InputStream.class);
        when(mergeResultSet.getInputStream("label", "Binary")).thenReturn(inputStream);
        assertThat(shardingResultSet.getBinaryStream("label"), is(inputStream));
    }
    
    @Test
    public void assertGetCharacterStreamWithColumnIndex() throws SQLException {
        Reader reader = mock(Reader.class);
        when(mergeResultSet.getValue(1, Reader.class)).thenReturn(reader);
        assertThat(shardingResultSet.getCharacterStream(1), is(reader));
    }
    
    @Test
    public void assertGetCharacterStreamWithColumnLabel() throws SQLException {
        Reader reader = mock(Reader.class);
        when(mergeResultSet.getValue("label", Reader.class)).thenReturn(reader);
        assertThat(shardingResultSet.getCharacterStream("label"), is(reader));
    }
    
    @Test
    public void assertGetBlobWithColumnIndex() throws SQLException {
        Blob blob = mock(Blob.class);
        when(mergeResultSet.getValue(1, Blob.class)).thenReturn(blob);
        assertThat(shardingResultSet.getBlob(1), is(blob));
    }
    
    @Test
    public void assertGetBlobWithColumnLabel() throws SQLException {
        Blob blob = mock(Blob.class);
        when(mergeResultSet.getValue("label", Blob.class)).thenReturn(blob);
        assertThat(shardingResultSet.getBlob("label"), is(blob));
    }
    
    @Test
    public void assertGetClobWithColumnIndex() throws SQLException {
        Clob clob = mock(Clob.class);
        when(mergeResultSet.getValue(1, Clob.class)).thenReturn(clob);
        assertThat(shardingResultSet.getClob(1), is(clob));
    }
    
    @Test
    public void assertGetClobWithColumnLabel() throws SQLException {
        Clob clob = mock(Clob.class);
        when(mergeResultSet.getValue("label", Clob.class)).thenReturn(clob);
        assertThat(shardingResultSet.getClob("label"), is(clob));
    }
    
    @Test
    public void assertGetURLWithColumnIndex() throws SQLException, MalformedURLException {
        when(mergeResultSet.getValue(1, URL.class)).thenReturn(new URL("http://xxx.xxx"));
        assertThat(shardingResultSet.getURL(1), is(new URL("http://xxx.xxx")));
    }
    
    @Test
    public void assertGetURLWithColumnLabel() throws SQLException, MalformedURLException {
        when(mergeResultSet.getValue("label", URL.class)).thenReturn(new URL("http://xxx.xxx"));
        assertThat(shardingResultSet.getURL("label"), is(new URL("http://xxx.xxx")));
    }
    
    @Test
    public void assertGetSQLXMLWithColumnIndex() throws SQLException {
        SQLXML sqlxml = mock(SQLXML.class);
        when(mergeResultSet.getValue(1, SQLXML.class)).thenReturn(sqlxml);
        assertThat(shardingResultSet.getSQLXML(1), is(sqlxml));
    }
    
    @Test
    public void assertGetSQLXMLWithColumnLabel() throws SQLException {
        SQLXML sqlxml = mock(SQLXML.class);
        when(mergeResultSet.getValue("label", SQLXML.class)).thenReturn(sqlxml);
        assertThat(shardingResultSet.getSQLXML("label"), is(sqlxml));
    }
    
    @Test
    public void assertGetObjectWithColumnIndex() throws SQLException, MalformedURLException {
        when(mergeResultSet.getValue(1, Object.class)).thenReturn("object_value");
        assertThat(shardingResultSet.getObject(1), is((Object) "object_value"));
    }
    
    @Test
    public void assertGetObjectWithColumnLabel() throws SQLException, MalformedURLException {
        when(mergeResultSet.getValue("label", Object.class)).thenReturn("object_value");
        assertThat(shardingResultSet.getObject("label"), is((Object) "object_value"));
    }
}
