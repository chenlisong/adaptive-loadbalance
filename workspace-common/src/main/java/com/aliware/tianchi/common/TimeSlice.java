package com.aliware.tianchi.common;

import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

public class TimeSlice {

    //second
    private int seconds = 60;

    //ms
    private int slices = 500;

    private int size = seconds * 1000 / slices;

    // 收集最近1分钟的数据
    private LongAdder[] sucSlices = new LongAdder[size];

    private LongAdder[] failSlices = new LongAdder[size];

    private volatile int beforeOkIndex = 0;

    private int beforeOkCount = 0;

    private volatile int beforeFailIndex = 0;

    private int beforeFailCount = 0;

    // 限流使用
    private volatile int currentSliceIndex = -1;

    private LongAdder currentSliceCost = new LongAdder();

    public TimeSlice(int slices, int seconds) {
        this.seconds = seconds;
        this.slices = slices;
        this.init();
    }

    private void init() {
        IntStream.rangeClosed(0, size-1).forEach(unit -> {
            sucSlices[unit] = new LongAdder();
            failSlices[unit] = new LongAdder();
        });
    }

    public void request(RequestStat stat, long cost) {
        int index = (int)(System.currentTimeMillis() / slices % size);
        if(stat == RequestStat.OK) {
            sucSlices[index].increment();
        }else if(stat == RequestStat.FAIL) {
            failSlices[index].increment();
        }

        if(index != currentSliceIndex) {
            synchronized(this){
                if(index != currentSliceIndex) {
                    currentSliceCost = new LongAdder();
                    currentSliceIndex = index;
                }
            }
        }
        currentSliceCost.add(cost);
    }

    public int beforeIndex() {
        int index = (int)(System.currentTimeMillis() / slices % size);
        return index == 0 ? size-1 : index-1;
    }

    public int beforeCount(RequestStat stat) {
        int index = (int)(System.currentTimeMillis() / slices % size);

        if(stat == RequestStat.OK && index == beforeOkIndex) {
            return beforeOkCount;
        }

        if(stat == RequestStat.FAIL && index == beforeFailIndex) {
            return beforeFailCount;
        }

        synchronized (this){
            if(stat == RequestStat.OK && index == beforeOkIndex) {
                return beforeOkCount;
            }

            if(stat == RequestStat.FAIL && index == beforeFailIndex) {
                return beforeFailCount;
            }

            if(index == 0) {
                index = size;
            }

            if(stat == RequestStat.OK) {
                beforeOkIndex = index;
                beforeOkCount = sucSlices[index - 1].intValue();
                return beforeOkCount;
            }else if(stat == RequestStat.FAIL) {
                beforeFailIndex = index;
                beforeFailCount = failSlices[index - 1].intValue();
                return beforeFailCount;
            }
            return 1;
        }
    }

    public long getCurrentSliceCost() {
        int index = (int)(System.currentTimeMillis() / slices % size);
        if(index != currentSliceIndex) {
            synchronized (this) {
                if(index != currentSliceIndex) {
                    currentSliceCost = new LongAdder();
                    currentSliceIndex = index;
                }
            }
        }
        return currentSliceCost.longValue();
    }

    public static void main(String[] args) {
        TimeSlice ts = new TimeSlice(500, 60);
        ts.init();
        IntStream.rangeClosed(0, 10).forEach(unit -> {
            ts.request(RequestStat.OK, 10);
        });
        IntStream.rangeClosed(0, 10).forEach(unit -> {
            ts.request(RequestStat.OK, 10);
        });

        System.out.println(ts.beforeCount(RequestStat.OK));
    }

}
