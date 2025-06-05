package src.ResponsibiltyLinkPattern;


public class TokenAuthenticator extends Authenticator{
    @Override
    protected void authenticate(Request request) {
        if (request.token){
            if(next!=null)
            next.authenticate(request);
            else
                System.out.println(" the request is forward to the functional handler");
        }
        else {
            System.out.println("403:anonymous request");
        }
    }
}
