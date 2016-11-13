package com.jiajunhui.multiscreenhttp_terminal;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Taurus on 2016/11/10.
 */

public class ReceiveUDP {

    public static final int PORT = 9999;
    private static DatagramSocket listenSocket;
    private static DatagramSocket responseSocket;

    public static void listenUDP(OnUDPListener OnUDPListener){
        try{
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            listenSocket = new DatagramSocket(PORT);
            System.out.println("Server started, Listen port: " + PORT);
            while (true) {
                listenSocket.receive(packet);
                //receive data
                String receiveData = new String(packet.getData(), 0, packet.getLength());
//                sendUDP(receiveData + ":" + Build.MODEL,packet.getAddress());
                sendUDP(Build.MODEL,packet.getAddress());
                if(OnUDPListener!=null){
                    //call back to UI
                    OnUDPListener.onReceive(receiveData,packet.getAddress());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sendUDP(String outMessage, InetAddress inetAddress){
        try{
            // Send packet thread
            byte[] buf;
            int packetPort = 8888;
            if (TextUtils.isEmpty(outMessage))
                return;
            buf = outMessage.getBytes();
            Log.d("UDP_Send","Send " + outMessage + " to " + inetAddress);
            // Send packet to hostAddress:9999, server that listen
            // 9999 would reply this packet
            DatagramPacket out = new DatagramPacket(buf,
                    buf.length, inetAddress, packetPort);
            responseSocket = new DatagramSocket(packetPort);
            responseSocket.send(out);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("UDP_Send","---Exception---");
        }
    }

    public static void stopListener(){
        if(listenSocket !=null){
            listenSocket.close();
        }
        if(responseSocket!=null){
            responseSocket.close();
        }
    }

    public interface OnUDPListener{
        void onReceive(String data, InetAddress socketAddress);
    }
}
