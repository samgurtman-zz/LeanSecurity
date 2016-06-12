package leansecurity.store;

/**
 * Created by sam on 12/06/16.
 */
public interface RoleStore <T extends User> {

    /**
     * Get the permission
     * @param user
     * @return
     */
    boolean hasRole(T user, String role);
}
