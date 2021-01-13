package com.codecool.webrouteanno;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.reflect.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Server {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new RouteHandler());
        server.createContext("/test", new RouteHandler());
        server.createContext("/profile", new RouteHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class RouteHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestedPath = httpExchange.getRequestURI().toString();
            String requestedMethod = httpExchange.getRequestMethod();
            String responseString = "";
            Class endpClass = Endpoint.class;

            Method[] methods = endpClass.getDeclaredMethods(); // doesn't work with getMethods
            for (Method method : methods){
                WebRoute annotation = (WebRoute) method.getAnnotation(WebRoute.class);

                if (requestedPath.equals(annotation.path()) && requestedMethod.equals(annotation.method())){
                    try {
                        responseString = (String) method.invoke(endpClass.newInstance());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
            }
            httpExchange.sendResponseHeaders(200, responseString.getBytes().length);
            OutputStream rb = httpExchange.getResponseBody();
            rb.write(responseString.getBytes());
            rb.close();
        }
    }



}
