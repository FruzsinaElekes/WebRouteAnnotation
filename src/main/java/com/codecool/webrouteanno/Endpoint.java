package com.codecool.webrouteanno;

/***
 * Parameters in URL-s have to be marked by <s>
 * The 1st parameter may be preceded by multiple url components
 * All subsequent parameters have to be preceded by a single url component
 * VALID: some/page/<s>/of/<s>  or  some/<s>/page/<s>/edit
 * INVALID: some/<s>/<s>/page   or  <s>/page    or some/<s>/page/edit/<s>
 *
 * Arguments in handler functions of parametrized URL-s are received as varargs
 */
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
    @WebRoute(path="/profile/edit")
    public String getProfileEdit(){
        return "this is the profile edit endpoint";
    }

    @WebRoute(path="/profile/<s>")
    public String getProfileForUser(String ... params){
        return String.format("this is the profile endpoint for %s", params[0]);
    }

    @WebRoute(path="/profile/<s>/page/<s>")
    public String getProfileForUserPage(String ... params){
        System.out.println("in getprofileforuserpage method");
        return String.format("this is the profile endpoint per page something");
    }
}
