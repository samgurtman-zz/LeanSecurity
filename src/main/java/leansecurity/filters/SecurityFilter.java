package leansecurity.filters;

import leansecurity.acl.exception.AccessDeniedException;
import leansecurity.acl.exception.NotLogggedInException;
import org.mindrot.jbcrypt.BCrypt;
import leansecurity.store.Token;
import leansecurity.store.TokenStore;
import leansecurity.store.User;
import leansecurity.store.UserStore;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * Created by sam on 20/03/16.
 */
@Named
public class SecurityFilter implements Filter {

    private static final String TOKEN_HEADER_ID_NAME = "session.token.id";

    private String invalidHash = BCrypt.hashpw("USER_NOT_FOUND_PASSWORD", BCrypt.gensalt());
    private TokenStore tokenStore;
    private UserStore userStore;

    private ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();
    private ThreadLocal<UserAndToken> userWithTokenIdThreadLocal = new ThreadLocal<>();

    @Inject
    public SecurityFilter(TokenStore tokenStore, UserStore userStore){
        this.tokenStore = tokenStore;
        this.userStore = userStore;
    }


    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        try {
            responseThreadLocal.set((HttpServletResponse) servletResponse);

            String tokenHeader = httpRequest.getHeader(TOKEN_HEADER_ID_NAME);
            UserAndToken currentUserAndToken = null;
            if (tokenHeader != null) {
                Token token = tokenStore.getToken(tokenHeader);
                if(token != null && System.currentTimeMillis() < token.getIssuedEpochMillis() + token.getTokenDurationMillis()) {
                    User user = userStore.getUserById(token.getUserId());
                    if (user != null) {
                        currentUserAndToken = new UserAndToken();
                        currentUserAndToken.token = token;
                        currentUserAndToken.user = user;
                    }
                }
            }

            if(currentUserAndToken != null){
                userWithTokenIdThreadLocal.set(currentUserAndToken);
            }

            try {
                chain.doFilter(servletRequest, servletResponse);
            } catch (AccessDeniedException exception) {
                handleError("Access Denied, " + exception.getMessage(), 403);
            } catch (NotLogggedInException exception) {
                handleError("No logged in user", 401);
            }
        }finally {
            userWithTokenIdThreadLocal.remove();
            responseThreadLocal.remove();
        }
    }

    private void handleError(String message, int errorCode){
        HttpServletResponse response = responseThreadLocal.get();
        if(!response.isCommitted()){
            response.setStatus(errorCode);
            try {
                response.getWriter().write(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void destroy() {
    }

    public User getLoggedInUser(){
        return userWithTokenIdThreadLocal.get().user;
    }


    public boolean logout(){
        UserAndToken userAndToken = userWithTokenIdThreadLocal.get();
        if(userAndToken == null){
            return false;
        }
        else{
            tokenStore.removeToken(userAndToken.token.getTokenId());
            return true;
        }
    }

    public Token login(String username, String password, Duration sessionDuration, Duration refreshDuration){
        User user = userStore.getUserByUsername(username);
        if(user == null){
            BCrypt.checkpw("USER_NOT_FOUND_INVALID_PASSWORD", invalidHash); //prevent timing attacks
            return null;
        }

        if(BCrypt.checkpw(password, user.getPasswordHash())) {
            Token token = tokenStore.generateToken(user.getId(), sessionDuration.toMillis(), refreshDuration.toMillis());
            UserAndToken currentUserAndToken = new UserAndToken();
            currentUserAndToken.token = token;
            currentUserAndToken.user = user;
            userWithTokenIdThreadLocal.set(currentUserAndToken);
            return token;
        }
        return null;
    }

    public Token refresh(String refreshTokenId){
        Token token = tokenStore.getTokenByRefresh(refreshTokenId);
        if(token == null){
            return null;
        }else{
            if(System.currentTimeMillis() < token.getIssuedEpochMillis() + token.getRefreshTokenDurationMillis()){
                tokenStore.removeToken(token.getTokenId());
                return tokenStore.generateToken(token.getUserId(), token.getTokenDurationMillis(), token.getRefreshTokenDurationMillis());
            }else{
                return null;
            }
        }
    }
    /**
     * Created by sam on 25/03/16.
     */
    private static class UserAndToken {
         User user;
         Token token;
    }
}

