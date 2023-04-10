package cz.zcu.fav.kiv.authenticator.dials;

/**
 * Enum for status codes
 */
public enum UserModelStatusCodes {
    USER_TOKEN_VALID("User authorize",200),
    USER_TOKEN_INVALID_SIGNATURE("User token has invalid signature",401),
    USER_TOKEN_INVALID("User token is invalid",401),
    USER_TOKEN_EXPIRED("User token is expired",401),
    USER_TOKEN_UNSUPPORTED("User token is unsupported",401),
    USER_TOKEN_EMPTY_OR_NULL("User token is empty or null",401);

    private final String label;
    private final int statusCode;

    UserModelStatusCodes(String s, int i) {
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
