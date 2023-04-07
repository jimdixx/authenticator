package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;

public interface IAuth {

    ResponseEntity<String> validateJwt(User user, String token);

    String authorized(User user);

    String generateJwt(User user);


}
