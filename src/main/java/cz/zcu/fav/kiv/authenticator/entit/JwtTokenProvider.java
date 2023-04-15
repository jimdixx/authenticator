package cz.zcu.fav.kiv.authenticator.entit;

import cz.zcu.fav.kiv.authenticator.dials.StatusCodes;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import com.sun.security.auth.UserPrincipal;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
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
    private static final HashMap<String, Boolean> tokenMap = new HashMap<>();
    /**
     * Life spawn of token, now 5 min
     */
    private static final long JWT_EXPIRATION = 300000L;

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

        tokenMap.put(randomId,true);

        return Jwts.builder()
                .setId(randomId)
                .setSubject(userPrincipal.getName())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
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
        } catch (Exception e) {
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
        } catch (Exception e) {
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
    public Jws<Claims> parserJWTToken(String token) throws Exception {
        try {
            return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
        } catch (SignatureException ex) {
            // invalid signature
            throw new Exception();
        } catch (MalformedJwtException ex) {
            // invalid token
            throw new Exception();
        } catch (ExpiredJwtException ex) {
            // expired token
            throw new Exception();
        } catch (UnsupportedJwtException ex) {
            // unsupported token
            throw new Exception();
        } catch (IllegalArgumentException ex) {
            // token is empty or null
            throw new Exception();
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
        } catch (Exception e) {
            return null;
        }
        return name;
    }

    /**
     * method make token invalid -> user is logged out
     * @param token JWT token of user who wants to be logged out
     * @return      true    - if token is valid
     *              false   - if token is invalid
     */
    public boolean invalidateToken(String token){
        String uuid = getAuthentication(token);
        if (uuid == null) {
            return false;
        }
        return tokenMap.remove(uuid);
    }


}
