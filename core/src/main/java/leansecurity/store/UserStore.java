package leansecurity.store;

/**
 * Created by sam on 13/04/16.
 */
public interface UserStore <T extends User>{
    T getUserByUsername(String username);
    T getUserById(String id);
}
