package com.zpf.androidshow.rtsp;

import android.provider.MediaStore;

import com.zpf.androidshow.media.VideoMediaCodec;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by user111 on 2018/3/14.
 */

public class ScreenStream extends VideoStream {



    public ScreenStream(){
        mPacketizer = new H264Packetizer();

    }

    public synchronized void start() throws IllegalStateException, IOException {
        if (!mStreaming) {
            super.start();
        }
    }

    /**
     * Configures the stream. You need to call this before calling {@link #getSessionDescription()} to apply
     * your configuration of the stream.
     */
    public synchronized void configure() throws IllegalStateException, IOException {
        super.configure();

    }

    @Override
    public String getSessionDescription() throws IllegalStateException {
        return "m=video "+String.valueOf(getDestinationPorts()[0])+" RTP/AVP 96\r\n" +
                "a=rtpmap:96 H264/90000\r\n" +
                "a=fmtp:96 packetization-mode=1;profile-level-id=000042"+";sprop-parameter-sets="+";\r\n";
    }


}
