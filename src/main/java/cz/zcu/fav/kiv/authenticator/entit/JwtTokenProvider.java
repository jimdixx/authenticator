package cz.zcu.fav.kiv.authenticator.entit;

import cz.zcu.fav.kiv.authenticator.dials.JwtExceptionStatus;
import cz.zcu.fav.kiv.authenticator.dials.StatusCodes;
import cz.zcu.fav.kiv.authenticator.jwtException.JwtException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import com.sun.security.auth.UserPrincipal;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class that has methods for JWT token management
 * @version 1.1
 * @author Petr Urban, Jiri Trefil, Vaclav Hrabik
 */
@Component
public class JwtTokenProvider {

    /**
     * Key for JWT token
     */
    @Value("${secret.key}")
    private String JWT_SECRET;

    /**
     * Collection of all active tokens (valid and invalid ones)
     */
    private static final Map<String, Date> tokenMap = new HashMap<>();
    /**
     * Life spawn of token, now 5 min
     */
    private static final long JWT_EXPIRATION = 300_000L; // 300 sec = 5 min

    /**
     * Life spawn of refreshed token, 1 hour
     */
    private static final long JWT_EXPIRATION_EXTENDED = 3_600_000L; // 3600 sec = 60 min
    /**
     * method to generate JWT token from username
     * @param authentication    wrapper of user credentials
     * @return                  JWT token as string
     */
    public String generateToken(Authentication authentication, boolean refreshToken) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (refreshToken ? JWT_EXPIRATION_EXTENDED : JWT_EXPIRATION));
        String randomId = UUID.randomUUID().toString();
        addTokenToMap(randomId,expirationDate);

        return Jwts.builder()
                .setId(randomId)
                .setSubject(userPrincipal.getName())
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    /**
     * Method accessed from TokenRemovalScheduler that removes all tokens which are outdated
     * The monitor adds quite a lot of overhead to code execution - should not be called too often
     */
    public static synchronized void removeExpiredTokens(){
        Date now = new Date();
        for(String token:tokenMap.keySet()){
            Date tokenDate = tokenMap.get(token);
            //token is expired - remove it from map
            if(now.after(tokenDate))
                tokenMap.remove(token);
        }

    }
    private synchronized boolean removeTokenFromMap(String uuid){
        if(!tokenMap.containsKey(uuid)) return false;
        tokenMap.remove(uuid);
        return true;
    }
    private synchronized boolean addTokenToMap(String uuid, Date expirationDate){
        if(tokenMap.containsKey(uuid))return false;
        tokenMap.put(uuid,expirationDate);
        return true;
    }


    /**
     * internal method for get key to collection of active token
     * @param token JWT token
     * @return      key to collection of active tokens  - if token is valid
     *              null                                - if token is invalid
     */
    public String getAuthentication(String token) {
        String id;

        try {
            id = parserJWTToken(token).getBody().getId();
        } catch (JwtException e) {
            return null;
        }
        return id;
    }

    /**
     * method for validation of JWT token
     * @param token     JWT token for validation
     * @return          Code value of StatusCodes
     *                      - 200 + MSG - if token is valid
     *                      - 401 + MSG - if something is wrong with token
     */
    public StatusCodes validateToken(String token) {

        // controls if token is in collection of active tokens
        String id = getAuthentication(token);
        if (id == null) {
            return StatusCodes.USER_TOKEN_INVALID;
        }
        if(!tokenMap.containsKey(id)) {
            return StatusCodes.USER_TOKEN_INVALID;
        }

        // controls token it self
        try {
            parserJWTToken(token);
            return StatusCodes.USER_TOKEN_VALID;
        } catch (JwtException e) {
            // invalid signature
            return StatusCodes.USER_TOKEN_INVALID;
        }
    }

    /**
     * Internal method to parse JWT token in one place
     * @param token         token for parsing
     * @return              parsed token
     * @throws Exception    generic exception - everytime it must be handled differently
     */
    public Jws<Claims> parserJWTToken(String token) throws JwtException {
        //no token is provided
        if(token == null || token.length() == 0)return null;
        try {
            return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
        } catch (SignatureException ex) {
            // invalid signature
            throw new JwtException(JwtExceptionStatus.INVALID_SIGNATURE);
        } catch (MalformedJwtException ex) {
            // invalid token
            throw new JwtException(JwtExceptionStatus.INVALID_TOKEN);
        } catch (ExpiredJwtException ex) {
            // expired token
            throw new JwtException(JwtExceptionStatus.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException ex) {
            // unsupported token
            throw new JwtException(JwtExceptionStatus.UNSUPPORTED_TOKEN);
        }
    }

    /**
     * method to get name for JWT token
     * @param token     JWT token from it will be username parsed
     * @return          username    - if token is valid
     *                  null        - if token is in valid
     */
    public String getNameFromToken(String token) {
        String name;
        try {
             name = parserJWTToken(token).getBody().getSubject();
        } catch (JwtException e) {
            return null;
        }
        return name;
    }

    /**
     * Method makes token invalid
     * @param token JWT token of user who wants to be logged out
     * @return      true    - if token was successfully invalidated
     *              false   - if token in invalid or non existant
     */
    public boolean invalidateToken(String token){
        String uuid = getAuthentication(token);
        if (uuid == null || !tokenMap.containsKey(uuid)) {
            return false;
        }
        removeTokenFromMap(uuid);
        return true;
    }


}
