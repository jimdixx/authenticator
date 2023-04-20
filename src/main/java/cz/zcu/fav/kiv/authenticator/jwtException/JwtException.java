package cz.zcu.fav.kiv.authenticator.jwtException;

import cz.zcu.fav.kiv.authenticator.dials.JwtExceptionStatus;

/**
 * Runtime exception that gets thrown when error occurs while parsing jwt token
 * basically just a wrapper around generic RuntimeException to provide a bit more information
 * @author Jiri Trefil
 */
public class JwtException extends RuntimeException {

    private final JwtExceptionStatus status;
    public JwtException(JwtExceptionStatus status){
        this.status = status;
    }

    public int getHttpCode(){return status.getStatusCode();}
    public String getExceptionMessage(){return status.getMessage();}

}
