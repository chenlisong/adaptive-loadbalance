package com.aliware.tianchi.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Statistic {

    private long startTime = 0L;

    private Map<String, Integer> okMap = new HashMap<>();

    private Map<String, Integer> failMap = new HashMap<>();

    //key: host, value: small, medium, large
    private volatile Map<String, String> urlMap = new HashMap<>();

    private int sliceIndex = 0;

    public void putData(String quota, int count, int sliceIndex) {
        if(sliceIndex != this.sliceIndex) {
            okMap.clear();
            this.sliceIndex = sliceIndex;
        }

        okMap.put(quota, count);
    }

    public int getIndex(String... hosts) {
        String[] quotas = new String[hosts.length];
        for(int i=0; i<hosts.length; i++) {
            quotas[i] = putIfAbsent(hosts[i]);
        }

        int[] seeds = new int[hosts.length];
        int seedAll = 0;

        for(int i=0; i<quotas.length; i++) {
            Integer count = okMap.get(quotas[i]);
            if(count == null || count == 0) {
                return ThreadLocalRandom.current().nextInt(hosts.length);
            }
            seeds[i] = count.intValue();
            seedAll += count.intValue();
        }

        int random = ThreadLocalRandom.current().nextInt(seedAll);
        for(int i=0; i<seeds.length; i++) {
            if(random < seeds[i]) {
                return i;
            }
            random -= seeds[i];
        }
        return 0;
    }

    public String putIfAbsent(String host) {
        if(urlMap.get(host) != null) {
            return urlMap.get(host);
        }

        synchronized (urlMap) {
            if(urlMap.get(host) != null) {
                return urlMap.get(host);
            }

            char start = '-';
            char end = ':';
            boolean log = false;
            String result = "";

            char[] chars = host.toCharArray();
            for(int i=0; i<chars.length; i++) {
                if(chars[i] == end) {
                    urlMap.put(host, result);
                    return result;
                }

                if(log) {
                    result += chars[i];
                }

                if(chars[i] == start) {
                    log = true;
                }
            }
        }
        return null;
    }

}
