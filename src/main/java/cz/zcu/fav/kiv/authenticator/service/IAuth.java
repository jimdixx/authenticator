package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface IAuth {

    ResponseEntity<String> validateJwt(String token);

    ResponseEntity<String> generateJwt(String userName, boolean refreshToken);

    ResponseEntity<String> logout(User user);

    ResponseEntity<String> refreshToken(HttpHeaders headers);
}

