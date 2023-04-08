package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;

public interface IAuth {

    boolean validateJwt(String token);

    String generateJwt(User user);

    boolean logout(User user);

}
