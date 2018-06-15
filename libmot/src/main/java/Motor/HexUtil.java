package Motor;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/12/15.
 */

public class HexUtil
{
    public static byte[] GetBytesFromString(String sData)
    {
        byte[] bdata = new byte[sData.length()];
        for (int i = 0; i < sData.length(); i++)
        {
            char ch = sData.charAt(i);
            bdata[i] = (byte) ch;
        }
        return bdata;
    }

    public static final String byte2hex(byte b[])
    {
        if (b == null)
        {
            return "";
        }
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++)
        {
            stmp = Integer.toHexString(b[n] & 0xff);
            if (stmp.length() == 1)
            {
                hs = hs + "0" + stmp;
            }
            else
            {
                hs = hs + stmp;
            }
            hs += " ";
        }
        return hs.toUpperCase();
    }

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data)
    {
        return encodeHex(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase)
    {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits)
    {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++)
        {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data)
    {
        return encodeHexStr(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase)
    {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits)
    {
        return new String(encodeHex(data, toDigits));
    }

    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data 十六进制char[]
     * @return byte[]
     * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
     */
    public static byte[] decodeHex(char[] data)
    {
        int len = data.length;
        if ((len & 0x01) != 0)
        {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++)
        {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
     */
    protected static int toDigit(char ch, int index)
    {
        int digit = Character.digit(ch, 16);
        if (digit == -1)
        {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

    public static void main(String[] args)
    {
        String srcStr = "51 A5 01 01 03 E3 01 01 9D 51 3A";
//        String encodeStr = encodeHexStr(srcStr.getBytes());
//        String decodeStr = new String();
//        System.out.println("转换前：" + srcStr);
//        System.out.println("转换后：" + decodeHex(srcStr.toCharArray()));
//        System.out.println("还原后：" + decodeStr);
    }


    //转换16进制
    public static String changeHex(int num)
    {

        String s = Integer.toHexString(num);
        if (Integer.valueOf(s) < 10)
        {
            s = "0x0" + s;
        }

        return s;

    }


    /**
     * 十进制位转换byte[]
     *
     * @param
     * @return
     */
    public static byte[] ten2Bytes(int len)
    {

        String s = Integer.toBinaryString(len);//100000000
//        int[] highAndLow = new int[2];
        byte[] bytes = new byte[2];
        //十进制转换高低位16 进制
        if (!TextUtils.isEmpty(s))
        {

            if (s.length() < 16)
            {
                String ste = "";
                for (int i = 0; i < 16 - s.length(); i++)
                {
                    ste = ste + "0";
                }

                String new2jinzhi = ste + s;//十六位
                String substring = new2jinzhi.substring(0, 4);
                String substring2 = new2jinzhi.substring(4, 8);
                String substring3 = new2jinzhi.substring(8, 12);
                String substring4 = new2jinzhi.substring(12, new2jinzhi.length());

                String s1 = Integer.toHexString(Integer.parseInt(substring, 2));
                String s2 = Integer.toHexString(Integer.parseInt(substring2, 2));
                String s3 = Integer.toHexString(Integer.parseInt(substring3, 2));
                String s4 = Integer.toHexString(Integer.parseInt(substring4, 2));

                /// String high = binaryString2hexString(substring) + binaryString2hexString(substring2);
                // String low = binaryString2hexString(substring3) + binaryString2hexString(substring4);

//                    test.setText(new2jinzhi + "\n" + substring + "\n" + substring2);
                byte high = (byte) Integer.parseInt(s1 + s2, 16);
                byte low = (byte) Integer.parseInt(s3 + s4, 16);
              /*  highAndLow[0] = i;
                highAndLow[1] = i1;
*/
                bytes[0] = high;
                bytes[1] = low;


            }
        }
        return bytes;
    }

    public static String hexString2binaryString(String hexString)
    {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++)
        {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }

        return bString;

    }


    public static String getStringfrombytes(byte[] bcmdrec)
    {
        StringBuilder sbr = new StringBuilder();
        if(bcmdrec!=null && bcmdrec.length>0)
        {
            int nbcmdreclen = bcmdrec.length;
            if (nbcmdreclen > 0)
            {
                for(int i=0;i<nbcmdreclen;i++)
                    sbr.append((char)bcmdrec[i]);
            }
        }
        else
            return "";
        String srecstring = String.valueOf(bcmdrec);
        return  srecstring;
    }
}
