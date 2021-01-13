package com.codecool.webrouteanno;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Server {
    private static Map<String, Map<String, Method>> methodFinder = new HashMap<>();

    public static void main(String[] args) throws Exception {

        sortMethods(); //fills in methodFinder

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

            String responseString = null;
            try {
                Class endpClass = Endpoint.class;
                Constructor constructor = endpClass.getConstructor(new Class[]{});
                Endpoint endpoint = (Endpoint) constructor.newInstance();
                Method handler = methodFinder.get(requestedPath).get(requestedMethod);
                responseString = (String) handler.invoke(endpoint);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            httpExchange.sendResponseHeaders(200, responseString.getBytes().length);
            OutputStream rb = httpExchange.getResponseBody();
            rb.write(responseString.getBytes());
            rb.close();
        }
    }

    public static void sortMethods(){
        Class endpClass = Endpoint.class;
        Method[] methods = endpClass.getDeclaredMethods(); // doesn't work with getMethods
        for (Method method : methods){
            WebRoute annotation = (WebRoute) method.getAnnotation(WebRoute.class);
            String path = annotation.path();
            String reqMethod = annotation.method();
            Map<String, Method> reqMethodMethod;
            if (methodFinder.get(path) != null){
                reqMethodMethod = methodFinder.get(path);
                reqMethodMethod.put(reqMethod, method);
            }
            else {
                reqMethodMethod = new HashMap<>();
                reqMethodMethod.put(reqMethod, method);
            }
            methodFinder.put(path, reqMethodMethod);
        }
    }

}
