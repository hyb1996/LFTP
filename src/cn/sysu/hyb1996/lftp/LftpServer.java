package cn.sysu.hyb1996.lftp;

import cn.sysu.hyb1996.lftp.util.Console;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2018/12/6.
 */
public class LftpServer {

    public static final int PORT = 1111;
    private ConcurrentHashMap<SocketAddress, DatagramSocketWrapper> mConnections = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        new LftpServer().start();
    }

    public void start() throws IOException {
        DatagramSocket socket = new DatagramSocket(PORT);
        byte[] buffer = new byte[Frame.MAX_SIZE * 2];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        Console.serverLog("server listening at %d", PORT);
        while (true) {
            socket.receive(packet);
            Console.serverLog("receive packet: %s", packet);
            SocketAddress address = packet.getSocketAddress();
            DatagramSocketWrapper connection = mConnections.get(address);
            if (connection == null) {
                createConnection(socket, packet);
            } else {
                connection.onReceive(packet);
            }
        }
    }

    private void createConnection(DatagramSocket socket, DatagramPacket packet) throws IOException {
        DatagramSocketWrapper socketWrapper = new DatagramSocketWrapper(socket);
        socketWrapper.setReceiveFromExternal(true);
        SocketAddress target = packet.getSocketAddress();
        mConnections.put(target, socketWrapper);
        MessageFrame frame = MessageFrame.fromPacket(packet);
        String file = frame.getMessage();
        Console.serverLog("message: %s, from: %s", file, target);
        new Thread(() -> {
            Lftp lftp = new Lftp(socketWrapper);
            try {
                lftp.send(new File(file), target);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mConnections.remove(target);
                Console.serverLog("disconnected: %s", target);
            }
        }).start();
    }

}

