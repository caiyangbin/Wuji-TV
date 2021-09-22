package com.admin.libcommon.utils;

import android.text.TextUtils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * author:houkai
 * Date: 2017/11/6
 * describe:     非对称加密。私钥加密 & 私钥解密 & 私钥签名
 */
public class RSAUtils {

    public static final String PUBLIC_STATIC = "305C300D06092A864886F70D0101010500034B003048024100AC71572452F84F30E981A2B25A77C16A2F3893AE62CE446739AEB664614ACC3F686E70691629D74B8EE846DECA8C7C694EE89F7E74554A30E261A1BEDF07EBE90203010001";
    public static final String PRIVATE_STATIC = "30820155020100300D06092A864886F70D01010105000482013F3082013B020100024100AC71572452F84F30E981A2B25A77C16A2F3893AE62CE446739AEB664614ACC3F686E70691629D74B8EE846DECA8C7C694EE89F7E74554A30E261A1BEDF07EBE9020301000102403D8F68B08AFE22272FF51B83D85010383C0B528D5AB09032D9A0C9742457737A495F192420534AB1E7B142E11E366B30671CA3D4D5712AC65C9C2167ABE5BAD1022100D61F39271779133C6BC8922BFC2243BC76205AA67D0E65942B4B50902FC5B997022100CE2B4C68036C7F8C81C27ECB378D16DAF409D85EB6D40B008369D9374AED367F022100D04E6DDE7EF275E948FE465DEE1B33848BF2EBDD9E39BC4E8A53E9DB6BEE529F022100BD117E1761146E0027BB7AD5D852DE23DB6AEFEF24115F29905A50A661631AF702204107C8E6BCFE90EA4348AF0FE1C4F3315F7306B06FA329018AA0EFB970246544";

    public static void main(String... args) {
        RSAUtils rsa = RSAUtils.create();
        String pubKey = rsa.getPublicKey();
        String priKey = rsa.getPrivateKey();

        System.out.println("公钥:" + pubKey);
        System.out.println("私钥:" + priKey);

        //原文
        StringBuffer res = new StringBuffer();

        res.append("123456");
        System.out.println("原文对比:" + res.toString());
        System.out.println("-----------------------");

        String enStr = rsa.encodeByPublicKey(res.toString(), pubKey);
        String deStr = rsa.decodeByPrivateKey(enStr, priKey);
        System.out.println("公钥加密:" + enStr);
        System.out.println("私钥解密:" + deStr);

        System.out.println("------------------------");
        enStr = rsa.encodeByPrivateKey(res.toString(), priKey);
        deStr = rsa.decodeByPublicKey(enStr, pubKey);
        System.out.println("私钥加密:" + enStr);
        System.out.println("公钥解密:" + deStr);
    }

    public static final String KEY_ALGORITHM = "RSA";
    public static final String split = " ";//分隔符
    public static final int max = 117; //加密分段长度//不可超过117

    private static RSAUtils me;

    private RSAUtils() {
    }//单例

    /**
     * RSA 公钥 私钥对
     *
     * @return
     */
    public static RSAUtils create() {
        if (me == null) {
            me = new RSAUtils();
        }
        //生成公钥、私钥
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            kpg.initialize(512);
            KeyPair kp = kpg.generateKeyPair();
            me.publicKey = (RSAPublicKey) kp.getPublic();
            me.privateKey = (RSAPrivateCrtKey) kp.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return me;
    }

    private RSAPublicKey publicKey;
    private RSAPrivateCrtKey privateKey;

    /**
     * 获取公钥
     */
    public String getPublicKey() {
        return parseByte2HexStr(publicKey.getEncoded());
    }

    /**
     * 获取私钥
     */
    public String getPrivateKey() {
        return parseByte2HexStr(privateKey.getEncoded());
    }

    /**
     * 加密-公钥
     */
    public String encodeByPublicKey(String res, String key) {
        byte[] resBytes = res.getBytes();
        byte[] keyBytes = parseHexStr2Byte(key);//先把公钥转为2进制
        StringBuffer result = new StringBuffer();//结果
        //如果超过了100个字节就分段
        if (keyBytes.length <= max) {//不超过直接返回即可
            return encodePub(resBytes, keyBytes);
        } else {
            int size = resBytes.length / max + (resBytes.length % max > 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int len = i == size - 1 ? resBytes.length % max : max;
                byte[] bs = new byte[len];//临时数组
                System.arraycopy(resBytes, i * max, bs, 0, len);
                result.append(encodePub(bs, keyBytes));
                if (i != size - 1) result.append(split);
            }
            return result.toString();
        }
    }

