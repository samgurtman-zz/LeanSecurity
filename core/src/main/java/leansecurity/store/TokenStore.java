package leansecurity.store;

/**
 * Created by sam on 20/03/16.
 */
public interface TokenStore <T extends Token> {

    T getToken(String tokenId);

    T getTokenByRefresh(String refreshId);

    T generateToken(String userId, long durationMillis, long refreshDurationMillis);

    void removeToken(String tokenId);

}
