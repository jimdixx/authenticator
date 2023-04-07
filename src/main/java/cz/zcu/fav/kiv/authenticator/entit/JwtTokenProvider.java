package cz.zcu.fav.kiv.authenticator.entit;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String JWT_SECRET = "private_key";

    private static final long JWT_EXPIRATION = 30000L;

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + JWT_EXPIRATION);
        return Jwts.builder()
                .setSubject(userPrincipal.getName())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        String userName = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        UserPrincipal userPrincipal = new UserPrincipal(userName);
        // TODO Get authorities or something like that? Do we need that?
        return new UsernamePasswordAuthenticationToken(userPrincipal, "", null);
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

}
