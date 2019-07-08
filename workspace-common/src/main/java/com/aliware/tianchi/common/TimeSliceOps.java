package com.aliware.tianchi.common;

import org.apache.dubbo.rpc.Invoker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TimeSliceOps {

    public static int slice = 1000;

    public static int seconds = 60;

    public static volatile long startTime = 0;

    public static volatile long prepareDelayTime = 5 * 1000L;

    private final static TimeSlice providerTs = new TimeSlice(slice, seconds);

    private final static Map<Invoker, TimeSlice> invokeTsMap = new ConcurrentHashMap<>();

    public static void operate4Cost(long cost) {
        providerTs.request4Cost(cost);
    }

    public static void request4Stat(Invoker invoker, RequestStat stat) {

        if(invokeTsMap.get(invoker) == null) {
            synchronized (invokeTsMap) {
                if(invokeTsMap.get(invoker) == null) {
                    invokeTsMap.put(invoker, new TimeSlice(slice, seconds));
                }
            }
        }
        invokeTsMap.get(invoker).request4Stat(stat);
    }

    public static <T> Invoker<T> rebalance(List<Invoker<T>> invokers) {

        if(invokeTsMap.size() != invokers.size()) {
            synchronized (invokeTsMap) {
                if(invokeTsMap.size() != invokers.size()) {
                    invokers.stream().forEach(invoker -> {
                        invokeTsMap.put(invoker, new TimeSlice(slice, seconds));
                    });
                    startTime = System.currentTimeMillis();
                }
            }
        }

        if(System.currentTimeMillis() - startTime > prepareDelayTime) {
            int[] okCounts = new int[invokers.size()];
            int okAll = 0;
            for (int i = 0; i < invokers.size(); i++) {
                okCounts[i] = invokeTsMap.get(invokers.get(i)).beforeCount(RequestStat.OK);
                okAll += okCounts[i];
            }

            if (okAll > 0) {
                int random = ThreadLocalRandom.current().nextInt(okAll);
                okAll = 0;
                for (int i = 0; i < okCounts.length; i++) {
                    int tmp = okAll + okCounts[i];
                    if (random >= okAll && random < tmp) {
                        return invokers.get(i);
                    }
                    okAll = tmp;
                }
            }
        }
        return invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
    }


    public static Map<Invoker, TimeSlice> getInvokeTsMap() {
        return invokeTsMap;
    }

    public static long getCurrentSliceCost() {
        return providerTs.getCurrentSliceCost();
    }

    public static int getcurrentThreads() {
        return providerTs.getcurrentThreads();
    }

}
