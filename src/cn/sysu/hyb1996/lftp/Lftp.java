package cn.sysu.hyb1996.lftp;

import cn.sysu.hyb1996.lftp.util.AverageCalculator;
import cn.sysu.hyb1996.lftp.util.Console;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Stardust on 2018/12/5.
 */
public class Lftp {

    private static final int MIN_TIMEOUT = 10000;
    private final DatagramSocketWrapper mSocket;
    private Map<Integer, Frame> mSendingFrames = new HashMap<>();
    private long mAckReceiveTimeout = 30000;
    private AverageCalculator mAckCalculator = new AverageCalculator();
    int cwnd = 10;
    private int ssthresh = 25;

    public Lftp(DatagramSocketWrapper socket) {
        mSocket = socket;
    }

    public void send(File file, SocketAddress target) throws IOException, InterruptedException {
        Queue<? extends Frame> framesToSend = FileFrame.fromFile(file);
        Console.log("read file: %d frames", framesToSend.size());
        while (!mSendingFrames.isEmpty() || !framesToSend.isEmpty()) {
            if (mSendingFrames.size() < cwnd && !framesToSend.isEmpty()) {
                Frame frame = framesToSend.poll();
                mSendingFrames.put(frame.metadata.seq, frame);
            }
            for (Frame frame : mSendingFrames.values()) {
                send(frame, target);
            }
            receiveAck();
        }
        Console.log("send eof to %s", target);
        send(FileFrame.eof(), target);
    }

    private void send(Frame frame, SocketAddress target) throws IOException {
        frame.metadata.time = System.currentTimeMillis();
        Console.log("send frame %s to %s", frame, target);
        DatagramPacket packet = frame.toPacket();
        packet.setSocketAddress(target);
        mSocket.send(packet);
    }

    private void receiveAck() throws IOException, InterruptedException {
        long time = System.currentTimeMillis();
        if (mAckCalculator.count() > 0) {
            mAckReceiveTimeout = Math.max(MIN_TIMEOUT, mAckCalculator.average());
        }
        Console.log("receiving acks in timeout: %d", mAckReceiveTimeout);
        while (!mSendingFrames.isEmpty() && System.currentTimeMillis() - time <= mAckReceiveTimeout) {
            DatagramPacket packet = receive(mAckReceiveTimeout);
            AckFrame frame = AckFrame.fromPacket(packet);
            onAckReceive(frame);
        }
        if (mSendingFrames.isEmpty()) {
            if (cwnd < ssthresh) {
                cwnd *= 2;
            } else {
                cwnd++;
            }
            Console.log("");
        } else {
            ssthresh = cwnd / 2;
            cwnd = ssthresh + 1;
        }
    }

    private DatagramPacket receive(long timeout) throws IOException, InterruptedException {
        mSocket.setSoTimeout((int) timeout);
        return mSocket.receive();
    }

    private void onAckReceive(AckFrame frame) {
        Console.serverLog("receive ack: %s", frame);
        long ackTime = System.currentTimeMillis() - frame.metadata.time;
        mAckCalculator.update(ackTime);
        mSendingFrames.remove(frame.metadata.seq);
    }


    public void receive(File file, SocketAddress source) throws IOException, InterruptedException {
        FileWriter writer = new FileWriter(file);
        FileFrame fileFrame;
        while (true) {
            DatagramPacket packet = receive(mAckReceiveTimeout);
            fileFrame = FileFrame.fromPacket(packet);
            if (fileFrame.metadata.eof) {
                break;
            }
            AckFrame ackFrame = AckFrame.fromFrame(fileFrame);
            send(ackFrame, source);
            writer.add(fileFrame);
        }
        writer.close();
    }
}
