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

import com.dangdang.ddframe.rdb.sharding.jdbc.unsupported.AbstractUnsupportedGeneratedKeysResultSet;
import com.google.common.base.Preconditions;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Iterator;

/**
 * 生成键结果集.
 * 
 * @author gaohongtao
 */
@RequiredArgsConstructor
public class GeneratedKeysResultSet extends AbstractUnsupportedGeneratedKeysResultSet {
    
    private final Iterator<Number> generatedKeys;
    
    private final String generatedKeyColumn;
    
    private final Statement statement;
    
    private boolean closed;
    
    private Number currentGeneratedKey;
    
    public GeneratedKeysResultSet() {
        generatedKeys = Collections.<Number>emptyList().iterator();
        generatedKeyColumn = null;
        statement = null;
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }
    
    @Override
    public boolean next() throws SQLException {
        if (closed || !generatedKeys.hasNext()) {
            currentGeneratedKey = null;
            return false;
        }
        currentGeneratedKey = generatedKeys.next();
        return true;
    }
    
    @Override
    public void close() throws SQLException {
        closed = true;
    }
    
    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkState();
        return new GeneratedKeysResultSetMetaData(generatedKeyColumn);
    }
    
    @Override
    public boolean wasNull() throws SQLException {
        checkState();
        return false;
    }
    
    @Override
    public String getString(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.toString();
    }
    
    @Override
    public String getString(final String columnLabel) throws SQLException {
        return getString(1);
    }
    
    @Override
    public byte getByte(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.byteValue();
    }
    
    @Override
    public byte getByte(final String columnLabel) throws SQLException {
        return getByte(1);
    }
    
    @Override
    public short getShort(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.shortValue();
    }
    
    @Override
    public short getShort(final String columnLabel) throws SQLException {
        return getShort(1);
    }
    
    @Override
    public int getInt(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.intValue();
    }
    
    @Override
    public int getInt(final String columnLabel) throws SQLException {
        return getInt(1);
    }
    
    @Override
    public long getLong(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.longValue();
    }
    
    @Override
    public long getLong(final String columnLabel) throws SQLException {
        return getLong(1);
    }
    
    @Override
    public float getFloat(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.floatValue();
    }
    
    @Override
    public float getFloat(final String columnLabel) throws SQLException {
        return getFloat(1);
    }
    
    @Override
    public double getDouble(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey.doubleValue();
    }
    
    @Override
    public double getDouble(final String columnLabel) throws SQLException {
        return getDouble(1);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        checkStateForGetData();
        return new BigDecimal(currentGeneratedKey.longValue()).setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnLabel, final int scale) throws SQLException {
        return getBigDecimal(1, scale);
    }
    
    @Override
    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return new BigDecimal(currentGeneratedKey.longValue());
    }
    
    @Override
    public BigDecimal getBigDecimal(final String columnLabel) throws SQLException {
        return getBigDecimal(1);
    }
    
    @Override
    public byte[] getBytes(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return getString(columnIndex).getBytes();
    }
    
    @Override
    public byte[] getBytes(final String columnLabel) throws SQLException {
        return getBytes(1);
    }
    
    @Override
    public Object getObject(final int columnIndex) throws SQLException {
        checkStateForGetData();
        return currentGeneratedKey;
    }
    
    @Override
    public Object getObject(final String columnLabel) throws SQLException {
        return getObject(1);
    }
    
    @Override
    public int findColumn(final String columnLabel) throws SQLException {
        checkState();
        return 1;
    }
    
    @Override
    public int getType() throws SQLException {
        checkState();
        return TYPE_FORWARD_ONLY;
    }
    
    @Override
    public int getConcurrency() throws SQLException {
        checkState();
        return CONCUR_READ_ONLY;
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        checkState();
        return statement;
    }
    
    private void checkState() {
        Preconditions.checkState(!closed, "ResultSet has closed.");
    }
    
    private void checkStateForGetData() {
        checkState();
        Preconditions.checkNotNull(currentGeneratedKey, "ResultSet should call next or has no more data.");
    }
}
