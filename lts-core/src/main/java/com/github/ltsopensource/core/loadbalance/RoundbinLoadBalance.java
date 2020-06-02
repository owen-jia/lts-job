package com.github.ltsopensource.core.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询算法
 * owen-jia(owen-jia@outlook.com) at 2020-06-02
 */
public class RoundbinLoadBalance extends AbstractLoadBalance{

    private AtomicInteger preSelectIndex = new AtomicInteger(0);

    private Integer preIndex = 0;

    @Override
    protected <S> S doSelect(List<S> shards, String seed) {
        return doSelect2(shards,seed);
    }

    private synchronized <S> S doSelect1(List<S> shards, String seed) {
        preIndex = (preIndex + 1) % shards.size();
        return shards.get(preIndex);
    }

    private <S> S doSelect2(List<S> shards, String seed) {
        return shards.get(preSelectIndex.updateAndGet(a -> (a + 1) % shards.size()));
    }
}
