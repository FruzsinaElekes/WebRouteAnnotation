package com.codecool.webrouteanno.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlParser {
    private static Map<String, Map<String, Method>> methodFinder = new HashMap<>();

    public UrlParser(Map<String, Map<String, Method>> methodFinder) {
        this.methodFinder = methodFinder;
    }

    /***
     * Variables in URL-s have to be marked by <s>
     * The 1st variable may be preceded by multiple url components
     * All subsequent variables have to be preceded by a single url component
     * VALID: some/page/<s>/of/<s>  or  some/<s>/page/<s>/edit
     * INVALID: some/<s>/<s>/page   or  <s>/page    or some/<s>/page/edit/<s>
     *
     * Arguments in handler functions of parametrized URL-s are received as varargs and are type String
     */
    public UrlParam parseURL(String requestedPath) {
        requestedPath = requestedPath.replaceAll("^/", "");
        requestedPath = requestedPath.replaceAll("/$", "");
        String[] URIcomponents = requestedPath.split("/");
        int counter = -1;
        StringBuilder recomposed = new StringBuilder();
        for (String path : methodFinder.keySet()){
            int c = 0;
            StringBuilder sb = new StringBuilder();
            for (String component : URIcomponents){
                if (!path.startsWith(sb.toString() + "/" + component)){
                    break;
                }
                sb.append("/").append(component);
                c++;
            }
            if (c > counter){
                counter = c;
                recomposed = sb;
            }
        }
        List<String> params = new ArrayList<>();
        boolean parameter = true;
        for (int i = counter; i < URIcomponents.length; i++){
            if (parameter){
                params.add(URIcomponents[i]);
                recomposed.append("/<s>");
            } else {
                recomposed.append("/").append(URIcomponents[i]);
            }
            parameter = !parameter;
        }
        return new UrlParam(recomposed.toString(), params);
    }
}

