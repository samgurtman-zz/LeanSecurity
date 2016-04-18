package leansecurity.store;

import leansecurity.filters.Permission;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sam on 25/03/16.
 */
public class InMemoryUserStore implements UserStore{
    ConcurrentHashMap<String, InMemoryUser> userByIdMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, InMemoryUser> userByNameMap = new ConcurrentHashMap<>();


    public InMemoryUserStore(){
        InMemoryUser user = new InMemoryUser();
        user.setId(UUID.randomUUID().toString());
        user.setPasswordHash(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setRoles(Collections.singleton("testrole"));
        user.setPermissions(Collections.singleton(new Permission("abc","123","write")));
        userByIdMap.put(user.getId(), user);
        userByNameMap.put(user.getUsername(), user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userByNameMap.get(username);
    }

    @Override
    public User getUserById(String id) {
        return userByIdMap.get(id);
    }

    public static class InMemoryUser implements User{

        
        private String id;
        private String username;
        private String passwordHash;
        private Set<Permission> permissions;
        private Set<String> roles;

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPasswordHash() {
            return passwordHash;
        }

        @Override
        public Set<? extends Permission> getPermissions() {
            return permissions;
        }

        @Override
        public Set<String> getRoles() {
            return roles;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPasswordHash(String passwordHash) {
            this.passwordHash = passwordHash;
        }

        public void setPermissions(Set<Permission> permissions) {
            this.permissions = permissions;
        }

        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }
    }

}
