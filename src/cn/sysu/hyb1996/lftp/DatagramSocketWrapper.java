package cn.sysu.hyb1996.lftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.*;

/**
 * Created by Stardust on 2018/12/6.
 */
public class DatagramSocketWrapper {
    private final DatagramSocket mSocket;
    private int mSoTimeout;
    private BlockingQueue<DatagramPacket> mDatagramPackets = new LinkedBlockingQueue<>();
    private boolean mReceiveFromExternal = false;
    private byte[] mBuffer = new byte[Frame.MAX_SIZE * 2];
    private DatagramPacket mDatagramPacket = new DatagramPacket(mBuffer, mBuffer.length);


    public DatagramSocketWrapper(DatagramSocket socket) throws SocketException {
        mSocket = socket;
        mSoTimeout = socket.getSoTimeout();
    }

    public void setReceiveFromExternal(boolean receiveFromExternal) {
        mReceiveFromExternal = receiveFromExternal;
    }

    public void send(DatagramPacket packet) throws IOException {
        mSocket.send(packet);
    }

    public void setSoTimeout(int soTimeout) {
        mSoTimeout = soTimeout;
    }

    public DatagramPacket receive() throws InterruptedException, IOException {
        if(mReceiveFromExternal){
            return mDatagramPackets.poll(mSoTimeout, TimeUnit.MILLISECONDS);
        }else {
            mSocket.setSoTimeout(mSoTimeout);
            mSocket.receive(mDatagramPacket);
            return mDatagramPacket;
        }
    }

    public void onReceive(DatagramPacket packet) {
        byte[] data = new byte[packet.getData().length];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, data.length);
        DatagramPacket copy = new DatagramPacket(data, data.length);
        copy.setSocketAddress(packet.getSocketAddress());
        mDatagramPackets.add(copy);
    }
}
