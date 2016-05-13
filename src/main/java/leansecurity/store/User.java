package leansecurity.store;


import java.util.Set;

/**
 * Created by sam on 20/03/16.
 */
public interface User {
    String getId();
    String getUsername();
    String getPasswordHash();
    Set<String> getRoles();
}
