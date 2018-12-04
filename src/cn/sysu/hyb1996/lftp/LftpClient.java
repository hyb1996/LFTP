package cn.sysu.hyb1996.lftp;

import cn.sysu.hyb1996.lftp.util.Console;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Stardust on 2018/12/6.
 */
public class LftpClient {

    private final DatagramSocket mSocket;


    public LftpClient() throws SocketException {
        mSocket = new DatagramSocket();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        download(new File("D:\\迅雷下载\\4-160421135125.rar"), new File("D:\\2.rar"));
        download(new File("D:\\1.txt"), new File("D:\\2.txt"));
    }

    public void downloadFile(File remoteFile, File localFile) throws IOException, InterruptedException {
        Console.clientLog("download %s to %s", remoteFile, localFile);
        MessageFrame messageFrame = new MessageFrame(remoteFile.getPath());
        DatagramPacket packet = messageFrame.toPacket();
        packet.setPort(LftpServer.PORT);
        packet.setAddress(InetAddress.getLocalHost());
        mSocket.send(packet);
        Lftp lftp = new Lftp(new DatagramSocketWrapper(mSocket));
        lftp.receive(localFile, packet.getSocketAddress());
    }

    private static void download(File remoteFile, File localFile) {
        new Thread(() -> {
            try {
                new LftpClient().downloadFile(remoteFile, localFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
