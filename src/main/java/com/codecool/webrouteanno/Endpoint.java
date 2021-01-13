package com.codecool.webrouteanno;

public class Endpoint {

    @WebRoute()
    public String getIndex(){
        return "Index";
    }

    @WebRoute(path="/test")
    public String getTest(){
        return "This is the get test route";
    }

    @WebRoute(path="/test", method="POST")
    public String postTest(){
        return "This is the post test route";
    }

    @WebRoute(path="/profile")
    public String getProfile(){
        return "this is the profile endpoint";
    }

    @WebRoute(path="/profile/<s>")
    public String getProfileForUser(String userName){
        return String.format("this is the profile endpoint for %s", userName);
    }
}
