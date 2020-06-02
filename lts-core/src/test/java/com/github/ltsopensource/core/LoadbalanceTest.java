package com.github.ltsopensource.core;

import com.github.ltsopensource.core.loadbalance.LoadBalance;
import com.github.ltsopensource.core.loadbalance.RoundbinLoadBalance;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 负载均衡算法测试
 * @author: Owen Jia
 * @time: 2020/6/2 10:58
 */
public class LoadbalanceTest {

    LoadBalance loadBalance = new RoundbinLoadBalance();
    List<String> nodes = new ArrayList<>();

    public LoadbalanceTest() {
        nodes.add("00");
        nodes.add("11");
        nodes.add("22");
    }

    @Test
    public void testRoundbin(){
        int cc = 50;
        while(cc > 0) {
            String result = loadBalance.select(nodes, "1");
            System.out.println(result);
            cc--;
        }
    }

//    @Test
    public void testRoundbinThreads(){
        ExecutorService executor = Executors.newScheduledThreadPool(10);
        for(int i = 1; i<=10; i++) {
            executor.execute(new RoundThread("name<"+i+">"));
        }
    }

    private class RoundThread implements Runnable{
        private String name;

        public RoundThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while(true) {
                String result = loadBalance.select(nodes, "1");
                System.out.println(this.name + " " + result);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
