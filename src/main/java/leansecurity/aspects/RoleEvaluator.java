package leansecurity.aspects;

import leansecurity.acl.annotations.HasRole;
import leansecurity.acl.exception.LacksRoleException;
import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.filters.SecurityFilter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import leansecurity.store.User;

import javax.inject.Inject;

/**
 * Created by sam on 21/03/16.
 */
@Aspect
public class RoleEvaluator {


    @Inject
    private SecurityFilter securityFilter;

    @Before("@leansecurity.acl.annotations.HasRole && execution(* *(..))")
    public void evaluatePermission(JoinPoint joinPoint) {
        HasRole roleAnnotation = ((MethodSignature)joinPoint.getSignature()).getMethod().getAnnotation(HasRole.class);

        String roleNeeded = roleAnnotation.value();
        User user = securityFilter.getLoggedInUser();
        if(user == null){
            throw new NotLogggedInException();
        }

        if(!user.getRoles().contains(roleNeeded)){
            throw new LacksRoleException(roleNeeded);
        }
    }


}
