package src.ResponsibiltyLinkPattern;

import lombok.Data;

@Data
public abstract class Authenticator {
    protected Authenticator next;
    protected abstract void authenticate(Request request);
}
