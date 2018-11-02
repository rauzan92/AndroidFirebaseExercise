package mobile.com.androidfirebaseexercise.model;

import java.io.Serializable;

/**
 * Created by rauzan on 26/10/18.
 */
public class Cities implements Serializable {
    private String id;
    private boolean active;
    private String text;

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public String getText() {
        return text;
    }
}
