package cz.zcu.fav.kiv.authenticator.controller;

import cz.zcu.fav.kiv.authenticator.dials.UserModelStatusCodes;
import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.service.IAuth;
import cz.zcu.fav.kiv.authenticator.utils.JSONBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class AuthController {

    @Autowired
    private IAuth auth;

    @PostMapping("/login")
    ResponseEntity<String> handleSingIn(@RequestBody User user) {
        String token = auth.generateJwt(user);
        // situations
        return this.getResponseEntity(UserModelStatusCodes.USER_LOGGED_IN, token, "");
    }

    @PostMapping(value = "/authenticate", produces = "application/json")
    ResponseEntity<String> authenticate(@RequestHeader HttpHeaders headers, @RequestBody User user) {
        boolean isValid = auth.validateJwt(user.getToken());
        if(isValid) {
            return ResponseEntity.ok().body("{\"message\": \"OK\"}");
        }

        return ResponseEntity.ok().body("{\"message\": \"Invalid token, please log in.\"}");
        // situations

    }

    /**
     *
     * @param user
     * @return
     */
    @PostMapping(value="/logout",produces = "application/json")
    ResponseEntity<String> logout(@RequestBody User user) {
        boolean loggedOut = auth.logout(user);
        HashMap<String,Object> json = new HashMap<>();
        if (loggedOut) {
            json.put("message","ok");
            String jsonString = JSONBuilder.buildJSON(json);
            return ResponseEntity.ok().body(jsonString);
        }
        json.put("message","Internal error");
        String jsonString = JSONBuilder.buildJSON(json);
        return ResponseEntity.internalServerError().body(jsonString);
    }

    /**
     * Method to create response
     *
     * @param statusCode UserModelStatusCodes code
     * @return ResponseEntity with code and msg
     */
    private ResponseEntity<String> getResponseEntity(UserModelStatusCodes statusCode, String jwtToken, String body) {
        body += "," + generateResponseObject(statusCode, jwtToken);
        int code = statusCode.getStatusCode();
        ResponseEntity<String> response = new ResponseEntity<>(body, HttpStatus.valueOf(code));
        return response;
    }

    /**
     * Method to create JSON object
     *
     * @param code UserModelStatusCodes code
     * @return String that represents JSON object
     */
    private String generateResponseObject(UserModelStatusCodes code, String jwtToken) {
        HashMap<String, Object> json = new HashMap<>();
        if (jwtToken != null) {
            json.put("jwtToken", jwtToken);
        }
        return JSONBuilder.buildJSON(json);
    }

}