    /**
     * 加密-私钥
     */
    public String encodeByPrivateKey(String res, String key) {
        byte[] resBytes = res.getBytes();
        byte[] keyBytes = parseHexStr2Byte(key);
        StringBuffer result = new StringBuffer();
        //如果超过了100个字节就分段
        if (keyBytes.length <= max) {//不超过直接返回即可
            return encodePri(resBytes, keyBytes);
        } else {
            int size = resBytes.length / max + (resBytes.length % max > 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int len = i == size - 1 ? resBytes.length % max : max;
                byte[] bs = new byte[len];//临时数组
                System.arraycopy(resBytes, i * max, bs, 0, len);
                result.append(encodePri(bs, keyBytes));
                if (i != size - 1) result.append(split);
            }
            return result.toString();
        }
    }

    /**
     * 解密-公钥
     */
    public String decodeByPublicKey(String res, String key) {
        byte[] keyBytes = parseHexStr2Byte(key);
        //先分段
        String[] rs = res.split("\\" + split);
        //分段解密
        if (rs != null) {
            int len = 0;
            //组合byte[]
            byte[] result = new byte[rs.length * max];
            for (int i = 0; i < rs.length; i++) {
                byte[] bs = decodePub(parseHexStr2Byte(rs[i]), keyBytes);
                System.arraycopy(bs, 0, result, i * max, bs.length);
                len += bs.length;
            }
            byte[] newResult = new byte[len];
            System.arraycopy(result, 0, newResult, 0, len);
            //还原字符串
            return new String(newResult);
        }
        return null;
    }

    /**
     * 解密-私钥
     */
    public String decodeByPrivateKey(String res, String key) {
        if (TextUtils.isEmpty(res)){
            return null;
        }
        byte[] keyBytes = parseHexStr2Byte(key);
        //先分段
        String[] rs = res.split("\\" + split);
        //分段解密
        if (rs != null) {
            int len = 0;
            //组合byte[]
            byte[] result = new byte[rs.length * max];
            for (int i = 0; i < rs.length; i++) {
                byte[] bs = decodePri(parseHexStr2Byte(rs[i]), keyBytes);
                System.arraycopy(bs, 0, result, i * max, bs.length);
                len += bs.length;
            }
            byte[] newResult = new byte[len];
            System.arraycopy(result, 0, newResult, 0, len);
            //还原字符串
            return new String(newResult);
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     */
    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * 加密-公钥-无分段
     */
    private String encodePub(byte[] res, byte[] keyBytes) {
        X509EncodedKeySpec x5 = new X509EncodedKeySpec(keyBytes);//用2进制的公钥生成x509
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            Key pubKey = kf.generatePublic(x5);//用KeyFactory把x509生成公钥pubKey
            Cipher cp = Cipher.getInstance(kf.getAlgorithm());//生成相应的Cipher
            cp.init(Cipher.ENCRYPT_MODE, pubKey);//给cipher初始化为加密模式，以及传入公钥pubKey
            return parseByte2HexStr(cp.doFinal(res));//以16进制的字符串返回
        } catch (Exception e) {
            System.out.println("公钥加密失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密-私钥-无分段
     */
    private String encodePri(byte[] res, byte[] keyBytes) {
        PKCS8EncodedKeySpec pk8 = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            Key priKey = kf.generatePrivate(pk8);
            Cipher cp = Cipher.getInstance(kf.getAlgorithm());
            cp.init(Cipher.ENCRYPT_MODE, priKey);
            return parseByte2HexStr(cp.doFinal(res));
        } catch (Exception e) {
            System.out.println("私钥加密失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密-公钥-无分段
     */
    private byte[] decodePub(byte[] res, byte[] keyBytes) {
        X509EncodedKeySpec x5 = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            Key pubKey = kf.generatePublic(x5);
            Cipher cp = Cipher.getInstance(kf.getAlgorithm());
            cp.init(Cipher.DECRYPT_MODE, pubKey);
            return cp.doFinal(res);
        } catch (Exception e) {
            System.out.println("公钥解密失败");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密-私钥-无分段
     */
    private byte[] decodePri(byte[] res, byte[] keyBytes) {
        PKCS8EncodedKeySpec pk8 = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM);
            Key priKey = kf.generatePrivate(pk8);
            Cipher cp = Cipher.getInstance(kf.getAlgorithm());
            cp.init(Cipher.DECRYPT_MODE, priKey);
            return cp.doFinal(res);
        } catch (Exception e) {
            System.out.println("私钥解密失败");
            e.printStackTrace();
        }
        return null;
    }
}


