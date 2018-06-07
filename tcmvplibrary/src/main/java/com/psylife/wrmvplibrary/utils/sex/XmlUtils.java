package com.psylife.wrmvplibrary.utils.sex;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * xml解析
 * Created by tc on 2016/7/20.
 */
public class XmlUtils {

    /**
     * 解析xml文件结构的方法， 返回一个对象
     *
     * @param str       解析内容
     * @param beanRoot  外层Bean需要实例化对象的一个标识
     * @param beanClazz Bean.class
     * @return 返回  object 对象
     * @throws Exception
     */
    public static <T1, T> T getBeanListByParseXml(String str, String beanRoot, Class<T> beanClazz)
            throws Exception {

        XmlPullParser parser = Xml.newPullParser();

        InputStream inputStream = new ByteArrayInputStream(str.getBytes());

        //最后结果
        T result = null;
        //list  存放一堆item
        List<T1> list = null;
        //内层ListBean
        T1 t = null;
        //外层Bean
        T bean = null;
        //一个计数器
        int count = 0;

        Field mField = null; //List

        Field mFieldList = null;

        String listName = "";

        try {
            parser.setInput(inputStream, "UTF-8");
            //获得当前标签类型
            int eventType = parser.getEventType();
            //如果不是xml文件结束标签，则一个一个向下解析
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //如果是xml文件开始标签，则初始化一些数据
                    case XmlPullParser.START_DOCUMENT:
                        //最后的结果
                        result = beanClazz.newInstance();
                        //list
                        list = new ArrayList<>();
                        break;
                    //开始标签
                    case XmlPullParser.START_TAG:
                        //获得标签的名字
                        String tagName = parser.getName();
                        //如果内层的ListBean已经实例化出来的话
                        if (t != null) {
                            try {
                                Field field = t.getClass().getDeclaredField(tagName);
                                if (!listName.equals(tagName)) {
                                    //判断当前标签在没在ListBean的属性中
                                    if (!tagName.equals(listName)) {
                                        //如果ListBean中有当前标签
                                        if (field != null) {
                                            //计数器+1
                                            count++;
                                            //将取出来的值赋给ListBean中对应的属性
                                            field.setAccessible(true);
                                            field.set(t, parser.nextText());
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                //如果ListBean中没有当前标签，则会直接跳到这里，什么都不执行，然后再继续往下走
                            }
                            //如果外层的Bean已经实例化出来的话
                        } else if (bean != null) {
                            try {
                                //判断当前标签在没在Bean的属性中
                                Field field = beanClazz.getDeclaredField(tagName);
                                if (!field.getType().getSimpleName().equals("List")) {
                                    //如果Bean中有当前标签
                                    if (field != null) {
                                        //计数器+1
                                        count++;
                                        //将取出来的值赋给Bean中对应的属性
                                        field.setAccessible(true);
                                        field.set(bean, parser.nextText());
                                    }
                                }
                            } catch (Exception e) {
                                //如果Bean中没有当前标签，则会直接跳到这里，什么都不执行，然后再继续往下走
                            }
                        }

                        try {
                            //判断当前标签类型是否为List
                            mField = beanClazz.getDeclaredField(tagName);
                            mField.setAccessible(true);
                            if (mField.getType().getSimpleName().equals("List")) { //判断标签是否为 一个 List 集合
                                if (!tagName.equals(listName)) { //判断是否为相同集合
                                    try {
                                        //如果不是相同一个集合，设置List值
                                        mFieldList.set(bean, list);
                                        //清空List
                                        list = new ArrayList<>();
                                    } catch (Exception e) {
                                        //防止设置List 空指针异常
                                    }
                                }
                                mFieldList = mField;
                                listName = tagName;
                                //实例化List集合中 对象类型
                                Class cla = (Class) (((ParameterizedType) mField.getGenericType())
                                        .getActualTypeArguments()[0]);
                                t = (T1) cla.newInstance();
                            }
                        } catch (Exception e) {
                        }

                        //如果当前标签为我们传入的内层根标签，说明Bean需要实例化出来了
                        if (tagName.equals(beanRoot)) {
                            //将Bean实例化出来
                            bean = beanClazz.newInstance();
                        }
                        break;
                    //结束标签
                    case XmlPullParser.END_TAG:
                        //如果当前标签为</item>
                        if (listName.equalsIgnoreCase(parser.getName())) {
                            //如果ListBean不为空
                            if (t != null) {
                                //保存到list中，同时也保存到了result中，因为list已经是保存在result中了，
                                //只不过刚才没有值，现在有值了
                                list.add(t);
                                //并且把ListBean置空，因为后续还有好多个item
                                t = null;
                            }
                        } else if (beanRoot.equalsIgnoreCase(parser.getName())) { //最后结束标签
                            try {
                                //设置 list   防止空指针异常
                                mFieldList.set(bean, list);
                            } catch (Exception e) {

                            }
                            //将Bean保存到result中
                            result = bean;
                        }
                        break;
                }
                //移动到下一个标签
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果计数器为0说明没有解析到任何数据
        if (count == 0) {
            //将result置空就可以了
            result = null;
        }
        //将result返回
        return result;
    }
}
