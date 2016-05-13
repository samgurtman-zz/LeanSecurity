package leansecurity.aspects;

import leansecurity.acl.exception.LacksPermissionException;
import leansecurity.acl.exception.LacksRoleException;
import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.filters.SecurityFilter;
import leansecurity.store.PermissionStore;
import leansecurity.store.Resource;
import leansecurity.store.User;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by sam on 13/05/16.
 */
@Named
public class SecurityEvaluator {

    private final SecurityFilter securityFilter;
    private final PermissionStore permissionStore;

    @Inject
    public SecurityEvaluator(SecurityFilter securityFilter, PermissionStore permissionStore){
        this.securityFilter = securityFilter;
        this.permissionStore = permissionStore;
    }



    private User getUser(){
        User user = securityFilter.getLoggedInUser();
        if(user == null){
            throw new NotLogggedInException();
        }
        return user;
    }

    public void evaluateLoggedIn(){
        getUser();
    }

    public void evaluateRole(String roleNeeded){
        User user = getUser();

        if(!user.getRoles().contains(roleNeeded)){
            throw new LacksRoleException(roleNeeded);
        }
    }

    public void evaluatePermission(Resource resource, String permissionGranted){
        evaluatePermission(resource.getType(), resource.getId(), permissionGranted);
    }

    public void evaluatePermission(String resourceType, String resourceId, String permissionGranted){
        if(permissionGranted == null){
            throw new IllegalStateException("Permission must be specified if using HasPermission annotation");
        }

        User user = getUser();
        String permission = permissionStore.getPermission(resourceType, resourceId, user);

        if(permission == null || !permission.equals(permissionGranted)){
            throw new LacksPermissionException(resourceType, resourceId, permissionGranted);
        }
    }
}
