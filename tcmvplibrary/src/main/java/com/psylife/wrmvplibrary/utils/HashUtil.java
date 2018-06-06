package com.psylife.wrmvplibrary.utils;

import android.annotation.TargetApi;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Locale;

public class HashUtil {
    public HashUtil() {
    }

    public static class MD5 {
        private static final String a = HashUtil.MD5.class.getSimpleName();

        public MD5() {
        }

        @TargetApi(9)
        public static String md5(String message) throws HashUtil.MD5.Md5EncodingException {
            try {
                MessageDigest var1 = MessageDigest.getInstance("MD5");
                var1.update(message.getBytes(Charset.forName("UTF-8")));
                byte[] var2 = var1.digest();
                StringBuffer var3 = new StringBuffer();

                for(int var4 = 0; var4 < var2.length; ++var4) {
                    var3.append(Integer.toHexString(var2[var4] & 255 | 256).substring(1, 3));
                }

                return var3.toString();
            } catch (Exception var5) {
                throw new HashUtil.MD5.Md5EncodingException("Cannot generate MD5 hash string.");
            }
        }

        public static class Md5EncodingException extends Exception {
            public Md5EncodingException() {
            }

            public Md5EncodingException(String msg) {
                super(msg);
            }
        }
    }

    public static class Hex {
        private static final char[] a = "0123456789ABCDEF".toCharArray();

        public Hex() {
        }

        public static String encode(byte[] bytes) {
            char[] var1 = new char[2 * bytes.length];
            int var2 = 0;

            for(int var3 = 0; var3 < bytes.length; ++var3) {
                byte var4 = bytes[var3];
                var1[var2++] = a[var4 >>> 4 & 15];
                var1[var2++] = a[var4 & 15];
            }

            return new String(var1);
        }

        public static byte[] decode(String hexStr) {
            hexStr = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
            char[] var1 = hexStr.toCharArray();
            byte[] var2 = new byte[hexStr.length() / 2];
            boolean var3 = false;

            for(int var4 = 0; var4 < var2.length; ++var4) {
                int var5 = "0123456789ABCDEF".indexOf(var1[2 * var4]) << 4;
                var5 |= "0123456789ABCDEF".indexOf(var1[2 * var4 + 1]);
                var2[var4] = (byte)(var5 & 255);
            }

            return var2;
        }
    }
}
