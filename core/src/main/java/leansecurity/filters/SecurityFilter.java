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
import javax.inject.Provider;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * Main security framework filter
 */
public class SecurityFilter implements Filter {

    private static final String TOKEN_HEADER_ID_NAME = "session.token.id";

    private String invalidHash = BCrypt.hashpw("USER_NOT_FOUND_PASSWORD", BCrypt.gensalt());
    private TokenStore tokenStore;
    private UserStore userStore;

    private ThreadLocal<HttpServletResponse> responseThreadLocal = new ThreadLocal<>();
    private ThreadLocal<UserAndToken> userWithTokenIdThreadLocal = new ThreadLocal<>();

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

    /**
     * Handles exceptions thrown by the security framework
     * @param message message to append to error response
     * @param errorCode error code to return
     */
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

    /**
     * Get the currently logged in user
     * @return currently logged in user or null if no user is logged in
     */
    public User getLoggedInUser(){
        UserAndToken userAndToken = userWithTokenIdThreadLocal.get();
        return userAndToken == null ? null : userAndToken.user;
    }

    /**
     * Log out the current user
     * @return whether user was logged out (true if user was found to log out)
     */
    public boolean logout(){
        UserAndToken userAndToken = userWithTokenIdThreadLocal.get();
        if(userAndToken == null){
            return false;
        }
        else{
            tokenStore.removeToken(userAndToken.token);
            userWithTokenIdThreadLocal.remove();
            return true;
        }
    }

    private boolean verifyLogin(User user, String password){
        if(user == null){
            BCrypt.checkpw("USER_NOT_FOUND_INVALID_PASSWORD", invalidHash); //prevent timing attacks
            return false;
        }
        return BCrypt.checkpw(password, user.getPasswordHash());
    }

    /**
     * Login a new current user
     * @param username username to log in
     * @param password password of user to use
     * @param sessionDuration duration of user's session
     * @param refreshDuration duration of user's remember-me refresh duration
     * @return session token that must be passed as header on all future requests
     */
    public Token login(String username, String password, Duration sessionDuration, Duration refreshDuration){
        User user = userStore.getUserByUsername(username);
        if(verifyLogin(user, password)){
            Token token = tokenStore.generateToken(user, sessionDuration.toMillis(), refreshDuration.toMillis());
            UserAndToken currentUserAndToken = new UserAndToken();
            currentUserAndToken.token = token;
            currentUserAndToken.user = user;
            userWithTokenIdThreadLocal.set(currentUserAndToken);
            return token;
        }else{
            return null;
        }
    }

    /**
     * Refresh a user's session given their refresh token id
     * @param refreshTokenId id of token
     * @return new session token
     */
    public Token refresh(String refreshTokenId){
        Token token = tokenStore.getTokenByRefresh(refreshTokenId);
        if(token == null){
            return null;
        }else{
            if(System.currentTimeMillis() < token.getIssuedEpochMillis() + token.getRefreshTokenDurationMillis()){
                tokenStore.removeToken(token);
                User user = userStore.getUserById(token.getUserId());
                return tokenStore.generateToken(user, token.getTokenDurationMillis(), token.getRefreshTokenDurationMillis());
            }else{
                return null;
            }
        }
    }

    /**
     * Wrapper to hold User and their token in the current thread
     */
    private class UserAndToken {
         User user;
         Token token;
    }

    /**
     * Hash password in correct framework hashing algorithm
     * @param password password to hash
     * @return hashed password
     */
    public static String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

