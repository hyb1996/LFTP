package cn.sysu.hyb1996.lftp;

import cn.sysu.hyb1996.lftp.util.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2018/12/5.
 */
public class FileWriter {

    private int mWritedFrameMaxSeq = -1;
    private FileOutputStream mFileOutputStream;
    private Map<Integer, FileFrame> mFrameCache = new HashMap<>();
    private int mFileSize = 0;

    public FileWriter(File file) throws FileNotFoundException {
        mFileOutputStream = new FileOutputStream(file);
    }

    public void add(FileFrame fileFrame) throws IOException {
        if (fileFrame.metadata.seq <= mWritedFrameMaxSeq) {
            return;
        }
        if (fileFrame.metadata.seq == mWritedFrameMaxSeq + 1) {
            write(fileFrame);
            while ((fileFrame = mFrameCache.get(mWritedFrameMaxSeq + 1)) != null) {
                write(fileFrame);
            }
            return;
        }
        mFrameCache.put(fileFrame.metadata.seq, fileFrame);
    }

    private void write(FileFrame fileFrame) throws IOException {
        mFileOutputStream.write(fileFrame.body);
        mWritedFrameMaxSeq = fileFrame.metadata.seq;
        mFileSize += fileFrame.bodyLength();
        Console.log("write file: size = %d", mFileSize);
    }

    public void close() throws IOException {
        mFileOutputStream.close();
        Console.log("total size = %d", mFileSize);
    }
}
