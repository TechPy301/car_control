package com.car.controller;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import android.text.TextUtils;

class NetTool {
    private String locAddress;
    private Runtime run = Runtime.getRuntime();
    private Process proc = null;
    private String ping = "ping -c 1 -w 0.05 " ;
    private int j;
    private String ser_ip = "";
    String server_ret;
    String scan(){
        int i;
        locAddress = getLocAddrIndex();
        for ( i = 0; i < 256; i++) {
            j = i;
            new Thread(){
                public void run() {
                    String p = NetTool.this.ping + locAddress + NetTool.this.j ;
                    String current_ip = locAddress+ NetTool.this.j;
                    try {
                        proc = run.exec(p);
                        int result = proc.waitFor();
                        proc.destroy();
                        if (result == 0) {
                            proc.destroy();
                            if(!current_ip.equals(getLocAddress())){
                                String msg = SendString(current_ip);
                                if(!TextUtils.isEmpty(msg)){
                                    ser_ip = "";
                                    if (TextUtils.equals(msg,"已连接")){
                                        ser_ip = current_ip;
                                    }
                                }
                            }
                        }else {proc.destroy();interrupt();}
                    } catch (IOException | InterruptedException e1) {
                        e1.printStackTrace();
                    } finally {
                        proc.destroy();
                    }
                }
            }.start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ser_ip;
    }
    private String getLocAddrIndex(){

        String str = getLocAddress();

        if(!TextUtils.isEmpty(str)){
            return str.substring(0,str.lastIndexOf(".")+1);
        }

        return null;
    }
    private static String getLocAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }
        return null;
    }
    private String SendString(String ipaddr){
        try {
            int SERVERPORT = 4396;
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipaddr, SERVERPORT),2000);
            OutputStream os = socket.getOutputStream();
            os.write("200".getBytes());
            os.flush();
            InputStream is = socket.getInputStream();
            byte[] buf=new byte[1024];
            server_ret = new String(buf,0,is.read(buf));
            os.close();
            is.close();
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }return server_ret;
    }
}
