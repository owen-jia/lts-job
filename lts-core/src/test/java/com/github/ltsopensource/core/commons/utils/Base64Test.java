package com.github.ltsopensource.core.commons.utils;

import org.junit.Test;

/**
 * @author: Owen Jia
 * @time: 2020/1/4 16:02
 */
public class Base64Test {

    @Test
    public void encode(){
        String admin64 = Base64.encode("admin".getBytes());
        System.out.println(admin64);
    }

    @Test
    public void decodeFast(){
        String decode = "YWRtaW4=";
        byte[] ss = com.github.ltsopensource.core.commons.utils.Base64.decodeFast(decode);
        System.out.println(new String(ss));
    }
}
