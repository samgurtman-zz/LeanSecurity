package store;

import leansecurity.store.PermissionStore;
import leansecurity.store.User;

import java.util.List;

/**
 * Created by sam on 12/06/16.
 */
public class InMemoryPermissionStore implements PermissionStore <InMemoryUserStore.InMemoryUser>{
    @Override
    public boolean hasPermssion(String resourceType, String resourceId, InMemoryUserStore.InMemoryUser inMemoryUser, String permission) {
        List<String> permissions = inMemoryUser.getPermissions(resourceType, resourceId);
        return permission.contains(permission);
    }
}
