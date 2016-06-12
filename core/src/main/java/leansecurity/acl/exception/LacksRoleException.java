package leansecurity.acl.exception;

/**
 * Created by sam on 24/03/16.
 */
public class LacksRoleException extends AccessDeniedException {
    public LacksRoleException(String message) {
        super(message);
    }
}
