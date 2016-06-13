package leansecurity.store;

/**
 * Manages tokens for security filter
 */
public interface TokenStore {

    Token getToken(String tokenId);

    Token getTokenByRefresh(String refreshId);

    Token generateToken(User user, long durationMillis, long refreshDurationMillis);

    void removeToken(Token token);

}
