package com.ncob.mongo.users;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
//@Getter
//@Setter
//@NoArgsConstructor
public class User
{
    @Id
    private String id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    // a list of references to robot objects the user is subscribed to
    private List<String> userRobots = new ArrayList<String>();

    public User(){}

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getUserRobots() { return userRobots; }

    @Override
    public String toString()
    {
        return String.format(
                "User[id=%s, username='%s']",
                id, username);
    }
}
