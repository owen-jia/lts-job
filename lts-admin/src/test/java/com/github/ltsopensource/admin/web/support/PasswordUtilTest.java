package com.github.ltsopensource.admin.web.support;

import org.junit.Test;

/**
 * @author: Owen Jia
 * @time: 2019/10/18 13:35
 */
public class PasswordUtilTest {

    @Test
    public void pwTest(){
        String pw = "12";

        String enPw = PasswordUtil.encode(pw);

        System.out.println(enPw);

        System.out.println(PasswordUtil.conformPassword("12",enPw));
    }

}
