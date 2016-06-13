package leansecurity.store;

/**
 * Manages users for security filter
 */
public interface UserStore{
    User getUserByUsername(String username);
    User getUserById(String id);
}
