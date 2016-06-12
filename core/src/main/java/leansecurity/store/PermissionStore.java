package leansecurity.store;

import java.util.Collection;

/**
 * Created by sam on 13/05/16.
 */
public interface PermissionStore <T extends User> {

    /**
     * Get the permission
     * @param resourceType
     * @param resourceId
     * @param user
     * @return
     */
    boolean hasPermssion(String resourceType, String resourceId, T user, String permission);
}
