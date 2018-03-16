最近碰到的一个问题，Java代码中写了一个定时器，分布式部署的时候，多台同时执行的话就会出现重复的数据，为了避免这种情况，之前是通过在配置文件里写上可以执行这段代码的IP，代码中判断如果跟这个IP相等，则执行，否则不执行，想想也是一种比较简单的方式吧，但是感觉很low很low，所以改用分布式锁。
目前分布式锁常用的三种方式：1.数据库的锁；2.基于Redis的分布式锁；3.基于ZooKeeper的分布式锁。其中数据库中的锁有共享锁和排他锁，这两种都无法直接解决数据库的单点和可重入的问题，所以，本章还是来讲讲基于Redis的分布式锁，也可以用其他缓存（Memcache、Tair等）来实现。
# 实现分布式锁的要求：
1. 互斥性。在任何时候，当且仅有一个客户端能够持有锁。
2. 不能有死锁。持有锁的客户端崩溃后，后续客户端能够加锁。
3. 容错性。大部分Redis或者ZooKeeper节点能够正常运行。
4. 加锁解锁相同。加锁的客户端和解锁的客户端必须为同一个客户端，不能让其他的解锁了。
# Redis实现的常用命令
1. **SETNX key val**
   当且仅当key不存在时，set一个key为val的字符串，返回1；若key存在，则什么都不做，返回0。
2. **expire key timeout**
   为key设置一个超时时间，单位为second，超过这个时间锁会自动释放，避免死锁。
3. **delete key**
   删除key


# 常见写法



# Redisson



 ```java
public class RedissonTest implements Runnable {
    private static RedissonClient redisson;
    private static int count = 10000;

    private static void init() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://119.23.46.71:6340")
                .setPassword("root")
                .setDatabase(10);
        redisson = Redisson.create(config);
    }

    @Override
    public void run() {
        RLock lock = redisson.getLock("anyLock");
        lock.lock();
        count--;
        System.out.println(count);
        lock.unlock();
    }

    public static void main(String[] args) {
        init();
        for (int i = 0; i < 100; i++) {
            new Thread(new RedissonTest()).start();
        }
    }
}
```
输出结果（部分结果）：
```html
...
9930
9929
9928
9927
9926
9925
9924
9923
9922
9921

...
```
去掉lock.lock()和lock.unlock()之后（部分结果）：
```html
...
9930
9931
9933
9935
9938
9937
9940
9941
9942
9944
9947
9946
9914
...
```


# RedissonLock源码剖析
最新版的Redisson要求redis能够支持eval的命令，否则无法实现，即Redis要求2.6版本以上。在lua脚本中可以调用大部分的Redis命令，使用脚本的好处如下：
**(1)减少网络开销**:在Redis操作需求需要向Redis发送5次请求，而使用脚本功能完成同样的操作只需要发送一个请求即可，减少了网络往返时延。
**(2)原子操作**:Redis会将整个脚本作为一个整体执行，中间不会被其他命令插入。换句话说在编写脚本的过程中无需担心会出现竞态条件，也就无需使用事务。事务可以完成的所有功能都可以用脚本来实现。
**(3)复用**:客户端发送的脚本会永久存储在Redis中，这就意味着其他客户端(可以是其他语言开发的项目)可以复用这一脚本而不需要使用代码完成同样的逻辑。

全局变量：
expirationRenewalMap
internalLockLeaseTime
id
PUBSUB
commandExecutor


以lock.lock()为例，调用lock之后，底层使用的是lockInterruptibly，之后调用lockInterruptibly(-1, null);
<div align="center">

![](https://upyuncdn.wenzhihuai.com/20180316081203383746214.png)

</div>

我们来看一下lockInterruptibly的源码
```java
public void lockInterruptibly(long leaseTime, TimeUnit unit) throws InterruptedException {
    long threadId = Thread.currentThread().getId();//获取当前线程ID
    Long ttl = tryAcquire(leaseTime, unit, threadId);//尝试加锁
    // 如果为空，当前线程获取锁成功，否则已经被其他客户端加锁
    if (ttl == null) {
        return;
    }
    //等待释放，并订阅锁
    RFuture<RedissonLockEntry> future = subscribe(threadId);
    commandExecutor.syncSubscription(future);

    try {
        while (true) {
            // 重新尝试获取锁
            ttl = tryAcquire(leaseTime, unit, threadId);
            // 成功获取锁
            if (ttl == null) {
                break;
            }

            // 等待锁释放
            if (ttl >= 0) {
                getEntry(threadId).getLatch().tryAcquire(ttl, TimeUnit.MILLISECONDS);
            } else {
                getEntry(threadId).getLatch().acquire();
            }
        }
    } finally {
        // 取消订阅
        unsubscribe(future, threadId);
    }
}

```


下面是tryAcquire的实现，调用的是tryAcquireAsync
```java
    private Long tryAcquire(long leaseTime, TimeUnit unit, long threadId) {
        return get(tryAcquireAsync(leaseTime, unit, threadId));
    }
```
下面是tryAcquireAsync的实现，异步
```java
private <T> RFuture<Long> tryAcquireAsync(long leaseTime, TimeUnit unit, final long threadId) {
    if (leaseTime != -1) {
        return tryLockInnerAsync(leaseTime, unit, threadId, RedisCommands.EVAL_LONG);
    }
    RFuture<Long> ttlRemainingFuture = tryLockInnerAsync(commandExecutor.getConnectionManager().getCfg().getLockWatchdogTimeout(), TimeUnit.MILLISECONDS, threadId, RedisCommands.EVAL_LONG);
    ttlRemainingFuture.addListener(new FutureListener<Long>() {
        @Override
        public void operationComplete(Future<Long> future) throws Exception {
            if (!future.isSuccess()) {
                return;
            }

            Long ttlRemaining = future.getNow();
            // lock acquired
            if (ttlRemaining == null) {
                scheduleExpirationRenewal(threadId);
            }
        }
    });
    return ttlRemainingFuture;
}
```


下面是tryLockInnerAsyncy异步加锁
```java

<T> RFuture<T> tryLockInnerAsync(long leaseTime, TimeUnit unit, long threadId, RedisStrictCommand<T> command) {
    internalLockLeaseTime = unit.toMillis(leaseTime);

    return commandExecutor.evalWriteAsync(getName(), LongCodec.INSTANCE, command,
              "if (redis.call('exists', KEYS[1]) == 0) then " +
                  "redis.call('hset', KEYS[1], ARGV[2], 1); " +
                  "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                  "return nil; " +
              "end; " +
              "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
                  "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
                  "redis.call('pexpire', KEYS[1], ARGV[1]); " +
                  "return nil; " +
              "end; " +
              "return redis.call('pttl', KEYS[1]);",
                Collections.<Object>singletonList(getName()), internalLockLeaseTime, getLockName(threadId));
}
```

参考：
1.[Redis分布式锁的正确实现方式](https://www.cnblogs.com/linjiqin/p/8003838.html)
2.[分布式锁的多种实现方式](https://www.cnblogs.com/yuyutianxia/p/7149363.html)
3.[用Redis构建分布式锁](http://ifeve.com/redis-lock/)