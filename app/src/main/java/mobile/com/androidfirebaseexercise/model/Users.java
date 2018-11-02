package mobile.com.androidfirebaseexercise.model;

import java.io.Serializable;

/**
 * Created by rauzan on 26/10/18.
 */
public class Users implements Serializable {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String userKeyID;
    private String androidID;
    private String role;
    private Cities cities;

    public Users() {
    }

    public Users(String id, String name, String email, String phoneNumber, String userKeyID, String androidID, String role, Cities cities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userKeyID = userKeyID;
        this.androidID = androidID;
        this.role = role;
        this.cities = cities;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserKeyID() {
        return userKeyID;
    }

    public String getRole() {
        return role;
    }

    public Cities getCities() {
        return cities;
    }

    public String getAndroidID() {
        return androidID;
    }
}
