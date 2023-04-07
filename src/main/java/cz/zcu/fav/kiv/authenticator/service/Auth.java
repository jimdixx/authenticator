package cz.zcu.fav.kiv.authenticator.service;

import com.sun.security.auth.UserPrincipal;
import cz.zcu.fav.kiv.authenticator.entit.JwtTokenProvider;
import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class Auth implements IAuth{

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @Override
    public ResponseEntity<String> validateJwt(User user, String token) {
        return null;
    }

    @Override
    public String authorized(User user) {
        return null;
    }

    @Override
    public String generateJwt(User user) {
//        Authentication authentication = (Authentication) new UserPrincipal(user.getName());
//        return jwtTokenProvider.generateToken();
        return null;
    }

}
