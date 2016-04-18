package leansecurity.aspects;

import leansecurity.acl.annotations.HasPermission;
import leansecurity.acl.exception.LacksPermissionException;
import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.filters.Permission;
import leansecurity.filters.SecurityFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import leansecurity.store.Resource;
import leansecurity.store.User;

import javax.inject.Inject;

/**
 * Created by sam on 20/03/16.
 */
@Aspect
public class PermissionEvaluator {

    @Inject
    private SecurityFilter securityFilter;

    @Before("@leansecurity.acl.annotations.HasPermission && execution(* *(Resource, ..))")
    public void evaluatePermission(JoinPoint joinPoint) {
        Resource resource = (Resource) joinPoint.getArgs()[0];
        HasPermission permissionAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasPermission.class);

        String permissionGranted = permissionAnnotation.value();
        checkPermission(resource.getType(), resource.getId(), permissionGranted);

    }

    @Before("@leansecurity.acl.annotations.HasPermission && execution(* *(String, ..))")
    public void evaluatePermissionById(JoinPoint joinPoint) {
        String resourceId = (String) joinPoint.getArgs()[0];
        HasPermission permissionAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasPermission.class);
        String resourceType = permissionAnnotation.resourceType();
        if(resourceType.isEmpty()){
            throw new IllegalArgumentException("Resource type must be specified if HasPermission with resource ID instead of Resource");
        }
        String permissionGranted = permissionAnnotation.value();
        checkPermission(resourceType, resourceId, permissionGranted);


    }

    private void checkPermission(String resourceType, String resourceId, String permissionGranted){
        if(permissionGranted == null){
            throw new IllegalStateException("Permission must be specified if using HasPermission annotation");
        }

        Permission permission = new Permission(resourceType, resourceId, permissionGranted);
        User user = securityFilter.getLoggedInUser();
        if(user == null){
            throw new NotLogggedInException();
        }

        if(!user.getPermissions().contains(permission)){
            throw new LacksPermissionException(permission);
        }
    }
}
