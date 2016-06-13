package store;

import leansecurity.store.RoleStore;
import leansecurity.store.User;

/**
 * Role Store for tests
 */
public class InMemoryRoleStore implements RoleStore {
    @Override
    public boolean hasRole(User user, String role) {
        return ((InMemoryUserStore.InMemoryUser)user).getRoles().contains(role);
    }
}
