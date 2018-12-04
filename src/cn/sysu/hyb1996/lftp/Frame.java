package cn.sysu.hyb1996.lftp;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Created by Stardust on 2018/12/5.
 */
public class Frame {

    public static final int MAX_SIZE = 1024;

    public final Metadata metadata;
    public final byte[] body;

    public Frame(@NotNull Metadata metadata, byte[] body) {
        this.metadata = metadata;
        this.body = body;
    }

    public DatagramPacket toPacket() throws IOException {
        byte[] bytes = new byte[metadata.byteLength() + bodyLength()];
        metadata.writeTo(bytes, 0);
        if (body != null) {
            System.arraycopy(body, 0, bytes, metadata.byteLength(), body.length);
        }
        return new DatagramPacket(bytes, bytes.length);
    }

    public int bodyLength() {
        return ((body == null) ? 0 : body.length);
    }

    public static Frame fromPacket(DatagramPacket packet) throws IOException {
        Metadata metadata = Metadata.fromBytes(packet.getData(), packet.getOffset());
        int bodyLength = packet.getLength() - metadata.byteLength();
        if (bodyLength == 0) {
            return new Frame(metadata, null);
        }
        byte[] body = new byte[bodyLength];
        System.arraycopy(packet.getData(), packet.getOffset() + metadata.byteLength(), body, 0, body.length);
        return new Frame(metadata, body);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "metadata=" + metadata +
                ", body.length=" + bodyLength() +
                '}';
    }
}
