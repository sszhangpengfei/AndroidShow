package com.zpf.androidshow.media;

import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by zpf on 2018/3/8.
 */

public class H264Helper {

    /**
     * 获取h264帧类型
     * I 0x05
     * NAL_SLICE 0x01
     * sps 0x07
     * pps 0x08
     *
     * **/
    public static int getFrameType(Byte b){
        if(b == null)
            return 1;
        return b & 0x1F;
    }


    public static byte[] generateAVCDecoderConfigurationRecord(MediaFormat mediaFormat) {
        ByteBuffer SPSByteBuff = mediaFormat.getByteBuffer("csd-0");
        SPSByteBuff.position(4);
        ByteBuffer PPSByteBuff = mediaFormat.getByteBuffer("csd-1");
        PPSByteBuff.position(4);
        int spslength = SPSByteBuff.remaining();
        int ppslength = PPSByteBuff.remaining();
        int length = 11 + spslength + ppslength;
        byte[] result = new byte[length];
        SPSByteBuff.get(result, 8, spslength);
        PPSByteBuff.get(result, 8 + spslength + 3, ppslength);
        /**
         * UB[8]configurationVersion
         * UB[8]AVCProfileIndication
         * UB[8]profile_compatibility
         * UB[8]AVCLevelIndication
         * UB[8]lengthSizeMinusOne
         */
        result[0] = 0x01;
        result[1] = result[9];
        result[2] = result[10];
        result[3] = result[11];
        result[4] = (byte) 0xFF;
        /**
         * UB[8]numOfSequenceParameterSets
         * UB[16]sequenceParameterSetLength
         */
        result[5] = (byte) 0xE1;
        intToByteArrayTwoByte(result, 6, spslength);
        /**
         * UB[8]numOfPictureParameterSets
         * UB[16]pictureParameterSetLength
         */
        int pos = 8 + spslength;
        result[pos] = (byte) 0x01;
        intToByteArrayTwoByte(result, pos + 1, ppslength);

        return result;
    }

    public static void intToByteArrayFull(byte[] dst, int pos, int interger) {
        dst[pos] = (byte) ((interger >> 24) & 0xFF);
        dst[pos + 1] = (byte) ((interger >> 16) & 0xFF);
        dst[pos + 2] = (byte) ((interger >> 8) & 0xFF);
        dst[pos + 3] = (byte) ((interger) & 0xFF);
    }

    public static void intToByteArrayTwoByte(byte[] dst, int pos, int interger) {
        dst[pos] = (byte) ((interger >> 8) & 0xFF);
        dst[pos + 1] = (byte) ((interger) & 0xFF);
    }


}
