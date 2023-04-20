package cz.zcu.fav.kiv.authenticator.service;

import com.sun.security.auth.UserPrincipal;
import cz.zcu.fav.kiv.authenticator.dials.StatusCodes;
import cz.zcu.fav.kiv.authenticator.entit.JwtTokenProvider;
import cz.zcu.fav.kiv.authenticator.entit.User;
import cz.zcu.fav.kiv.authenticator.utils.JSONBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OAuth service
 * @version 1.0
 * @author Petr Urban, Jiri Trefil, Vaclav Hrabik
 */
@Service
public class Auth implements IAuth {

    /**
     * Class which manage JWT token
     */
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Method to call validation of JWT token
     * @param headers       header of request for authentication
     * @return              ResponseEntity<String>
     *                          200 + MSG   - token is ok
     *                          401         - token is in valid
     */
    @Override
    public ResponseEntity<String> refreshToken(HttpHeaders headers) {
        //vytahnu token

        String authHeaders = headers.getFirst(HttpHeaders.AUTHORIZATION);

        //validace tokenu
        if (authHeaders == null) {
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_TOKEN_INVALID.getStatusCode())).build();
        }
        String token = authHeaders.replace("Bearer ", "");
        boolean invalidated = jwtTokenProvider.invalidateToken(token);

        //vytahnu username z tela tokenu
        String userName = jwtTokenProvider.getNameFromToken(token);


        if(userName != null && invalidated) {
            //vygeneruju novej token s delsi zivotnosti
            //poslu uzivateli
            return generateJwt(userName,true);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Method to call validation of JWT token
     * @param authHeader    header of request for authentication
     * @return              ResponseEntity<String>
     *                          200 + MSG   - token is ok
     *                          401         - token is in valid
     */
    @Override
    public ResponseEntity<String> validateJwt(String authHeader) {
        final String token = authHeader.substring(7);
        String name = jwtTokenProvider.getNameFromToken(token);
        Map<String,Object> json = new HashMap<>();

        if (name == null) {
            json.put("message",StatusCodes.USER_TOKEN_INVALID.getLabel());
            String body = JSONBuilder.buildJSON(json);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        StatusCodes isValid = jwtTokenProvider.validateToken(token);

        json.put("message",isValid.getLabel());
        String body = JSONBuilder.buildJSON(json);
        return ResponseEntity.status(HttpStatus.valueOf(isValid.getStatusCode())).body(body);
    }

    /**
     * Method to call generation of new JWT token
     * @param userName name of the user that logged in and requires token
     * @return      ResponseEntity<String>
     *                  200 + token     - if everything is ok
     *                  401             - token creation failed
     */
    @Override
    public ResponseEntity<String> generateJwt(String userName, boolean refreshToken) {
        UserPrincipal userPrincipal = new UserPrincipal(userName);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, "", null);
        String token = jwtTokenProvider.generateToken(authentication, refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.TOKEN_CREATION_FAILED.getStatusCode()))
                    .body(StatusCodes.TOKEN_CREATION_FAILED.getLabel());
        }
        return ResponseEntity.ok().body(token);
    }

    /**
     * TODO [JT] mozna prepsat status code? Vracet 400 na absenci tokenu v userovi je spise na 401 - nekdo zkousi
     * TODO neco nekaleho, nebo je neco blbe na strane SPADEu
     * Method to call invalidation of users JWT token
     * @param user  User who wants to log out
     * @return      ResponseEntity<String>
     *                  200 + username      - if everything is ok
     *                  400                 - something went wrong with token
     */
    @Override
    public ResponseEntity<String> logout(User user){
        String token = user.getToken();
        HashMap<String,Object> json = new HashMap<>();
        String MSG = "Message";
        if(token == null || token.isEmpty()){
            json.put(MSG, StatusCodes.USER_LOGOUT_FAILED.getLabel());
            String jsonString = JSONBuilder.buildJSON(json);
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_LOGOUT_FAILED.getStatusCode()))
                    .body(jsonString);
        }
        jwtTokenProvider.invalidateToken(token);
        json.put(MSG, StatusCodes.USER_LOGGED_OUT.getLabel());
        String jsonString = JSONBuilder.buildJSON(json);
        return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_LOGGED_OUT.getStatusCode()))
                .body(jsonString);
    }

}
