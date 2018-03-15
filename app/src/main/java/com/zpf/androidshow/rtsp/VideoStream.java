/*
 * Copyright (C) 2011-2014 GUIGUI Simon, fyhertz@gmail.com
 *
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 *
 * Spydroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.zpf.androidshow.rtsp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import java.io.IOException;

/**
 * Don't use this class directly.
 */
public abstract class VideoStream extends MediaStream {

	protected final static String TAG = "VideoStream";
	protected SharedPreferences mSettings = null;

	/**
	 * Don't use this class directly.
	 * Uses CAMERA_FACING_BACK by default.
	 */
	public VideoStream() {

	}

	/**
	 * Some data (SPS and PPS params) needs to be stored when {@link #getSessionDescription()} is called
	 * @param prefs The SharedPreferences that will be used to save SPS and PPS parameters
	 */
	public void setPreferences(SharedPreferences prefs) {
		mSettings = prefs;
	}

	/**
	 * Configures the stream. You need to call this before calling {@link #getSessionDescription()}
	 * to apply your configuration of the stream.
	 */
	public synchronized void configure() throws IllegalStateException, IOException {
		super.configure();
	}


	public synchronized void start() throws IllegalStateException, IOException {
		super.start();
	}

	/** Stops the stream. */
	public synchronized void stop() {

	}

	/**
	 * Video encoding is done by a MediaRecorder.
	 */
	protected void encodeWithMediaRecorder() throws IOException {



	}


	/**
	 * Video encoding is done by a MediaCodec.
	 */
	protected void encodeWithMediaCodec() throws RuntimeException, IOException {
        // The packetizer encapsulates the bit stream in an RTP stream and send it over the network
        mPacketizer.setDestination(mDestination, mRtpPort, mRtcpPort);
        mPacketizer.setInputStream(new ScreenInputStream());
        mPacketizer.start();

        mStreaming = true;
	}

	/**
	 * Video encoding is done by a MediaCodec.
	 */
	@SuppressLint("NewApi")
	protected void encodeWithMediaCodecMethod1() throws RuntimeException, IOException {



	}

	/**
	 * Video encoding is done by a MediaCodec.
	 * But here we will use the buffer-to-surface methode
	 */
	@SuppressLint({ "InlinedApi", "NewApi" })
	protected void encodeWithMediaCodecMethod2() throws RuntimeException, IOException {


	}

	/**
	 * Returns a description of the stream using SDP.
	 * This method can only be called after {@link Stream#configure()}.
	 * @throws IllegalStateException Thrown when {@link Stream#configure()} wa not called.
	 */
	public abstract String getSessionDescription() throws IllegalStateException;

}
