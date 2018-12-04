package cn.sysu.hyb1996.lftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Stardust on 2018/12/5.
 */
public class FileFrame extends Frame {

    public FileFrame(Metadata metadata, byte[] body) {
        super(metadata, body);
    }

    public static Queue<FileFrame> fromFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[MAX_SIZE];
        Queue<FileFrame> frames = new LinkedList<>();
        int read;
        int seq = 0;
        while (fis.available() > 0) {
            read = fis.read(buffer);
            if (read > 0) {
                byte[] data = new byte[read];
                System.arraycopy(buffer, 0, data, 0, read);
                frames.add(new FileFrame(new Metadata(seq), data));
                seq++;
            }
        }
        return frames;
    }

    public static FileFrame fromPacket(DatagramPacket packet) throws IOException {
        Frame frame = Frame.fromPacket(packet);
        return new FileFrame(frame.metadata, frame.body);
    }

    public static FileFrame eof() {
        Metadata metadata = new Metadata(-1);
        metadata.time = System.currentTimeMillis();
        metadata.eof = true;
        return new FileFrame(metadata, new byte[0]);
    }
}
