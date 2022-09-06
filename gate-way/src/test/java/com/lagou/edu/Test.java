package com.lagou.edu;

import com.lagou.edu.filter.BlackListFilter;

import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    public static void main(String[] args) {

        for (int i = 0; i < 10; i++) {

            // 记录IP访问频率
            AtomicInteger atomicInteger = BlackListFilter.ipTemContent.computeIfPresent("127.0.0.1", (s, integer) -> {
                System.out.println("原始值是:"+integer.get());
                int andIncrement = integer.incrementAndGet();
                System.out.println(andIncrement);
                integer.set(andIncrement);
                return integer;
            });
            if ( atomicInteger == null ) {
                BlackListFilter.ipTemContent.put("127.0.0.1",new AtomicInteger(1));
            }

        }

    }
}
