package Motor;

/**
 * Created by Administrator on 2017/12/15.
 */

public class CRCUtil
{

    public static byte GetBCC(byte[] bData) {
        byte bcc=bData[0];

        for (int i = 1; i <bData.length; i++) {
            bcc ^=bData[i];
        }

        return bcc;
    }



    public static int Crc8L31(byte[] bData)
    {
        int crcdat =  0xff;
        for (int i=0;i<bData.length;i++)
        {
            crcdat = Crc8Byte( (crcdat ^ bData[i]));
        }
        return   crcdat;
    }

    public static int Crc8Byte(int btItem)
    {
        int crcvlue = 0x00;
        for (int i =0;i<8;i++)
        {
            if((( crcvlue ^ btItem ) & 0x01) == 0x01 )
            {
                crcvlue ^= 0x18;
                crcvlue >>= 0x01;
                crcvlue |= 0x80;
            }
            else
                crcvlue >>= 1;
            btItem >>= 1;
        }
        return crcvlue;
    }
}
