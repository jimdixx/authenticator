package cz.zcu.fav.kiv.authenticator.service;

import cz.zcu.fav.kiv.authenticator.dials.UserModelStatusCodes;
import cz.zcu.fav.kiv.authenticator.entit.User;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;

public interface IAuth {

    UserModelStatusCodes validateJwt(String token);

    String generateJwt(User user);

    boolean logout(User user);

    String getUserName(String token);

}

