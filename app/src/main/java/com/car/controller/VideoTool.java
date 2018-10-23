package com.car.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

class VideoTool{
    Bitmap ivmap;
    int int_fps;
    void RecImg(String socket_host, int socket_timeout) throws IOException {
        int i;
        byte[] img_byte,ret = new byte[51200];
        StringBuilder img_string = new StringBuilder();
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(socket_host, 4397),socket_timeout);
        InputStream is = socket.getInputStream();
        while((i=is.read(ret,0,ret.length))!=-1){
            img_string.append(new String(ret, 0, i));
        }
        is.close();
        socket.close();
        img_byte = Base64.decode(img_string.toString(),Base64.DEFAULT);
        ivmap =  BitmapFactory.decodeByteArray(img_byte, 0, img_byte.length);
        int_fps++;
    }
}
