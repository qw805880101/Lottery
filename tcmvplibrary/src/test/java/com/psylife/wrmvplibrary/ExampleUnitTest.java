package com.psylife.wrmvplibrary;

import com.psylife.wrmvplibrary.utils.SignUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        String s = "12";
        String[] array = s.split("");
        System.out.println(array);
    }

    @Test
    public void testSign() throws Exception {

        String sd = "{\"application\":\"terminalInit.Req\",\"version\":\"1.0.0\",\"sendTime\":\"201806159034241\",\"terminalCode\":\"0002\",\"terminalId\":\"10000\",\"respCode\":\"00\",\"respDesc\":\"操作成功\",\"terminalStatus\":\"1\",\"terminalLotteryDtos\":[{\"boxId\":\"1\",\"lotteryId\":\"10\",\"lotteryName\":\"中国红\",\"lotteryImg\":\"http://h.hiphotos.baidu.com/image/pic/item/2cf5e0fe9925bc31b3e6ab0a52df8db1ca1370ed.jpg\",\"lotteryAmt\":\"5\",\"surplus\":\"60\",\"boxStatus\":\"1\"}],\"updateStatus\":\"00\"}";

        String sign = "g8XPLY2nNSzgQWYEMd3vBmzT+BVDMLxsUJXHcDTYVCHKfspLxOMqIArL92T2bU6h41waZ56SVyulF79WK9ra9HtsCPhGpqLuI9Gf6P8HX9M1ongt3w+0wH+wDLYGxnpkzQbnJtmR+vzAK2FSDnR6adFLAPZnhwc5/bxmDcGnocY\\u003d";

        boolean s = SignUtil.checkSign(sd, sign);


    }

}