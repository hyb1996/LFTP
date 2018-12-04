package cn.sysu.hyb1996.lftp;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by Stardust on 2018/12/6.
 */
public class MessageFrame extends Frame {

    private final String mMessage;

    public MessageFrame(Metadata metadata, byte[] body) {
        super(metadata, body);
        mMessage = new String(body);
    }

    public MessageFrame(byte[] body) {
        this(metadata(), body);
    }

    public MessageFrame(String message) {
        super(metadata(), message.getBytes());
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    public static Metadata metadata() {
        Metadata metadata = new Metadata(0);
        metadata.time = System.currentTimeMillis();
        return metadata;
    }

    public static MessageFrame fromPacket(DatagramPacket packet) throws IOException {
        Frame frame = Frame.fromPacket(packet);
        return new MessageFrame(frame.metadata, frame.body);
    }
}
