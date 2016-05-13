package leansecurity.aspectj;

import leansecurity.acl.annotations.HasPermission;
import leansecurity.acl.annotations.HasRole;
import leansecurity.acl.exception.LacksRoleException;
import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.aspects.SecurityEvaluator;
import leansecurity.filters.SecurityFilter;
import leansecurity.store.Resource;
import leansecurity.store.User;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;

import javax.inject.Inject;

/**
 * Created by sam on 13/05/16.
 */
@Aspect
public class Pointcuts {

    private SecurityEvaluator securityEvaluator;

    public void setSecurityEvaluator(SecurityEvaluator securityEvaluator){
        this.securityEvaluator = securityEvaluator;
    }

    @Before("@annotation( leansecurity.acl.annotations.IsLoggedIn ) && execution(* *(..))")
    public void evaluateLoggedIn(JoinPoint joinPoint) throws Throwable {
        securityEvaluator.evaluateLoggedIn();
    }

    @Before("@annotation( leansecurity.acl.annotations.HasPermission ) && execution(* *(Resource, ..))")
    public void evaluatePermission(JoinPoint joinPoint) throws Throwable {
        Resource resource = (Resource) joinPoint.getArgs()[0];
        HasPermission permissionAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasPermission.class);
        String permissionGranted = permissionAnnotation.value();
        securityEvaluator.evaluatePermission(resource, permissionGranted);

    }

    @Before("@annotation(leansecurity.acl.annotations.HasPermission) && execution(* *(String, ..))")
    public void evaluatePermissionById(JoinPoint joinPoint) throws Throwable {
        String resourceId = (String) joinPoint.getArgs()[0];
        HasPermission permissionAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasPermission.class);
        String resourceType = permissionAnnotation.resourceType();
        if(resourceType.isEmpty()){
            throw new IllegalArgumentException("Resource type must be specified if HasPermission with resource ID instead of Resource");
        }
        String permissionGranted = permissionAnnotation.value();
        securityEvaluator.evaluatePermission(resourceType, resourceId, permissionGranted);
    }

    @Before("@annotation( leansecurity.acl.annotations.HasRole ) && execution(* *(..))")
    public void evaluateRole(JoinPoint joinPoint) throws Throwable {
        HasRole roleAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasRole.class);

        String roleNeeded = roleAnnotation.value();
        securityEvaluator.evaluateRole(roleNeeded);

    }
}
