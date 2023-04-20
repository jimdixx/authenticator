package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.dials.StatusCodes;
import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints of authenticator application
 * @version 1.0
 * @author Petr Urban, Jiri Trefil, Vaclav Hrabik
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
        return oAuth.generateJwt(user.getName(), false);
    }

    /**
     * endpoint for authentication of user
     * @param headers   request with "Authorization" key and "Bearer token" as value in header
     * @return          ResponseEntity<String>
     *                      200 + MSG   - token is ok
     *                      401         - token is in valid
     */
    @PostMapping(value = "/authenticate", produces =  MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> authenticate(@RequestHeader HttpHeaders headers) {
        final String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer")) {
            //user request does not have authorization-bearer key value pair - kill it
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_TOKEN_INVALID.getStatusCode()))
                    .body(StatusCodes.USER_TOKEN_INVALID.getLabel());
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

    /**
     * endpoint to get token with long lifespan
     * @param headers   header of request with token
     * @return          ResponseEntity<String>
     *                      200 + new token   - if everything is ok
     *                      401               - send token is in valid
     */
    @GetMapping(value = "/refresh", produces = "application/json")
    ResponseEntity<String> refreshToken(@RequestHeader HttpHeaders headers) {
        return oAuth.refreshToken(headers);
    }

}
