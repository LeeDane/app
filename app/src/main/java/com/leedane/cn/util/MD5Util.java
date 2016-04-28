package com.leedane.cn.util;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5加密
 * Created by LeeDane on 2015/10/7.
 */
public class MD5Util {

    private static MessageDigest md;
    /**
     * 输出转化后的字符串
     * @param origin  //原始密码
     * @return
     */
    public static String compute(String origin) {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        char[] charArray = origin.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] mdBytes = md.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < mdBytes.length; i++) {
            int val = ((int) mdBytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * volley学习提供的md5加密算法
     * @param plainText
     * @return
     */
    public static String md5(String plainText){
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String md5code = new BigInteger(1, secretBytes).toString(16);

        //如果生成数字未满32位，需要前面补0
        for(int i = 0; i < 32 - md5code.length(); i++){
            md5code = "0" + md5code;
        }
        return md5code;
    }

}
