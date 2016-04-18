package leansecurity.store;

/**
 * Created by sam on 13/04/16.
 */
public interface UserStore {
    User getUserByUsername(String username);
    User getUserById(String id);
}
