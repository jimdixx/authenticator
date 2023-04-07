package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/authenticate")
    ResponseEntity<String> authenticate(@RequestBody User user) {
//        String token = auth.authorized(user);

        // situations
        return ResponseEntity.ok().body(auth.authorized(user));
    }
    //9e9259768a83c0604394f6d38fb9a


}
