package cn.sysu.hyb1996.lftp;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by Stardust on 2018/12/5.
 */
public class Metadata {
    public final int seq;
    public long time;
    public boolean eof = false;

    public Metadata(int seq) {
        this.seq = seq;
    }

    public int byteLength() {
        return Integer.BYTES + Long.BYTES + Byte.BYTES;
    }

    public void writeTo(byte[] bytes, int offset) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(seq);
        dos.writeLong(time);
        dos.writeBoolean(eof);
        dos.close();
        byte[] output = bos.toByteArray();
        System.arraycopy(output, 0, bytes, offset, output.length);
    }

    public static Metadata fromBytes(byte[] bytes, int offset) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes, offset, bytes.length - offset));
        int seq = dis.readInt();
        Metadata metadata = new Metadata(seq);
        metadata.time = dis.readLong();
        metadata.eof = dis.readBoolean();
        return metadata;
    }

    @Override
    public String toString() {
        if (eof) {
            return "Metadata{EOF}";
        }
        return "Metadata{" +
                "seq=" + seq +
                ", time=" + time + "}";
    }
}
