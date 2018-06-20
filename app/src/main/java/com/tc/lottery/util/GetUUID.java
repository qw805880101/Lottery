package com.tc.lottery.util;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by 24179 on 2018/6/15.
 */


public class GetUUID {
    /**
     * 获取 UUID
     * @return 返回 UUID 号，如果返回null说明获取UUID失败。
     */
    public static String getUUID(){
        String result = null;
        String cmd = "cat /proc/cpuinfo";
        Vector<String> vector = execRootCmd(cmd);
        for (String string : vector){
            if(string.contains("Serial")){
                result = string.substring(string.indexOf(":")+2);
            }
        }
        return result;
    }

    private static Vector<String> execRootCmd(String cmd) {
        Vector<String> result = new Vector<>();
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.w("result", line);
                result.add(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
