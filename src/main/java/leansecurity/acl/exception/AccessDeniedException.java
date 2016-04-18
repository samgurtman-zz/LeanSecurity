package leansecurity.acl.exception;

/**
 * Created by sam on 21/03/16.
 */
public abstract class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String role){
        super("Lacks role: " + role);
    }
}
