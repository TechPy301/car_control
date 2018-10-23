package com.car.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

class SendStrTool {
    static String server_ret;
    void SendString(String input, String socket_host, int socket_timeout) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(socket_host, 4396),socket_timeout);
        OutputStream os = socket.getOutputStream();
        os.write(input.getBytes());
        os.flush();
        InputStream is = socket.getInputStream();
        byte[] buf=new byte[1024];
        server_ret = new String(buf,0,is.read(buf));
        os.close();
        is.close();
        socket.close();
    }
}
