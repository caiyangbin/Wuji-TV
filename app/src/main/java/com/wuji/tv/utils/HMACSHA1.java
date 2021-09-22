package com.wuji.tv.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HMACSHA1 {

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    

    public static byte[] hmacSHA1(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }
}