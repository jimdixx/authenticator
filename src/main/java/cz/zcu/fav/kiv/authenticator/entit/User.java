package cz.zcu.fav.kiv.authenticator.entit;

/**
 * Class of the user who want something
 * @version 1.0
 * @author Vaclav Hrabik, Jiri Trefil
 */
public class User {

    /**
     * name of the user
     */
    private String name;

    /**
     * Jwt token of the user
     */
    private String token;

    /**
     * construktor of the user
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * construktor of the user
     */
    public User(String name, String token) {
        this.name = name;
        this.token = token;
    }

    /**
     * Getter of name
     *
     * @return name of the user
     */
    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }



}