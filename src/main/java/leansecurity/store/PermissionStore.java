package leansecurity.store;

/**
 * Created by sam on 13/05/16.
 */
public interface PermissionStore {
    String getPermission(String resourceType, String resourceId, User user);
}
