package com.odong.fly.camera;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.SeekableByteChannel;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-10-3
 * Time: 下午5:40
 */
public class MP4Encoder {

    public MP4Encoder(String file, int rate) throws IOException {
        channel = NIOUtils.writableFileChannel(file);
        // Transform to convert between RGB and YUV
        transform = new RgbToYuv420(0, 0);

        // Muxer that will store the encoded frames
        muxer = new MP4Muxer(channel, Brand.MP4);

        // Add video track to muxer
        outTrack = muxer.addTrackForCompressed(TrackType.VIDEO, rate);

        // Allocate a buffer big enough to hold output frames
        out = ByteBuffer.allocate(1920 * 1080 * 6);

        // Create an instance of encoder
        encoder = new H264Encoder();

        // Encoder extra data ( SPS, PPS ) to be stored in a special place of
        // MP4
        spsList = new ArrayList<ByteBuffer>();
        ppsList = new ArrayList<ByteBuffer>();
    }

    public void addImage(BufferedImage img) throws IOException {
        if (toEncode == null) {
            toEncode = Picture.create(img.getWidth(), img.getHeight(), ColorSpace.YUV420);
        }

        // Perform conversion
        for (int i = 0; i < 3; i++)
            Arrays.fill(toEncode.getData()[i], 0);
        transform.transform(AWTUtil.fromBufferedImage(img), toEncode);

        // Encode image into H.264 frame, the result is stored in 'out' buffer
        out.clear();
        ByteBuffer result = encoder.encodeFrame(out, toEncode);

        // Based on the frame above form correct MP4 packet
        spsList.clear();
        ppsList.clear();
        H264Utils.encodeMOVPacket(result, spsList, ppsList);

        // Add packet to video track
        outTrack.addFrame(new MP4Packet(result, frameNo, 25, 1, frameNo, true, null, frameNo, 0));

        frameNo++;
    }

    public void close() throws IOException {
        // Push saved SPS/PPS to a special storage in MP4
        outTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

        // Write MP4 header and finalize recording
        muxer.writeHeader();
        NIOUtils.closeQuietly(channel);
    }

    private SeekableByteChannel channel;
    private Picture toEncode;
    private RgbToYuv420 transform;
    private H264Encoder encoder;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private FramesMP4MuxerTrack outTrack;
    private ByteBuffer out;
    private int frameNo;
    private MP4Muxer muxer;
}
