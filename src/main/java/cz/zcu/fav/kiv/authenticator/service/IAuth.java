package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;

public interface IAuth {

    ResponseEntity<String> validateJwt(String token);

    ResponseEntity<String> generateJwt(User user);

    ResponseEntity<String> logout(User user);
}

