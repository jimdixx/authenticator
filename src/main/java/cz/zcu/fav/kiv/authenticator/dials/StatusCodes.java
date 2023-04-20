package cz.zcu.fav.kiv.authenticator.dials;

/**
 * Enum for status codes
 */
public enum StatusCodes {
    USER_TOKEN_VALID("User authorized",200),
    USER_TOKEN_INVALID("User token is invalid",401),
    USER_LOGGED_OUT("User logged out", 200),
    USER_LOGOUT_FAILED("Something went wrong!", 400),
    TOKEN_CREATION_FAILED("Something went wrong!", 500);

    private final String label;
    private final int statusCode;

    StatusCodes(String s, int i) {
        this.label = s;
        this.statusCode = i;
    }

    public int getStatusCode(){
        return this.statusCode;
    }
    public String getLabel(){
        return this.label;
    }

}
