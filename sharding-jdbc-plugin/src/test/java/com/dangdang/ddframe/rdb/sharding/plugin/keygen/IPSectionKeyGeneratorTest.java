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
@PrepareForTest(IPSectionKeyGenerator.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("all")
public class IPSectionKeyGeneratorTest {

    private static InetAddress ipv4Address;

    private static InetAddress ipv6Address;


    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void init() throws UnknownHostException {
        String ipv4 = "192.168.1.1";
        byte[] ipv4Byte = new byte[4];
        String[] ipv4StingArray = ipv4.split("\\.");
        for (int i = 0; i < 4; i++) {
            ipv4Byte[i] = (byte) Integer.valueOf(ipv4StingArray[i]).intValue();
        }
        ipv4Address = InetAddress.getByAddress("dangdang-db-sharding-dev-233", ipv4Byte);

        String ipv6 = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff";
        ipv6Address = InetAddress.getByName(ipv6);
    }

    @Test
    public void testIPV4() throws UnknownHostException, NoSuchFieldException, IllegalAccessException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(ipv4Address);
        IPSectionKeyGenerator.initWorkerId();
        Field workerIdField = DefaultKeyGenerator.class.getDeclaredField("workerId");
        workerIdField.setAccessible(true);
        assertThat(workerIdField.getLong(DefaultKeyGenerator.class), is(362L));
    }

    @Test
    public void testIPV6() throws UnknownHostException, NoSuchFieldException, IllegalAccessException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(ipv6Address);
        IPSectionKeyGenerator.initWorkerId();
        Field workerIdField = DefaultKeyGenerator.class.getDeclaredField("workerId");
        workerIdField.setAccessible(true);
        assertThat(workerIdField.getLong(DefaultKeyGenerator.class), is(1008L));
    }

    @Test
    public void testUnknownHost() throws UnknownHostException {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenThrow(new UnknownHostException());
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Cannot get LocalHost InetAddress, please check your network!");
        IPSectionKeyGenerator.initWorkerId();
    }

    @Test
    public void generateId() throws Exception {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(ipv4Address);
        IPSectionKeyGenerator.initWorkerId();
        int threadNumber = Runtime.getRuntime().availableProcessors() << 1;
        ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

        final int taskNumber = threadNumber << 2;
        final IPSectionKeyGenerator ipSectionKeyGenerator = new IPSectionKeyGenerator();
        Set<Long> hashSet = new HashSet<>();
        for (int i = 0; i < taskNumber; i++) {
            hashSet.add(executor.submit(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return (Long) ipSectionKeyGenerator.generateKey();
                }
            }).get());
        }
        assertThat(hashSet.size(), is(taskNumber));
    }
}
