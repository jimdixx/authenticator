package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {

    @Autowired
    private IAuth auth;

    @PostMapping("/login")
    ResponseEntity<String> handleSingIn(@RequestBody User user) {
        String token = auth.generateJwt(user);
        // situations
        return ResponseEntity.ok().body(token);
    }

    @PostMapping(value = "/authenticate", produces = "application/json")
    ResponseEntity<String> authenticate(@RequestBody User user) {
        boolean isValid = auth.validateJwt(user.getToken());
        if(isValid) {
            return ResponseEntity.ok().body("{\"message\": \"OK\"}");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"Invalid token, please log in.\"}");
        // situations

    }

    @PostMapping("/logout")
    ResponseEntity<String> logout(@RequestBody User user) {
        boolean loggedOut = auth.logout(user);
        String message;
        if (loggedOut) {
            message = "{\"message\": \"OK\"}";
            return ResponseEntity.ok().body(message);
        }
        message = "{\"message\": \"Unknown error\"}";
        return ResponseEntity.internalServerError().body(message);
    }


}
