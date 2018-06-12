package Motor;
import static Motor.CRCUtil.Crc8L31;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.HashMap;

//import Tools.LogUtil;
import android_serialport_api.SerialPort;

public class MotorSlaveS32
{
    private SerialPort mSerialPort = null;
    private OutputStream mOutputStream = null;
    private InputStream mInputStream = null;

    private static MotorSlaveS32 motorSlave = null;
    private static int mFID = 0;//0-255

    private MotorSlaveS32()
    {
        try
        {
            //mSerialPort = new SerialPort(new File("/dev/ttyS4"), 19200, 0);//工控板
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 19200, 0);//10cun
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            Log.d("serial","serial port open ok !");
            return;

        } catch (SecurityException e)
        {

        } catch (IOException e)
        {

        } catch (InvalidParameterException e)
        {
        }
        Log.d("serial","serial port open fail !");
    }

    public synchronized static MotorSlaveS32 getInstance()
    {
        if (motorSlave == null)
        {
            motorSlave = new MotorSlaveS32();
        }
        return motorSlave;
    }

    private int GetFID()
    {
        mFID++;
        if (mFID == 256)
            mFID = 0;
        return mFID;
    }

    //销毁,每次执行完毕调用这个方法,或者app退出时调用这个方法关闭
    public void closeSerialPort()
    {
        if (mSerialPort != null)
        {
            mSerialPort.close();
            mSerialPort = null;
        }
        mSerialPort = null;
        motorSlave =  null;
    }


    public static final byte CMD_TicketOut = (byte) 0xE3;
    public static final byte CMD_QueryStatus = (byte) 0xE1;
    public static final byte CMD_Move = (byte) 0xE5;
    public static final byte CMD_MoveBack = (byte) 0xE4;


    private byte[] GetParams(byte cCmd,int nParam1,int nParam2,int nParam3)
    {
        byte[] brets = null;
        switch (cCmd)
        {
            case CMD_TicketOut:
                //byte blenl = (byte)((nParam2<<8) & 0xff);
                byte blenl = (byte)((nParam2) & 0x00ff);
                byte blenh= (byte)((nParam2>>8) & 0x00ff);
                brets = new byte[]{cCmd,(byte)nParam1,blenh,blenl};
                break;
            case CMD_QueryStatus:
                brets = new byte[]{cCmd};
                break;


            default:
                brets = new byte[]{cCmd};
                break;
        }
        return brets;
    }


    //0X51 0xA5 0x01 0x01 4 0xE3 Num TicketLength Crc 0X51 0X3A
    public byte[] GetCommandDataS(int nFID, byte cCmd, int nMotorID, int nParam1,int nParam2,int nParam3)
    {
        byte[] brets = null;
        byte bcrc = (byte) 0xff;
        byte[] bparams = GetParams(cCmd,nParam1,nParam2, nParam3);//(cmd + params )
        int ncmdlen = bparams.length;
        int ncmdstotallen = 8 + ncmdlen;

        byte[] bcmdtotal = new byte[ncmdstotallen];
        brets = new byte[]{(byte) 0x51,(byte) 0xa5, (byte) nMotorID,(byte) nFID, (byte) ncmdlen,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00,  (byte) 0x00};

        System.arraycopy(brets, 0, bcmdtotal, 0, 5);
        System.arraycopy(bparams, 0, bcmdtotal, 5, ncmdlen);
        bcmdtotal[5+ncmdlen] = bcrc;
        bcmdtotal[6+ncmdlen] = (byte)0x51;
        bcmdtotal[7+ncmdlen] = (byte)0x3A;

        byte[] b2bcc = new byte[brets.length - 1];
        System.arraycopy(brets, 0, b2bcc, 2, 3+ncmdlen);
        bcrc = (byte)Crc8L31(b2bcc);
        bcmdtotal[5+ncmdlen] = bcrc;
        return bcmdtotal;
    }


    public boolean CheckResult(byte[] bData)
    {
        boolean bret = false;
        if(bData == null || bData.length<8)
            return false;
        if(!HeadEndCheck(bData))
            return false;
        if(bData[6] == (byte)0x01)
            return true;
        return  bret;
    }
    public boolean CheckQueryResult(byte[] bData)
    {
        boolean bret = false;
        if(bData == null || bData.length<8)
            return false;
        if(!HeadEndCheck(bData))
            return false;
        return  true;
    }
    public  int[] GetStatus(byte[] bData)
    {
        int arr[] = new int[]{0,0,0};//空仓 位置 掉票
        if(bData ==null || bData.length<6)
            return arr;
        byte bStatus= bData[6];
        if((byte)(bStatus&0x01) == 0x01)
            arr[0] = 1;
        if((byte)(bStatus&0x02) == 0x02)
            arr[1] = 1;
        if((byte)(bStatus&0x04) == 0x04)
            arr[2] = 1;
        return arr;
    }


    public static boolean HeadEndCheck(byte[] bData)
    {
        if (bData[0] == (byte) 0x51
                && ((byte)(bData[1]& 0xff) == (byte) 0xa5 )
                && ((byte)( bData[bData.length - 2]& 0xff) ==(byte)0x51 )
                &&  ((byte)(bData[bData.length - 1]& 0xff) == (byte)0x3a))
        {
            return true;
        }
        return false;
    }

    private  byte[] GetValidBytes(int nFID, byte[] bData)
    {
        String shexrecv = HexUtil.byte2hex(bData);
        //Log.i("GetValidBytes", "GetValidBytes:begin  "+shexrecv);
        // 51 a5  addr fid len data crc 51 3a
        if(bData ==null || bData.length<9)
            return null;
        int istart = 0;
        boolean bfind = false;
        int ntotallen = 8;

        for(istart =0;istart<bData.length;istart++)
        {
            if(bData[istart] == (byte) 0x51)
            {
                //Log.i("GetValidBytes", "GetValidBytes:fail a "+String.valueOf(istart));
                if(istart+3<bData.length)
                {
                    //Log.i("GetValidBytes", "GetValidBytes:fail b "+String.valueOf(istart));
                    int ndatlen =  bData[istart+4];
                    ntotallen = ndatlen+8;
                    if(ndatlen>0 && (istart+ntotallen)<bData.length+1)
                    {
                        //              Log.i("GetValidBytes", "GetValidBytes:fail c "+String.valueOf(istart));
                        if( bData[istart] == (byte) 0x51
                                && ((byte)(bData[istart+1]& 0xff) == (byte) 0xa5)
                                && ((bData[istart+3] & 0xff) == nFID)
                                && ((byte)(bData[istart+ntotallen - 2]& 0xff) ==(byte)0x51)
                                && ((byte)(bData[istart+ntotallen - 1]& 0xff) == (byte)0x3a)
                                )
                        {
                            bfind = true;
                            break;
                        }
                        else
                        {
                            Log.i("GetValidBytes", "GetValidBytes:fail a");
                            if( bData[istart] == (byte) 0x51)
                                Log.i("GetValidBytes", "GetValidBytes:fail b");
                            if ((byte)(bData[istart+1]& 0xff) == (byte) 0xa5)
                                Log.i("GetValidBytes", "GetValidBytes:fail c" );
                            if ((bData[istart+3] & 0xff) == nFID)
                                Log.i("GetValidBytes", "GetValidBytes:fail d");
                            if ((byte)(bData[istart+ntotallen - 2]& 0xff) ==(byte)0x51)
                                Log.i("GetValidBytes", "GetValidBytes:fail e");
                            if ((byte)(bData[istart+ntotallen - 1]& 0xff) == (byte)0x3a)
                                Log.i("GetValidBytes", "GetValidBytes:fail f");
                        }
                    }
                }
            }

        }
        if(bfind)
        {
            byte[] bbytesfind = new byte[ntotallen];

            System.arraycopy(bData, istart, bbytesfind, 0, ntotallen);

            return bbytesfind;
        }
        return null;
    }
    private byte[] SendDataS(int nFID, byte[] bData, int nWait_ms)
    {
        if (mSerialPort != null && mOutputStream != null && mInputStream != null)
        {
            try
            {
                mOutputStream.write(bData);
            } catch (SecurityException e)
            {
                return null;
            } catch (IOException e)
            {
                return null;

            } catch (InvalidParameterException e)
            {
                return null;
            }

            try
            {
                byte[] recvData = new byte[128];
                int nRecv = 0;
                int nwait = 0;
                while (nwait++ < nWait_ms / 100)
                {
//                    Log.i("send command ", "SendDataS: nwait "+String.valueOf(nwait)+",nRecv "+String.valueOf(nRecv)+","+HexUtil.byte2hex(recvData));
                    if (mInputStream.available() > 0)
                    {
                        byte[] buffer = new byte[32];
                        int size = mInputStream.read(buffer);
                        if(nRecv+size >127)
                        {
                            String shexrecv = HexUtil.byte2hex(recvData);
                            Log.d("recv_data_inv", shexrecv);
                            break;
                        }
                        if (size > 0)
                        {
                            System.arraycopy(buffer, 0, recvData, nRecv, size);
                            nRecv += size;
                        }

                        byte[] bfind =  GetValidBytes(nFID,recvData);
                        if(bfind!=null)
                        {
                            Log.d("find","return find1");
                            Log.i("data receive ", "data recv: nwait "+String.valueOf(nwait)+",nRecv "+String.valueOf(nRecv)+","+HexUtil.byte2hex(recvData));

                            return bfind;
                            //break;
                        }

                    }
                    else
                    {
                        if (nRecv > 0)//一直读取缓冲区,直到缓冲区为空
                        {
                            byte[] bfind =  GetValidBytes(nFID,recvData);
                            if(bfind!=null)
                            {
                                Log.d("find2", "return find2");
                                return bfind;
                                //break;
                            }
                            else//不匹配 ，丢弃，重新等待读取
                            {
                                String shexrecv = HexUtil.byte2hex(recvData);
                                Log.d("recv_data_inv", shexrecv);

                                recvData = new byte[128];
                                nRecv = 0;
                            }
                        }
                    }

                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (nRecv > 0)
                {
                    byte[] brec = new byte[nRecv];
                    System.arraycopy(recvData, 0, brec, 0, nRecv);
                    return brec;
                }

            } catch (SecurityException e)
            {

            } catch (IOException e)
            {

            } catch (InvalidParameterException e)
            {

            }
            catch (Exception e)
            {
                Log.i("ex:", "SendDataS: "+e.getMessage());
            }
        }
        return null;
    }

    public boolean TransOneSimpleS( int nMid,int nTicketLen, StringBuilder sHexIn, StringBuilder sHexRecv, int num)
    {
        GetFID();
        boolean bret = false;
        try
        {
            byte[] bcmds =  GetCommandDataS(mFID, MotorSlaveS32.CMD_TicketOut, nMid, num, nTicketLen,0);
            byte CMD_MoveBack = (byte) 0xE7;
            sHexIn.append( HexUtil.byte2hex(bcmds));
            //LogUtil.d("cut send：" +sHexIn);
            byte[] bcmdrec =  SendDataS(mFID, bcmds, 15000);
            String shexrec = HexUtil.byte2hex(bcmdrec);
            sHexRecv.append(shexrec);
            //LogUtil.d("cut recv：" +sHexRecv);

            boolean btook =  CheckResult(bcmdrec);
            // LogUtil.d("cut res：" + String.valueOf(btook));

            bret = btook;
        }
        catch (Exception exp)
        {

        }
        finally
        {

        }

        return bret;
    }

    public boolean Move( int nMid, StringBuilder sHexIn, StringBuilder sHexRecv)
    {
        GetFID();
        boolean bret = false;
        try
        {
            byte[] bcmds =  GetCommandDataS(mFID, MotorSlaveS32.CMD_Move, nMid, 0, 0,0);
            sHexIn.append( HexUtil.byte2hex(bcmds));
            //LogUtil.d("move send：" +sHexIn);
            byte[] bcmdrec =  SendDataS(mFID, bcmds, 10000);
            String shexrec = HexUtil.byte2hex(bcmdrec);
            sHexRecv.append(shexrec);
            //LogUtil.d("move recv：" +sHexRecv);

            boolean btook =  CheckQueryResult(bcmdrec);
            //LogUtil.d("move res：" + String.valueOf(btook));

            bret = btook;
        }
        catch (Exception exp)
        {

        }
        finally
        {

        }

        return bret;
    }

    public boolean MoveBack( int nMid, StringBuilder sHexIn, StringBuilder sHexRecv)
    {
        GetFID();
        boolean bret = false;
        try
        {
            byte[] bcmds =  GetCommandDataS(mFID, MotorSlaveS32.CMD_MoveBack, nMid, 0, 0,0);
            sHexIn.append( HexUtil.byte2hex(bcmds));
            //LogUtil.d("moveback send：" +sHexIn);
            byte[] bcmdrec =  SendDataS(mFID, bcmds, 10000);
            String shexrec = HexUtil.byte2hex(bcmdrec);
            sHexRecv.append(shexrec);
            //LogUtil.d("moveback recv：" +sHexRecv);

            boolean btook =  CheckQueryResult(bcmdrec);
            //LogUtil.d("moveback res：" + String.valueOf(btook));

            bret = btook;
        }
        catch (Exception exp)
        {

        }
        finally
        {

        }

        return bret;
    }

    public static final Integer EmptyStatus=0;
    public static final Integer PositionStatus=1;
    public static final Integer OutStatus=2;
    //查询机头状态
    public HashMap<Integer,Boolean> ReadStatus( int nMid, StringBuilder sHexIn, StringBuilder sHexRecv)
    {
        HashMap<Integer,Boolean> hmRet  = new HashMap<Integer,Boolean>();
        GetFID();
        try
        {
            byte[] bcmds =  GetCommandDataS(mFID, MotorSlaveS32.CMD_QueryStatus, nMid, 0, 0,0);
            sHexIn.append( HexUtil.byte2hex(bcmds));
            //LogUtil.d("query send：" +sHexIn);
            byte[] bcmdrec =  SendDataS(mFID, bcmds, 10000);
            String shexrec = HexUtil.byte2hex(bcmdrec);
            sHexRecv.append(shexrec);
            //LogUtil.d("query recv：" +sHexRecv);

            boolean btook =  CheckQueryResult(bcmdrec);
            //LogUtil.d("query res：" + String.valueOf(btook));
            int[] arrstatus = new int[]{0,0,0};//空仓 位置 掉票
            if(btook)
            {
                arrstatus = GetStatus(bcmdrec);
//                LogUtil.d("query status：" + String.valueOf(arrstatus[0])
//                        +","  + String.valueOf(arrstatus[1])
//                        +"," + String.valueOf(arrstatus[2]));
                hmRet.put(EmptyStatus,arrstatus[0]==0?true:false);
                hmRet.put(PositionStatus,arrstatus[1]==0?true:false);
                hmRet.put(OutStatus,arrstatus[2]==0?true:false);
            }

        }
        catch (Exception exp)
        {
            Log.i("ex:", "ReadStatus: "+exp.getMessage());
        }
        finally
        {

        }

        return hmRet;
    }


    //endregion
}

