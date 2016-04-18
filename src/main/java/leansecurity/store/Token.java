package leansecurity.store;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by sam on 20/03/16.
 */
public interface Token {
    String getTokenId();
    long getIssuedEpochMillis();
    long getTokenDurationMillis();
    String getRefreshTokenId();
    long getRefreshTokenDurationMillis();
    String getUserId();
}
