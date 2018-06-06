package com.tc.lottery.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class DES3code {

    public static String configKey2 = "MfI7rgld";

    public static final String KEY_ALGORITHM = "DESede";
    public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

    private static Key toKey(byte[] key) throws Exception {
        DESedeKeySpec dks = new DESedeKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance(KEY_ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(dks);

        return secretKey;
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        byte[] temp = cipher.doFinal(data);
        return temp;
    }

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    public static byte[] DECRYPT3DES(String key, String data) {
        try {
            byte[] km = new byte[24];
            System.arraycopy(StringUtil.hexStringToByteArray(key), 0, km, 0, 16);
            System.out.println(km.length);
            System.arraycopy(StringUtil.hexStringToByteArray(key), 0, km, 16, 8);
            System.out.println(km.length);
            Cipher cipher = Cipher.getInstance("DESede/ECB/NoPadding");
            DESedeKeySpec dks = new DESedeKeySpec(km);

            SecretKey k = SecretKeyFactory.getInstance("DESede")
                    .generateSecret(dks);
            cipher.init(Cipher.DECRYPT_MODE, k);
            byte[] result = cipher.doFinal(StringUtil.hexStringToByteArray(data));
            System.out.println(StringUtil.hexStringToByteArray(data).length);
            System.out.println(result.length);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("" + e);
        }
    }
}
