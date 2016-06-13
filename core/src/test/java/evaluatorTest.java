import leansecurity.acl.exception.LacksPermissionException;
import leansecurity.acl.exception.LacksRoleException;
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
    private SecurityFilter filter;
    private SecurityEvaluator securityEvaluator;


    @Before
    public void setup(){
        InMemoryUserStore.InMemoryUser user = new InMemoryUserStore.InMemoryUser();
        user.setId(UUID.randomUUID().toString());
        user.setPasswordHash(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setRoles(Collections.singleton("testrole"));
        user.addPermission("type", "id", "permission");
        userStore.addUser(user);

        this.filter = new SecurityFilter(tokenStore, userStore);

        this.securityEvaluator = new SecurityEvaluator(filter, new InMemoryRoleStore(), new InMemoryPermissionStore());
    }

    @Test
    public void testGetLoggedInUser(){
        Assert.assertNull(filter.getLoggedInUser());
        loginStandardUser();
        Assert.assertNotNull(filter.getLoggedInUser());
        Assert.assertEquals(filter.getLoggedInUser().getUsername(), "test");
    }

    @Test
    public void testLoggedInEvaluator() {
        try {
            securityEvaluator.evaluateLoggedIn();
            fail("Should throw UserNotLoggedInException");
        } catch (NotLogggedInException ignore) {
        }
        loginStandardUser();
        securityEvaluator.evaluateLoggedIn();
    }

    @Test
    public void testRoleEvaluator(){
        try{
            securityEvaluator.evaluateRole("testrole");
            fail("Should throw UserNotLoggedInException");
        } catch (NotLogggedInException ignore) {
        }
        loginStandardUser();
        try{
            securityEvaluator.evaluateRole("testrole2");
            fail("Should throw LacksRoleException");
        } catch (LacksRoleException ignore) {
        }
        securityEvaluator.evaluateRole("testrole");
    }

    @Test
    public void testPermissionEvaluator(){
        try{
            securityEvaluator.evaluatePermission("type","id","permission");
            fail("Should throw UserNotLoggedInException");
        }catch(NotLogggedInException ignore){}
        loginStandardUser();
        try{
            securityEvaluator.evaluatePermission("type","id","permission2");
            fail("Should throw LacksPermissionException");
        }catch(LacksPermissionException ignore){}
        try{
            securityEvaluator.evaluatePermission("type2","id","permission");
            fail("Should throw LacksPermissionException");
        }catch(LacksPermissionException ignore){}
        try{
            securityEvaluator.evaluatePermission("type","id2","permission");
            fail("Should throw LacksPermissionException");
        }catch(LacksPermissionException ignore){}

        securityEvaluator.evaluatePermission("type","id","permission");
    }

    private void loginStandardUser(){
        filter.login("test", "password", Duration.ofHours(1), Duration.ofHours(2));

    }
}
