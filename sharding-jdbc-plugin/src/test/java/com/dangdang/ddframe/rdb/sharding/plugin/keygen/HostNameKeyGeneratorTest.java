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

package com.dangdang.ddframe.rdb.sharding.plugin.keygen;

import com.dangdang.ddframe.rdb.sharding.keygen.DefaultKeyGenerator;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HostNameKeyGenerator.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HostNameKeyGeneratorTest {
    
    private static InetAddress rightAddress;
    
    private static InetAddress wrongAddress;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @BeforeClass
    public static void init() throws UnknownHostException {
        String ipv4 = "192.168.1.108";
        byte[] ipv4Byte = new byte[4];
        String[] ipv4StingArray = ipv4.split("\\.");
        for (int i = 0; i < 4; i++) {
            ipv4Byte[i] = (byte) Integer.valueOf(ipv4StingArray[i]).intValue();
        }
        rightAddress = InetAddress.getByAddress("dangdang-db-sharding-dev-233", ipv4Byte);
        wrongAddress = InetAddress.getByAddress("dangdang-db-sharding-dev", ipv4Byte);
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(rightAddress);
        HostNameKeyGenerator.initWorkerId();
    }
    
    @Test
    public void assertRightHostName() throws UnknownHostException, NoSuchFieldException, IllegalAccessException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(rightAddress);
        HostNameKeyGenerator.initWorkerId();
        Field workerIdField = DefaultKeyGenerator.class.getDeclaredField("workerId");
        workerIdField.setAccessible(true);
        assertThat(workerIdField.getLong(DefaultKeyGenerator.class), is(233L));
    }
    
    @Test
    public void assertUnknownHost() throws UnknownHostException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException());
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Cannot get LocalHost InetAddress, please check your network!");
        HostNameKeyGenerator.initWorkerId();
    }
    
    @Test
    public void assertWrongHostName() throws UnknownHostException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(wrongAddress);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(String.format("Wrong hostname:%s, hostname must be end with number!", wrongAddress.getHostName()));
        HostNameKeyGenerator.initWorkerId();
    }
    
    @Test
    public void assertGenerateId() throws Exception {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(rightAddress);
        HostNameKeyGenerator.initWorkerId();
        int threadNumber = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);
        final int taskNumber = threadNumber << 2;
        final HostNameKeyGenerator keyGenerator = new HostNameKeyGenerator();
        Set<Long> hashSet = new HashSet<>();
        for (int i = 0; i < taskNumber; i++) {
            hashSet.add(executor.submit(new Callable<Long>() {
                
                @Override
                public Long call() throws Exception {
                    return (Long) keyGenerator.generateKey();
                }
            }).get());
        }
        assertThat(hashSet.size(), is(taskNumber));
    }
}
