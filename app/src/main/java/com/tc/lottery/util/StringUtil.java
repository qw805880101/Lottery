package com.tc.lottery.util;


import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * StringUtil.java
 */
public class StringUtil {
    
    public static final int FRONT = 0;

	public static final int BACK = 1;
    
    public static final String HexCode[] = {
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        "A", "B", "C", "D", "E", "F"
    };
    
    public static String byteArrayToHexString(byte b[], int offset, int size) {
        if (b==null) return null;
        String result = "";
        for (int i = offset; i < offset+size; i++)
            result = result + byteToHexString(b[i]);
        return result;
    }
    
    public static String byteArrayToHexString(byte b[]) {
        if (b==null) return null;
        //String result = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++)
        	sb.append(byteToHexString(b[i])) ;
        return sb.toString();
    }
    
    public static byte[] hexStringToByteArrays(String text) {
        if (text==null) return null;
        byte[] result = new byte[text.length()/2];
        for(int i=0; i<result.length; ++i) {
            int x  = Integer.parseInt(text.substring(i*2,i*2+2),16);
            result[i] = x<=127 ? (byte)x : (byte)(x-256);
        }
        return result;
    }
    
    public static String hexStringToString(String hexString, String charSet){
        if (hexString==null) return null;
        String result = "";
        try {
            result = new String(hexStringToByteArray(hexString), charSet);
        } catch (Exception ex) {}
        return result;
    }
    
    public static String hexStringToAsciiString(String hexString){
        return hexStringToString(hexString, "ASCII");
    }
    
    public static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return HexCode[d1] + HexCode[d2];
    }
    
    
    /**
	 * ��ָ�����ַ����ָ�����ַ����ﵽָ���ĳ��ȣ����������֮����ַ���<br>
	 * 
	 * @param p_scr
	 *            �������ַ���
	 * @param p_fill
	 *            �����ַ�
	 * @param p_length
	 *            ���֮����ַ����ܳ���
	 * @param direction
	 *            ��䷽��SerialPart.FRONT ǰ�棬SerialPart.BACK����
	 * @return String ���֮����ַ���
	 */
	public static String fill(String p_scr, char p_fill, int p_length,
                              int direction) {
		/* ���������ַ����ĳ��ȵ������֮���ַ����ĳ��ȣ����������ֱ�ӷ��� */
		if (p_scr.length() == p_length) {
			return p_scr;
		}
		/* ��ʼ���ַ����� */
		char[] fill = new char[p_length - p_scr.length()];
		/* ����ַ����� */
		Arrays.fill(fill, p_fill);
		/* ������䷽�򣬽�����ַ�����Դ�ַ�������ƴ�� */
		switch (direction) {
		case FRONT:
			return String.valueOf(fill).concat(p_scr);
		case BACK:
			return p_scr.concat(String.valueOf(fill));
		default:
			return p_scr;
		}
	}
	public static String getMeans(int length, String data) {
		String temp = "";
		if (length > 9) {
			temp = "" + length;
		} else {
			temp = "0" + length;
		}
//		System.out.println("lenght = " + temp);

		if (data.length() < (length << 1)) {
			for (int i = 0; i < ((length << 1) - data.length()); i++) {
				temp += "0";
			}
			temp += data;
		} else {
			temp += data;
		}

//		System.out.println("lenght = " + temp);
		return temp;
	}
	
	 public static byte[] hexStringToByteArray(String digits) {
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        for (int i = 0; i < digits.length(); i += 2) {
	          char c1 = digits.charAt(i);
	          if (i + 1 >= digits.length()) {
	            throw new IllegalArgumentException("hexUtil.odd");
	          }
	          char c2 = digits.charAt(i + 1);
	          byte b = 0;
	          if ((c1 >= '0') && (c1 <= '9'))
	            b = (byte)(b + (c1 - '0') * 16);
	          else if ((c1 >= 'a') && (c1 <= 'f'))
	            b = (byte)(b + (c1 - 'a' + 10) * 16);
	          else if ((c1 >= 'A') && (c1 <= 'F'))
	            b = (byte)(b + (c1 - 'A' + 10) * 16);
	          else
	            throw new IllegalArgumentException("hexUtil.bad");
	          if ((c2 >= '0') && (c2 <= '9'))
	            b = (byte)(b + (c2 - '0'));
	          else if ((c2 >= 'a') && (c2 <= 'f'))
	            b = (byte)(b + (c2 - 'a' + 10));
	          else if ((c2 >= 'A') && (c2 <= 'F'))
	            b = (byte)(b + (c2 - 'A' + 10));
	          else
	            throw new IllegalArgumentException("hexUtil.bad");
	          baos.write(b);
	        }
	        return baos.toByteArray();
	    }

	
}
