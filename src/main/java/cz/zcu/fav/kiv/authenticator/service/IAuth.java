package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;

public interface IAuth {

    ResponseEntity<String> signIn(User user);

    ResponseEntity<String> signUp(User user);

    ResponseEntity<String> validateJwt(User user, String token);

    boolean authorized(User user);

    String generateJwt(User user);

}
