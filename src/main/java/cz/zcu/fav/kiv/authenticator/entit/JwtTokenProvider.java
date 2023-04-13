package cz.zcu.fav.kiv.authenticator.entit;

import cz.zcu.fav.kiv.authenticator.dials.UserModelStatusCodes;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${secret.key}")
    private String JWT_SECRET;
    private static HashMap<String, Boolean> tokenMap = new HashMap<>();
    private static final long JWT_EXPIRATION = 300000L;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + JWT_EXPIRATION);
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

    public String getAuthentication(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    public UserModelStatusCodes validateToken(String token) {
        if(!tokenMap.containsKey(getAuthentication(token))) {
            return UserModelStatusCodes.USER_TOKEN_INVALID;
        }

        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return UserModelStatusCodes.USER_TOKEN_VALID;
        } catch (SignatureException ex) {
            // invalid signature
            return UserModelStatusCodes.USER_TOKEN_INVALID_SIGNATURE;
        } catch (MalformedJwtException ex) {
            // invalid token
            return UserModelStatusCodes.USER_TOKEN_INVALID;
        } catch (ExpiredJwtException ex) {
            // expired token
            return UserModelStatusCodes.USER_TOKEN_EXPIRED;
        } catch (UnsupportedJwtException ex) {
            // unsupported token
            return UserModelStatusCodes.USER_TOKEN_UNSUPPORTED;
        } catch (IllegalArgumentException ex) {
            // token is empty or null
            return UserModelStatusCodes.USER_TOKEN_EMPTY_OR_NULL;
        }
    }

    public String getNameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean invalidateToken(String token){
        String uuid = getAuthentication(token);
        return tokenMap.remove(uuid);
    }


}
