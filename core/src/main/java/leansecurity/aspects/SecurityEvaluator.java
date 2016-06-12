package leansecurity.aspects;

import leansecurity.acl.exception.LacksPermissionException;
import leansecurity.acl.exception.LacksRoleException;
import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.filters.SecurityFilter;
import leansecurity.store.PermissionStore;
import leansecurity.store.Resource;
import leansecurity.store.RoleStore;
import leansecurity.store.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;

/**
 * User programatically and by aspects to assert permissions
 */
@Named("securityEvaluator")
public class SecurityEvaluator {

    private final SecurityFilter securityFilter;
    private final PermissionStore permissionStore;
    private final RoleStore roleStore;

    @Inject
    public SecurityEvaluator(SecurityFilter securityFilter, RoleStore roleStore, PermissionStore permissionStore){
        this.securityFilter = securityFilter;
        this.roleStore = roleStore;
        this.permissionStore = permissionStore;
    }



    private User getUser(){
        User user = securityFilter.getLoggedInUser();
        if(user == null){
            throw new NotLogggedInException();
        }
        return user;
    }

    /**
     * Evaluate whether there is a currently logged in user
     */
    public void evaluateLoggedIn(){
        getUser();
    }

    /**
     * Evaluate whether the current user has the given role
     * @param roleNeeded role to check for
     */
    public void evaluateRole(String roleNeeded){
        User user = getUser();
        boolean hasRole = roleStore.hasRole(user, roleNeeded);
        if(!hasRole){
            throw new LacksRoleException(roleNeeded);
        }
    }

    /**
     * Evaluate whether the current logged in user has the given permission on the given resource
     * @param resource resource permission is for
     * @param permissionGranted permission to check for
     */
    public void evaluatePermission(Resource resource, String permissionGranted){
        evaluatePermission(resource.getType(), resource.getId(), permissionGranted);
    }

    /**
     * Evaluate whether the current logged in user has the given permission on the given resource
     * @param resourceType type of resource
     * @param resourceId unique id of resource (unique within resource type)
     * @param permissionGranted permission to check for
     */
    public void evaluatePermission(String resourceType, String resourceId, String permissionGranted){
        if(permissionGranted == null){
            throw new IllegalStateException("Permission must be specified if using HasPermission annotation");
        }

        User user = getUser();
        boolean hasPermssion = permissionStore.hasPermssion(resourceType, resourceId, user, permissionGranted);
        if(!hasPermssion){
            throw new LacksPermissionException(resourceType, resourceId, permissionGranted);
        }
    }
}
