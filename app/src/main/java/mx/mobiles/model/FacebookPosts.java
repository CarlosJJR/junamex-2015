package mx.mobiles.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by carlosjimenez on 07/07/15.
 */
@ParseClassName("FacebookPosts")
public class FacebookPosts extends ParseObject{

    public static final String FACEBOOK_ID = "facebookId";
    public static final String COUNTER = "postCounter";
    public static final String USER_NAME = "userName";

    public FacebookPosts() {
        super();
    }

    public String getFacebookId() {
        return getString(FACEBOOK_ID);
    }

    public FacebookPosts setFacebookId(String facebookId) {
        put(FACEBOOK_ID, facebookId);
        return this;
    }

    public int getPostCounter() {
        return getInt(COUNTER);
    }

    public FacebookPosts setPostCounter(int postCounter) {
        put(COUNTER, postCounter);
        return this;
    }

    public String getUserName() {
        return getString(USER_NAME);
    }

    public FacebookPosts setUserName(String userName) {
        put(USER_NAME, userName);
        return this;
    }
}
