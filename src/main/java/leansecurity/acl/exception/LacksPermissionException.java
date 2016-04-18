package leansecurity.acl.exception;

import leansecurity.filters.Permission;

/**
 * Created by sam on 21/03/16.
 */
public class LacksPermissionException extends AccessDeniedException {
    Permission permissionLacked;

    public LacksPermissionException(Permission permissionLacked){
        super("Lacks permission: " + permissionLacked.toString());
        this.permissionLacked = permissionLacked;
    }

    public Permission getPermissionLacked(){
        return permissionLacked;
    }
}
