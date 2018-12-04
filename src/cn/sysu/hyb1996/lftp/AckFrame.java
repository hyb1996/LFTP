package cn.sysu.hyb1996.lftp;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Created by Stardust on 2018/12/5.
 */
public class AckFrame extends Frame {

    public AckFrame(Metadata metadata) {
        super(metadata, null);
    }

    public static AckFrame fromPacket(DatagramPacket packet) throws IOException {
        Metadata metadata = Metadata.fromBytes(packet.getData(), packet.getOffset());
        return new AckFrame(metadata);
    }

    public static AckFrame fromFrame(Frame frame) {
        Metadata metadata = new Metadata(frame.metadata.seq);
        metadata.time = frame.metadata.time;
        return new AckFrame(metadata);
    }

}
