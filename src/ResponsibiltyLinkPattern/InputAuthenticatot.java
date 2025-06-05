package src.ResponsibiltyLinkPattern;

public class InputAuthenticatot extends Authenticator {
    @Override
    protected void authenticate(Request request) {
        if (request.input){
            if(next!=null)
                next.authenticate(request);
            else
                System.out.println(" the request is forward to the functional handler");
        }
        else {
            System.out.println("401:bad request with unallowed input");
        }
    }
}

