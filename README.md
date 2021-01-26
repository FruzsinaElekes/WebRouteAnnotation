This project implements a mini-webserver that uses reflection and annotations to route browser requests to specific 
handler methods.

A custom annotation, called @WebRoute is created that provides information on path (defaults to "/") and Http request method (defaults to "GET"). 
Paths may include variables denoted by \<s>. 

Methods annotated with @WebRoute(path="/path", method="POST") are the HTTP request handlers, 
called whenever a request accepted by the webserver matches the path and request method value in a @WebRoute annotation.

Reflection is used to createContext for necessary paths and to find the right method for incoming requests.