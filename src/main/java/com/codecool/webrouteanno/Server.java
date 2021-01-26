package com.codecool.webrouteanno;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.lang.reflect.*;

import com.codecool.webrouteanno.util.UrlParam;
import com.codecool.webrouteanno.util.UrlParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Server {
    private static final Map<String, Map<String, Method>> methodFinder = new HashMap<>();
    private static final Set<String> allUrlEndpoints = new HashSet<>();
    private static Class endpClass;
    private static Endpoint endpoint;

    public static void main(String[] args) throws IOException {
        UrlParser urlParser = new UrlParser(methodFinder);
        endpClass = Endpoint.class;
        try {
            Constructor constructor = endpClass.getConstructor();
            endpoint = (Endpoint) constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

        analyzeAnnotations();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        allUrlEndpoints.forEach(url -> server.createContext(url, new RouteHandler(urlParser, methodFinder, endpoint)));
        server.setExecutor(null);
        server.start();
    }

    /***
     * Identifies all paths for which context has to be created (allUrlEndpoints)
     * Fills in methodFinder map (1st key full-path, 2nd key request method)
     */
    public static void analyzeAnnotations(){
        Method[] methods = endpClass.getDeclaredMethods();

        for (Method method : methods){
            if (method.isAnnotationPresent(WebRoute.class)){
                WebRoute annotation = method.getAnnotation(WebRoute.class);
                String path = annotation.path();
                String reqMethod = annotation.method();
                Map<String, Method> reqMethodToMethod;
                reqMethodToMethod = (methodFinder.get(path) != null)? methodFinder.get(path) : new HashMap<>();
                reqMethodToMethod.put(reqMethod, method);
                methodFinder.put(path, reqMethodToMethod);
                allUrlEndpoints.add(path.contains("<s>")
                        ? path.substring(0, path.indexOf("<s>")).replaceAll("/$", "")
                        : path);
            }
        }
    }


}
