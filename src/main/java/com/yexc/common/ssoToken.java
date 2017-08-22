package com.yexc.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2017/8/18.
 */
public class ssoToken {
    public static String EncoderByMd5(String str) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            Map<Object, Object> map = new HashMap<>();
            return ToHex(messageDigest.digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String ToHex(byte[] md) {
        char hexDigits[] = {'0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
