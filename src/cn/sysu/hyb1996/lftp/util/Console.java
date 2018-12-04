package cn.sysu.hyb1996.lftp.util;

/**
 * Created by Stardust on 2018/12/6.
 */
public class Console {

    public static void serverLog(String message, Object... args) {
        System.out.println(String.format("[Server] " + message, args));
    }

    public static void clientLog(String message, Object... args) {
        System.out.println(String.format("[Client] " + message, args));
    }

    public static void log(String message, Object... args) {
        System.out.println(String.format(message, args));
    }
}
