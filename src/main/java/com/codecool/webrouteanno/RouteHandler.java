package com.codecool.webrouteanno;

import com.codecool.webrouteanno.util.UrlParam;
import com.codecool.webrouteanno.util.UrlParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class RouteHandler implements HttpHandler {
    UrlParser urlParser;
    Map<String, Map<String, Method>> methodFinder;
    Endpoint endpoint;

    public RouteHandler(UrlParser urlParser, Map<String, Map<String, Method>> methodFinder, Endpoint endpoint) {
        this.urlParser = urlParser;
        this.methodFinder = methodFinder;
        this.endpoint = endpoint;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestedPath = httpExchange.getRequestURI().toString();
        String requestedMethod = httpExchange.getRequestMethod();
        UrlParam data = urlParser.parseURL(requestedPath);

        String responseString = "";
        try {
            Method handler = methodFinder.get(data.getRecomposed()).get(requestedMethod);
            String[] paramArray = data.getParams();
            if (paramArray.length > 0) {
                responseString = (String) handler.invoke(endpoint, new Object[]{paramArray});
            } else responseString = (String) handler.invoke(endpoint);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        httpExchange.sendResponseHeaders(200, responseString.getBytes().length);
        OutputStream rb = httpExchange.getResponseBody();
        rb.write(responseString.getBytes());
        rb.close();
    }

}

