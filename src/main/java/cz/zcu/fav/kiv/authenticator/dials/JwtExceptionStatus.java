package cz.zcu.fav.kiv.authenticator.dials;

/**
 * @author Jiri Trefil
 * Enum represents error state of jwt parser
 */
public enum JwtExceptionStatus {

    //Token is not signed by secret key of this application
    //user sent some strange token
    //raises red flag
    INVALID_SIGNATURE("Unrecognized token signature",401),
    //token that was sent by client is not properly constructed
    //raises red flag
    INVALID_TOKEN("Token is invalid.",400),
    //token is expired
    EXPIRED_TOKEN("Token expired.",401),
    //token that was provided is not jwt token
    UNSUPPORTED_TOKEN("Unrecognized token structure.",400);


    private final String message;
    private final int code;
    JwtExceptionStatus(String s, int i){
        this.message = s;
        this.code = i;
    }

    public String getMessage() {
        return message;
    }
    public int getStatusCode(){
        return code;
    }
}
