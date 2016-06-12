package store;

import leansecurity.store.RoleStore;

/**
 * Created by sam on 12/06/16.
 */
public class InMemoryRoleStore implements RoleStore<InMemoryUserStore.InMemoryUser> {
    @Override
    public boolean hasRole(InMemoryUserStore.InMemoryUser user, String role) {
        return user.getRoles().contains(role);
    }
}
