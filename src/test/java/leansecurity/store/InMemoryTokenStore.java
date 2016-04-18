package leansecurity.store;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by sam on 18/04/16.
 */
public class InMemoryTokenStore implements TokenStore {

    ConcurrentHashMap<String, InMemoryTokenStore.InMemoryToken> tokenByIdMap = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, InMemoryTokenStore.InMemoryToken> tokenByRefreshIdMap = new ConcurrentHashMap<>();


    @Override
    public Token getToken(String tokenId) {
        return tokenByIdMap.get(tokenId);
    }

    @Override
    public Token getTokenByRefresh(String refreshId) {
        return tokenByRefreshIdMap.get(refreshId);
    }

    @Override
    public Token generateToken(String userId, long durationMillis, long refreshDurationMillis) {
        InMemoryToken token = new InMemoryToken(userId, durationMillis, refreshDurationMillis);
        tokenByIdMap.put(token.getTokenId(), token);
        tokenByRefreshIdMap.put(token.getRefreshTokenId(), token);
        return token;
    }

    @Override
    public void removeToken(String tokenId) {

    }

    private static class InMemoryToken implements Token{

        private String tokenId;
        private String refreshTokenId;
        private String userId;
        private long issuedEpochMillis;
        private long tokenDurationMillis;
        private long refreshTokenDurationMillis;

        private InMemoryToken(String userId, long tokenDurationMillis, long refreshTokenDurationMillis){
            this.tokenId = UUID.randomUUID().toString();
            this.refreshTokenId = UUID.randomUUID().toString();
            this.userId = userId;
            this.issuedEpochMillis = System.currentTimeMillis();
            this.tokenDurationMillis = tokenDurationMillis;
            this.refreshTokenDurationMillis = refreshTokenDurationMillis;
        }


        @Override
        public String getTokenId() {
            return tokenId;
        }

        @Override
        public long getIssuedEpochMillis() {
            return issuedEpochMillis;
        }

        @Override
        public long getTokenDurationMillis() {
            return tokenDurationMillis;
        }

        @Override
        public String getRefreshTokenId() {
            return refreshTokenId;
        }

        @Override
        public long getRefreshTokenDurationMillis() {
            return refreshTokenDurationMillis;
        }

        @Override
        public String getUserId() {
            return userId;
        }
    }
}
