package com.github.ltsopensource.admin.web.support;

import com.github.ltsopensource.core.commons.utils.Md5Encrypt;
import org.apache.commons.lang.StringUtils;

/**
 * 密码Util
 * @author: Owen Jia
 * @time: 2019/10/18 13:17
 */
public class PasswordUtil {

    private static final String passwordPre = "$";

    /**
     * 加密
     * @param password  明文密码
     * @return
     */
    public static String encode(String password){
        if(StringUtils.isEmpty(password)){
            return null;
        }

        return passwordPre + Md5Encrypt.md5(password);
    }

    /**
     * 判断密码是否相同
     * @param passwordStr  明文密码
     * @param encryptStr 密文密码
     * @return boolean
     */
    public static boolean conformEncrypt(String passwordStr, String encryptStr){
        if(StringUtils.isEmpty(passwordStr)){
            return false;
        }

        if(StringUtils.isEmpty(encryptStr)) return false;

        return PasswordUtil.encode(passwordStr).equals(encryptStr);
    }

    /**
     * 判断密码是否相同
     * （支持明文密码）
     * @param password  明文密码
     * @param oldPassword 旧密码（支持明文）
     * @return
     */
    public static boolean conformPassword(String password, String oldPassword){
        if(PasswordUtil.conformEncrypt(password, oldPassword))
            return true;
        else
            return password.equals(oldPassword);
    }

}
