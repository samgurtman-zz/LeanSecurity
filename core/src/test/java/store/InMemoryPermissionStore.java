package store;

import leansecurity.store.PermissionStore;
import leansecurity.store.User;

import java.util.List;

/**
 * PermissionStore for tests
 */
public class InMemoryPermissionStore implements PermissionStore{
    @Override
    public boolean hasPermssion(String resourceType, String resourceId, User inMemoryUser, String permission) {
        List<String> permissions = ((InMemoryUserStore.InMemoryUser)inMemoryUser).getPermissions(resourceType, resourceId);
        return permissions != null && permissions.contains(permission);
    }
}
