/*
 * Copyright 2016-2018 Nextop Co.,Ltd
 *
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
 */

package com.zerogc.pool;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Shawn Zhang
 */
public class ObjectPoolTest extends BaseTest {



    @Test
    public void test() throws Exception {
        final AtomicInteger id = new AtomicInteger(0);

        final FastObjectPool<TestObject1> pool = createFastObjectPool(new FastObjectFactoryBase() {
            @Override
            public Object makeObject() {
                return new TestObject1(id);
            }

        });

        ExecutorService s = Executors.newFixedThreadPool(50);
        final AtomicInteger success = new AtomicInteger();
        final AtomicInteger failed = new AtomicInteger();
        int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            s.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        TestObject1 t = null;
                        try {
                            t = pool.borrowObject();
                            Thread.sleep(3);
                            if (t != null) {
                                success.incrementAndGet();
                            } else {
                                failed.incrementAndGet();
                            }
                        } catch (Throwable cause) {
                        } finally {
                            if (t != null)
                                pool.returnObject(t);
                        }
                    }
                    latch.countDown();
                }
            });
        }
        latch.await();
        assertEquals(true, success.get() > 9990);
        assertEquals(true, failed.get() < 10);
        System.out.println("sleep 20 seconds, success:" + success.get() + ", failed:" + failed.get());
        TimeUnit.SECONDS.sleep(16);
        final CountDownLatch latch1 = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            s.submit(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 1000; j++) {
                        TestObject1 t = null;
                        try {
                            t = pool.borrowObject();
                            if (t != null)
                                Thread.sleep(3);
                        } catch (Throwable cause) {
                        } finally {
                            if (t != null)
                                pool.returnObject(t);
                        }
                    }
                    latch1.countDown();
                }
            });
        }
        latch1.await();
        System.out.println("done");
        TimeUnit.SECONDS.sleep(16);
        s.shutdown();

    }
}