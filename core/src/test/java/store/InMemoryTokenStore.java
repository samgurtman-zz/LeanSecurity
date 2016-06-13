package store;

import leansecurity.store.Token;
import leansecurity.store.TokenStore;
import leansecurity.store.User;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TokenStore for tests
 */
public class InMemoryTokenStore implements TokenStore {

    private ConcurrentHashMap<String, InMemoryTokenStore.InMemoryToken> tokenByIdMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, InMemoryTokenStore.InMemoryToken> tokenByRefreshIdMap = new ConcurrentHashMap<>();


    @Override
    public InMemoryToken getToken(String tokenId) {
        return tokenByIdMap.get(tokenId);
    }

    @Override
    public InMemoryToken getTokenByRefresh(String refreshId) {
        return tokenByRefreshIdMap.get(refreshId);
    }

    @Override
    public InMemoryToken generateToken(User user, long durationMillis, long refreshDurationMillis) {
        InMemoryToken token = new InMemoryToken(user.getId(), durationMillis, refreshDurationMillis);
        tokenByIdMap.put(token.getTokenId(), token);
        tokenByRefreshIdMap.put(token.getRefreshTokenId(), token);
        return token;
    }

    @Override
    public void removeToken(Token tokenId) {

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
