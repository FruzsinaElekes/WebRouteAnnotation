package com.codecool.webrouteanno;

public class Endpoint {

    @WebRoute()
    String handleIndexEndpoint(){
        return "Index";
    }

    @WebRoute(path="/test")
    String getTestEndpoint(){
        return "This is the get test route";
    }

    @WebRoute(path="/test", method="POST")
    String postTestEndpoint(){
        return "This is the post test route";
    }

    @WebRoute(path="/profile")
    String handleProfileEndpoint(){
        return "this is the profile endpoint";
    }
}
