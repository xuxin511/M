package com.xx.chinetek.method.Log;

/**
 * Created by GHOST on 2018/12/19.
 */

public class GpsImagePackage {
    public GpsImagePackage() {
        // TODO Auto-generated constructor stub
    }

    //封装字节数组与参数
    public static byte[] getPacket(String json,byte[] image){

        byte[] jsonb = json.getBytes();
        int length = image.length + jsonb.length;
        System.out.println(image.length +"    "+ jsonb.length);
        byte[] bytes = new byte[length+1];
        byte[] lengthb = InttoByteArray(jsonb.length, 1);
        System.arraycopy(lengthb, 0, bytes, 0, 1);
        System.arraycopy(jsonb, 0, bytes, 1, jsonb.length);
        System.arraycopy(image, 0, bytes, 1+jsonb.length, image.length);
        return bytes;

    }

    //将int转换为字节数组
    public static byte[] InttoByteArray(int iSource, int iArrayLen) {

        byte[] bLocalArr = new byte[iArrayLen];
        for ( int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte)( iSource>>8*i & 0xFF );
        }
        return bLocalArr;
    }

    // 将byte数组bRefArr转为一个整数,字节数组的低位是整型的低字节位
    public static int BytestoInt(byte[] bRefArr) {

        int iOutcome = 0;
        byte bLoop;
        for ( int i =0; i<bRefArr.length ; i++) {
            bLoop = bRefArr[i];
            iOutcome+= (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }
}
