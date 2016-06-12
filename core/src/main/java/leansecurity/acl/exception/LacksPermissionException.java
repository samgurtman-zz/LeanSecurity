package leansecurity.acl.exception;

/**
 * Created by sam on 21/03/16.
 */
public class LacksPermissionException extends AccessDeniedException {
    public LacksPermissionException(String resourceType, String resourceId, String permission){
        super("Lacks permission: " + permission + " on " + resourceType + ":" + resourceId);
    }
}
