package leansecurity.store;

/**
 * Tests role  for security filter
 */
public interface RoleStore {

    /**
     * Get the permission
     * @param user user to test if has role
     * @param role role to test
     * @return whether use has role
     */
    boolean hasRole(User user, String role);
}
