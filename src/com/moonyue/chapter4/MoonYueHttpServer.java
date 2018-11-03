package com.moonyue.chapter4;

import com.moonyue.chapter4.threadPool.ThreadPool;
import com.moonyue.chapter4.threadPool.impl.MoonYueThreadPool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class MoonYueHttpServer {
    private static ThreadPool<HttpRequestHandler> threadPool = new MoonYueThreadPool<>(1);
    private static String basePath;
    private static ServerSocket serverSocket;

    private static int port = 5200;

    public static void setPort(int port){
        if(port > 0){
            MoonYueHttpServer.port = port;
        }
    }

    public static void setBasePath(String basePath){
        if(Objects.nonNull(basePath) && new File(basePath).exists() && new File(basePath).isDirectory()){
            MoonYueHttpServer.basePath = basePath;
        }
    }

    public static void start() throws  Exception{
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while ((socket = serverSocket.accept()) != null){
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }
    static class HttpRequestHandler implements Runnable{
        private Socket socket;

        public HttpRequestHandler(Socket socket){
            this.socket = socket;
        }
        @Override
        public void run() {
            String line = null;
            BufferedReader br = null;
            BufferedReader reader = null;
            PrintWriter out = null;
            InputStream in = null;

            try{
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String header = reader.readLine();
                String filePath = basePath + header.split(" ")[1];
                out = new PrintWriter(socket.getOutputStream());
                if(filePath.endsWith("jpg") || filePath.endsWith("png")){
                    in = new FileInputStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int i = 0;
                    while((i = in.read()) != -1){
                        baos.write(i);
                    }
                    byte[] array = baos.toByteArray();
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: MoonYue");
                    out.println("Content-Type: image/jpeg");
                    out.println("Content-Length: " + array.length);
                    out.println("");
                    socket.getOutputStream().write(array, 0, array.length);
                }else{
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
                    out = new PrintWriter(socket.getOutputStream());
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: MoonYue");
                    out.println("Content-Type: text/html; charset=UTF-8");
                    out.println("");
                    while ((line = br.readLine()) != null){
                        out.println(line);
                    }
                }
                out.flush();
            }catch (Exception e){
                out.println("HTTP/1.1 500");
                out.println("");
                out.flush();
            }finally {
                close(br, in, reader, out, socket);
            }
        }
    }

    private static void close(Closeable... closeables){
        if(closeables != null){
            for(Closeable closeable : closeables){
                try {
                    closeable.close();
                }catch (Exception e){

                }
            }
        }
    }
}
