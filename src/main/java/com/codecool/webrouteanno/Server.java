package com.codecool.webrouteanno;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class Server {
    private static final Map<String, Map<String, Method>> methodFinder = new HashMap<>();

    public static void main(String[] args) throws Exception {
        sortMethods(); //fills in methodFinder

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new RouteHandler());
        server.createContext("/test", new RouteHandler());
        server.createContext("/profile", new RouteHandler());
        server.createContext("/profile/edit", new RouteHandler());
        server.setExecutor(null);
        server.start();
    }

    private static class RouteHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String requestedPath = httpExchange.getRequestURI().toString();
            String requestedMethod = httpExchange.getRequestMethod();
            UrlParam data = parseURL(requestedPath);

            String responseString = "";
            try {
                Class endpClass = Endpoint.class;
                Constructor constructor = endpClass.getConstructor(new Class[]{});
                Endpoint endpoint = (Endpoint) constructor.newInstance();
                Method handler = methodFinder.get(data.getRecomposed()).get(requestedMethod);
                String[] paramArray = data.getParams();
                if (paramArray.length > 0) {
                    responseString = (String) handler.invoke(endpoint, new Object[]{paramArray});
                }
                else responseString = (String) handler.invoke(endpoint);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            httpExchange.sendResponseHeaders(200, responseString.getBytes().length);
            OutputStream rb = httpExchange.getResponseBody();
            rb.write(responseString.getBytes());
            rb.close();
        }

        private UrlParam parseURL(String requestedPath) {
            requestedPath = requestedPath.replaceAll("^/", "");
            requestedPath = requestedPath.replaceAll("/$", "");
            String[] URIcomponents = requestedPath.split("/");
            int counter = -1;
            String recomposed = "";
            for (String path : methodFinder.keySet()){
                int c = 0;
                StringBuilder sb = new StringBuilder();
                for (String component : URIcomponents){
                    if (!path.startsWith(sb.toString() + "/" + component)){
                        break;
                    }
                    sb.append("/" + component);
                    c++;
                }
                if (c > counter){
                    counter = c;
                    recomposed = sb.toString();
                }
            }
            List<String> params = new ArrayList<>();
            boolean parameter = true;
            for (int i = counter; i < URIcomponents.length; i++){
                if (parameter){
                    params.add(URIcomponents[i]);
                    recomposed += "/<s>";
                } else {
                    recomposed += "/" + URIcomponents[i];
                }
                parameter = !parameter;
            }
            return new UrlParam(recomposed, params);

        }
    }

    public static void sortMethods(){
        Class endpClass = Endpoint.class;
        Method[] methods = endpClass.getDeclaredMethods();
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
