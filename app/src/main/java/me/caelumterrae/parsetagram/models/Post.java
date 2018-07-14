package me.caelumterrae.parsetagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    //TODO: add proper variables to POST object

    public Post(){}

    public ParseFile getMedia() {
        return getParseFile("media");
    }

    public void setMedia(ParseFile parseFile) {
        put("media", parseFile);
    }

    public void setUser(ParseUser user){
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public void setUsername(String username) {
        put("username", username);
    }

    public String getUsername(){
        return getString("username");
    }

    public void setUserID(String id){
        put("userID", id);
    }

    public String getUserID(){
        return getString("userID");
    }

}