package com.psylife.wrmvplibrary.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SignUtil {

    /**
     * 签名
     *
     * @param contentData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(Map<String, String> contentData, String privateKey) throws Exception {
        String stringSignTemp = coverMap2String(filterBlank(contentData));
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64Class.decode(privateKey, 0));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateK);
        signature.update(stringSignTemp.getBytes("utf-8"));
        return Base64Class.encodeToString(signature.sign(), 0);
    }

    /**
     * 签名
     *
     * @param contentData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(String contentData, String privateKey) throws Exception {
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64Class.decode(privateKey, 0));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateK);
        signature.update(contentData.getBytes("utf-8"));
        return Base64Class.encodeToString(signature.sign(), 0);
    }

    /**
     * 签名验证
     *
     * @param contentData
     * @param publicKey
     * @return
     */
    public static boolean checkSign(Map<String, String> contentData, String publicKey)
            throws Exception {
        String orgSign = contentData.get("sign");
        String stringSignTemp = coverMap2String(filterBlank(contentData));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Class.decode(publicKey, 0));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initVerify(publicK);
        signature.update(stringSignTemp.getBytes("utf-8"));
        return signature.verify(Base64Class.decode(orgSign, 0));
    }

    /**
     * 过滤请求报文中的空字符串或者空字符串
     *
     * @param contentData
     * @return
     */
    public static Map<String, String> filterBlank(Map<String, String> contentData) {
        Map<String, String> submitFromData = new HashMap<String, String>();
        for (Entry<String, String> entry : contentData.entrySet()) {
            if (!Utils.isEmpty(entry.getValue())) {
                // 对value值进行去除前后空处理
                submitFromData.put(entry.getKey(), entry.getValue());
            }
        }
        return submitFromData;
    }

    /**
     * 将Map中的数据转换成按照Key的ascii码排序后的key1=value1&key2=value2的形式 不包含签名域signature
     *
     * @param data 待拼接的Map数据
     * @return 拼接好后的字符串
     */
    public static String coverMap2String(Map<String, String> data) {
        TreeMap<String, String> tree = new TreeMap<String, String>();
        Iterator<Entry<String, String>> it = data.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            if ("sign".equals(en.getKey().trim())) {
                continue;
            }
            tree.put(en.getKey(), en.getValue());
        }
        it = tree.entrySet().iterator();
        StringBuffer sf = new StringBuffer();
        while (it.hasNext()) {
            Entry<String, String> en = it.next();
            sf.append(en.getKey() + "=" + en.getValue() + "&");
        }
        return sf.substring(0, sf.length() - 1);
    }

}
