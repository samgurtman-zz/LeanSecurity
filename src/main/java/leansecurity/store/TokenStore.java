package leansecurity.store;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by sam on 20/03/16.
 */
public interface TokenStore {

    Token getToken(String tokenId);

    Token getTokenByRefresh(String refreshId);

    Token generateToken(String userId, long durationMillis, long refreshDurationMillis);

    void removeToken(String tokenId);


}
