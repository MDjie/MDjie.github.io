package src.ResponsibiltyLinkPattern;

public class AcessAuthenticator extends Authenticator{
    @Override
    protected void authenticate(Request request) {
        if (request.permission){
            if(next!=null)
                next.authenticate(request);
            else
                System.out.println(" the request is forward to the functional handler");
        }
        else {
            System.out.println("402: unallowed request");
        }
    }
}
