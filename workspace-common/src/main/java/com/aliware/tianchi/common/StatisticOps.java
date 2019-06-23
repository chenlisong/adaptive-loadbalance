package com.aliware.tianchi.common;

import org.apache.dubbo.rpc.Invoker;

import java.util.List;

public class StatisticOps {



    private static final Statistic statistic = new Statistic();

    public static void putDate(String quota, int count, int sliceIndex) {
        statistic.putData(quota, count, sliceIndex);
    }

    public static int getIndex(String[] addrsses) {

        return statistic.getIndex(addrsses);
    }
}
