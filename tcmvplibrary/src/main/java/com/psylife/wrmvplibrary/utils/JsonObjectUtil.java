package com.psylife.wrmvplibrary.utils;

import java.io.File;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonObjectUtil {

    private static ObjectMapper objectMapper = null;

    private static ObjectMapper getInstance() {
	if (objectMapper == null) {
	    synchronized (JsonObjectUtil.class) {
		if (objectMapper == null) {
		    objectMapper = new ObjectMapper();
		    // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		    // 禁止使用int代表Enum的order()來反序列化Enum
		    objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		    objectMapper.setSerializationInclusion(Include.NON_NULL);
		}
	    }
	}
	return objectMapper;
    }

    /**
     * 对象转json
     * 
     * @param obj
     * @return
     */
    public static String BeanToJson(Object obj) throws Exception {
	return getInstance().writeValueAsString(obj);
    }

    /**
     * json转对象
     * 
     * @param json
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T JsonToBean(String json, Class<T> clazz) throws Exception {
	return getInstance().readValue(json, clazz);
    }

    /**
     * json转List，Map
     * 
     * @param json
     * @param javaType
     * @return
     * @throws Exception
     */
    public static <T> T JsonToBean(String json, JavaType javaType) throws Exception {
	return getInstance().readValue(json, javaType);
    }

    /**
     * 获取参数类型 如果为List<Object>则传 ArrayList.class,Object.class
     * 如果为Map(Object1,Object2)则传HashMap.class,Object1.class,Object2.class
     * 
     * @param collectionClass
     * @param elementClasses
     * @return
     */
    public static JavaType getJavaType(Class<?> collectionClass, Class<?>... elementClasses) {
	return getInstance().getTypeFactory().constructParametrizedType(collectionClass, collectionClass,
		elementClasses);
    }

    /**
     * josn转node
     * 
     * @param json
     * @return
     * @throws Exception
     */
    public static JsonNode JsonToNode(String json) throws Exception {
	return getInstance().readTree(json);
    }

    /**
     * json写入json文件
     * 
     * @param filePath
     * @param json
     * @throws Exception
     */
    public static void JsonToFile(String filePath, String json) throws Exception {
	getInstance().writeValue(new File(filePath), json);
    }

    /**
     * 对象写入json文件
     * 
     * @param filePath
     * @param json
     * @throws Exception
     */
    public static void JsonToFile(String filePath, Object obj) throws Exception {
	getInstance().writeValue(new File(filePath), obj);
    }

    /**
     * json文件转node
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public static JsonNode FileToBean(File file) throws Exception {
	return getInstance().readTree(file);
    }

    /**
     * json文件转对象
     * 
     * @param file
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T FileToBean(File file, Class<T> clazz) throws Exception {
	return getInstance().readValue(file, clazz);
    }

    /**
     * json文件转对象 List Map
     * 
     * @param file
     * @param javaType
     * @return
     * @throws Exception
     */
    public static <T> T FileToBean(File file, JavaType javaType) throws Exception {
	return getInstance().readValue(file, javaType);
    }

    /**
     * 
     * muzi 2016年4月20日 下午6:05:18
     * 
     * @param json
     * @param key
     * @return
     * @throws Exception
     */
    public static String JsonToValue(String json, String key) throws Exception {
	JsonNode node = getInstance().readTree(json);
	return node.get(key).textValue();
    }

    public static void main(String[] args) throws Exception {
//	String i = " {\"listPic\": \"4042458.jpg\",\"rollPic\": [{\"url\": \"4042458.jpg\"},{\"url\": \"4042458.jpg\"},{\"url\": \"4042458.jpg\"}], \"detailPic\": \"4042458.jpg\"}";
//	GoodsInfoPic rollPics = JsonObjectUtil.JsonToBean(i, GoodsInfoPic.class);
//	RollPic[] rollPic = rollPics.getRollPic();
//	for (int j = 0; j < rollPic.length; j++) {
//	    System.out.println(rollPic[j].getUrl());
//	    rollPic[j].setUrl(AliyunPicUtil.RED_PIC_URL + rollPic[j].getUrl());
//	}
//	// rollPics.setRollPic(rollPic);
//	System.out.println(JsonObjectUtil.BeanToJson(rollPic));
	
    }

}
