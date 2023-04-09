package cz.zcu.fav.kiv.authenticator.entit;

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

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // invalid signature
        } catch (MalformedJwtException ex) {
            // invalid token
        } catch (ExpiredJwtException ex) {
            // expired token
        } catch (UnsupportedJwtException ex) {
            // unsupported token
        } catch (IllegalArgumentException ex) {
            // token is empty or null
        }
        return false;
    }


    public boolean invalidateToken(String token){
        String uuid = getAuthentication(token);
        return tokenMap.remove(uuid);
    }


}
