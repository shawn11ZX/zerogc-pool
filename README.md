## Introduction
GC friendly Lightweight Thread Safe Object Pool.

To maintain pooled objects, most Object Pools libraries need allocate small objects when allocating/freeing objects. But if the objects we are going to pool is very small, it makes those libraries helpless.

In a latency critical application (FPS game), we use Java to implement server. To avoid GC as much as possible, we pool everything we can, even objects as small as float[3].

## Requirements

 - Java 7
 - maven

## Build
`mvn package -Dmaven.test.skip=true`

This will generate jar in the target directory. 

## Usage

Implement `IFastObjectFactory<T>` or use `DefaultFastObjectFactory<T>`
 Create an instance of `FastObjectPool<T>`
 Call borrowObject to return from pool
 Call returnObject to return to pool

    
    FastObjectPool<TestObject> pool = new FastObjectPool<>(
		    new DefaultFastObjectFactory<>(TestObject.class));  
    TestObject obj = pool.borrowObject();  
    pool.returnObject(obj);


## Implementation
Internally this lib uses single linked list to save pooled objects. When objects is allocated from pool the link nodes is cached.

To make it thread safe, it uses `AtomicStampedReference<T>` to set linkes between nodes.
