package com.github.ltsopensource.admin.web.support;

import com.sun.org.apache.xml.internal.security.utils.Base64;
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

    @Test
    public void test01(){
        String username = "admin",password = "admin123";
        String result01 = Base64.encode((username+":"+password).getBytes());
        System.out.println(result01);

        String result02 = new String(com.github.ltsopensource.core.commons.utils.Base64.decodeFast(result01));
        System.out.println(result02);

        String result03 = new String(java.util.Base64.getEncoder().encode((username+":"+password).getBytes()));
        System.out.println(result03);
    }
}
