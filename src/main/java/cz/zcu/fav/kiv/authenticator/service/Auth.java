package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Auth implements IAuth{


    @Override
    public ResponseEntity<String> validateJwt(User user, String token) {
        return null;
    }

    @Override
    public boolean authorized(User user) {
        return false;
    }

    @Override
    public String generateJwt(User user) {
        return null;
    }

}
