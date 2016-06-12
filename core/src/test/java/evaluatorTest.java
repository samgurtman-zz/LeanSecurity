import leansecurity.acl.exception.NotLogggedInException;
import leansecurity.aspects.SecurityEvaluator;
import leansecurity.filters.SecurityFilter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;
import store.InMemoryPermissionStore;
import store.InMemoryRoleStore;
import store.InMemoryTokenStore;
import store.InMemoryUserStore;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.fail;

/**
 * Created by sam on 12/06/16.
 */
public class evaluatorTest {
    private InMemoryUserStore userStore = new InMemoryUserStore();
    private InMemoryTokenStore tokenStore = new InMemoryTokenStore();

    @Before
    public void setup(){
        InMemoryUserStore.InMemoryUser user = new InMemoryUserStore.InMemoryUser();
        user.setId(UUID.randomUUID().toString());
        user.setPasswordHash(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setRoles(Collections.singleton("testrole"));
        userStore.addUser(user);
    }

    @Test
    public void testEvalutor(){

        SecurityFilter filter = new SecurityFilter(tokenStore, userStore);

        Assert.assertNull(filter.getLoggedInUser());
        SecurityEvaluator securityEvaluator = new SecurityEvaluator(filter, new InMemoryRoleStore(), new InMemoryPermissionStore());
        try{
            securityEvaluator.evaluateLoggedIn();
            fail("Should throw UserNotLoggedInException");
        }catch(NotLogggedInException ignore){}
        try{
            securityEvaluator.evaluatePermission("resource","id","permission");
            fail("Should throw UserNotLoggedInException");
        }catch(NotLogggedInException ignore){}
        filter.login("test", "password", Duration.ofHours(1), Duration.ofHours(2));
        securityEvaluator.evaluateLoggedIn();


    }
}
