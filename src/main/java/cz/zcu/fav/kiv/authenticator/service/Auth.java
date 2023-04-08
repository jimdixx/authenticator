package cz.zcu.fav.kiv.authenticator.service;

import com.sun.security.auth.UserPrincipal;
import cz.zcu.fav.kiv.authenticator.entit.JwtTokenProvider;
import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class Auth implements IAuth {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Override
    public boolean validateJwt(String token) {
        return jwtTokenProvider.validateToken(token);
    }


    @Override
    public String generateJwt(User user) {
        UserPrincipal userPrincipal = new UserPrincipal(user.getName());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, "", null);
        return jwtTokenProvider.generateToken(authentication);
    }


    @Override
    public boolean logout(User user){
        String token = user.getToken();
        if(token != null && !token.isEmpty()){
            return false;
        }
        return jwtTokenProvider.invalidateToken(token);
    }

}
