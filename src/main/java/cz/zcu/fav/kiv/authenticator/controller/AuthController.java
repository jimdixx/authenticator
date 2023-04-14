package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.dials.StatusCodes;
import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints of authenticator application
 * @version 1.0
 * @author Petr Uerban, Jiri Trefil, Vaclav Hrabik
 */
@RestController
public class AuthController {

    /**
     * Instance of oAuth service
     */
    @Autowired
    private IAuth oAuth;

    /**
     * endpoint for login user
     * @param user  user with name
     * @return      ResponseEntity<String>
     *                  200 + token     - if everything is ok
     *                  401             - token creation failed
     */
    @PostMapping("/login")
    ResponseEntity<String> handleSingIn(@RequestBody User user) {
        return oAuth.generateJwt(user);
    }

    /**
     * endpoint for authentication of user
     * @param headers   request with "Authorization" key and "Bearer token" as value in header
     * @return          ResponseEntity<String>
     *                      200 + MSG   - token is ok
     *                      401         - token is in valid
     */
    @PostMapping(value = "/authenticate", produces = "application/json")
    ResponseEntity<String> authenticate(@RequestHeader HttpHeaders headers) {
        final String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            //chyba
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_LOGOUT_FAILED.getStatusCode()))
                    .body(StatusCodes.USER_LOGOUT_FAILED.getLabel());
        }
        return oAuth.validateJwt(authHeader);
    }

    /**
     * endpoint of logout the user
     * @param user  user with token
     * @return      ResponseEntity<String>
     *                  200 + username      - if everything is ok
     *                  400                 - something went wrong with token
     */
    @PostMapping(value="/logout",produces = "application/json")
    ResponseEntity<String> logout(@RequestBody User user) {
        return oAuth.logout(user);
    }


}
