package leansecurity.store;


import leansecurity.filters.Permission;

import java.util.Collection;
import java.util.Set;

/**
 * Created by sam on 20/03/16.
 */
public interface User {
    String getId();
    String getUsername();
    String getPasswordHash();
    Set<? extends Permission> getPermissions();
    Set<String> getRoles();
}
