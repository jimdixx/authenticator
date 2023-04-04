package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private IAuth auth;

    @PostMapping("/createJwt")
    ResponseEntity<String> handleSingIn(@RequestBody User user) {
        String token = auth.generateJwt(user);

        // situations
        return ResponseEntity.ok().body(auth.generateJwt(user));
    }

}
