package com.leedane.cn.util;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * RSA加密处理
 */
public class RSACoderUtil {
    public static String encryptWithRSA(String publicKey, String plainData) throws Exception {
        RSAPublicKey rsaPublicKey = loadPublicKey(publicKey);
        if (publicKey == null) {
            throw new NullPointerException("encrypt PublicKey is null !");
        }

        Cipher cipher  = Cipher.getInstance("RSA/ECB/PKCS1Padding");// 此处如果写成"RSA"加密出来的信息JAVA服务器无法解析
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] output = cipher.doFinal(plainData.getBytes());

        //调试半天，都是因为直接复制过来的时候没有删掉下面base64加密的代码，到时字节数超过128导致服务器解析保存
        // 必须先encode成 byte[]，再转成encodeToString，否则服务器解密会失败
        //byte[] encode = Base64.encode(output, Base64.DEFAULT);
        return Base64.encodeToString(output, Base64.DEFAULT);
    }


    private static RSAPublicKey loadPublicKey(String pubKey) {
        try {
            byte[] buffer = Base64.decode(pubKey, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
