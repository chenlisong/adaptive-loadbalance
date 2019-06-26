package com.aliware.tianchi.common;

public class TimeSliceOps {

    public static int slice = 1000;

    public static int seconds = 60;

    private final static TimeSlice ts = new TimeSlice(slice, seconds);

    public static void operate(RequestStat stat, long cost) {
        ts.request(stat, cost);
    }

    public static int getBeforeIndex() {
        return ts.beforeIndex();
    }

    public static int getBeforeCount(RequestStat stat) {
        return ts.beforeCount(stat);
    }

    public static long getCurrentSliceCost() {
        return ts.getCurrentSliceCost();
    }

    public static void main(String[] args) {
        TimeSliceOps.operate(RequestStat.FAIL, 10);
    }

}
