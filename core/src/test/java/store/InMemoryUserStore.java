package store;

import leansecurity.store.Resource;
import leansecurity.store.User;
import leansecurity.store.UserStore;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * UserStore for tests
 */
public class InMemoryUserStore implements UserStore {
    private ConcurrentHashMap<String, InMemoryUser> userByIdMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, InMemoryUser> userByNameMap = new ConcurrentHashMap<>();


    public InMemoryUserStore(){
    }

    @Override
    public User getUserByUsername(String username) {
        return userByNameMap.get(username);
    }

    @Override
    public User getUserById(String id) {
        return userByIdMap.get(id);
    }

    public void addUser(InMemoryUser user){
        userByIdMap.put(user.getId(), user);
        userByNameMap.put(user.getUsername(), user);
    }


    public static class InMemoryUser implements User{

        
        private String id;
        private String username;
        private String passwordHash;
        private Set<String> roles;
        private Map<ResourceIdentifier, List<String>> permissions = new HashMap<>();

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

        Set<String> getRoles() {
            return roles;
        }

        List<String> getPermissions(String resourceType, String resourceId){
            return permissions.get(new ResourceIdentifier(resourceType, resourceId));
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


        public void setRoles(Set<String> roles) {
            this.roles = roles;
        }


        public void addPermission(String type, String id, String permission){
            ResourceIdentifier identifier = new ResourceIdentifier(type, id);
            permissions.compute(identifier, (key, oldValue) ->{
                List<String> newValues = oldValue == null ? new ArrayList<>() : new ArrayList<>(oldValue);
                newValues.add(permission);
                return newValues;
            });
        }

        static class ResourceIdentifier implements Resource{

            private String type;
            private String id;


            private ResourceIdentifier(String type, String id){
                this.type = type;
                this.id = id;
            }


            @Override
            public String getType() {
                return type;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ResourceIdentifier that = (ResourceIdentifier) o;
                return Objects.equals(type, that.type) &&
                        Objects.equals(id, that.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(type, id);
            }
        }
    }


}
