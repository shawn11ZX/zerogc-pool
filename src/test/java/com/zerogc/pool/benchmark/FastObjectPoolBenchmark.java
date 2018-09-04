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

package com.zerogc.pool.benchmark;

import com.zerogc.pool.BaseTest;
import com.zerogc.pool.DefaultFastObjectFactory;
import com.zerogc.pool.FastObjectPool;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * @author Shawn Zhang
 */
@State(Scope.Benchmark)
public class FastObjectPoolBenchmark extends BaseTest {

    public FastObjectPool<TestObject> pool;

    @Setup(Level.Trial)
    public void doSetup() {
        pool = createFastObjectPool(new DefaultFastObjectFactory<TestObject>(TestObject.class));
    }

    @TearDown(Level.Trial)
    public void doTearDown() {
        pool.close();
    }

    @Benchmark
    @Threads(1)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_01() throws Exception {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }

    @Benchmark
    @Threads(2)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_02() throws Exception  {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }

    @Benchmark
    @Threads(5)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_05() throws Exception  {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }

    @Benchmark
    @Threads(10)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_10() throws Exception  {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }

    @Benchmark
    @Threads(20)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_20() throws Exception  {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }

    @Benchmark
    @Threads(50)
    @CompilerControl(CompilerControl.Mode.INLINE)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void thread_50() throws Exception  {
        TestObject object = pool.borrowObject();
        if (object != null) pool.returnObject(object);
    }
}
