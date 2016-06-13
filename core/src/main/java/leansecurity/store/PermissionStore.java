package leansecurity.store;

import java.util.Collection;

/**
 * Manages permissions for security filter
 */
public interface PermissionStore {
    /**
     * Get the permission
     * @param resourceType resource type
     * @param resourceId resource id
     * @param user user to check permission for
     * @return whether user has permission on resource
     */
    boolean hasPermssion(String resourceType, String resourceId, User user, String permission);
}
