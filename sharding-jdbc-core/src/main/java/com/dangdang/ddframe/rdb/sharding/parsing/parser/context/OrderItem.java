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

package com.dangdang.ddframe.rdb.sharding.parsing.parser.context;

import com.dangdang.ddframe.rdb.sharding.constant.OrderType;
import com.google.common.base.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 排序项.
 *
 * @author zhangliang
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public final class OrderItem {
    
    private final Optional<String> owner;
    
    private final Optional<String> name;
    
    private final OrderType type;
    
    private int index = -1;
    
    private Optional<String> alias;
    
    public OrderItem(final String name, final OrderType type, final Optional<String> alias) {
        this.owner = Optional.absent();
        this.name = Optional.of(name);
        this.type = type;
        this.alias = alias;
    }
    
    public OrderItem(final String owner, final String name, final OrderType type, final Optional<String> alias) {
        this.owner = Optional.of(owner);
        this.name = Optional.of(name);
        this.type = type;
        this.alias = alias;
    }
    
    public OrderItem(final int index, final OrderType type) {
        owner = Optional.absent();
        name = Optional.absent();
        this.index = index;
        this.type = type;
        alias = Optional.absent();
    }
    
    /**
     * 获取列标签.
     *
     * @return 列标签
     */
    public String getColumnLabel() {
        return alias.isPresent() ? alias.get() : name.orNull();
    }
    
    /**
     * 获取列全名.
     *
     * @return 列全名
     */
    public Optional<String> getQualifiedName() {
        if (!name.isPresent()) {
            return Optional.absent();
        }
        return owner.isPresent() ? Optional.of(owner.get() + "." + name.get()) : name;
    }
}
