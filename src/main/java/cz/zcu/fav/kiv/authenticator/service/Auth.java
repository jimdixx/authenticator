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

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Override
    public ResponseEntity<String> refreshToken(HttpHeaders headers) {
        List<String> authHeaders = headers.get(HttpHeaders.AUTHORIZATION);

        if (authHeaders == null || authHeaders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_TOKEN_INVALID.getStatusCode())).build();
        }

        // TODO >> findFirst throws exception, we do not want this shit guys. (:
        Optional<String> bearerToken = authHeaders.stream()
                    .filter(header -> header.startsWith("Bearer "))
                    .findFirst();


        if (bearerToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_TOKEN_INVALID.getStatusCode())).build();
        }

        String token = bearerToken.get().replace("Bearer ", "");
        String userName = jwtTokenProvider.getNameFromToken(token);
        boolean invalidated = jwtTokenProvider.invalidateToken(token);

        if(userName != null && invalidated) {
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
        if (name == null) {
            return ResponseEntity.status(HttpStatus.valueOf(StatusCodes.USER_TOKEN_INVALID.getStatusCode()))
                    .body(StatusCodes.USER_TOKEN_INVALID.getLabel());
        }
        StatusCodes isValid = jwtTokenProvider.validateToken(token);
        return ResponseEntity.status(HttpStatus.valueOf(isValid.getStatusCode())).body(name);
    }

    /**
     * Method to call generation of new JWT token
     * @param user  User who wants to login
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
