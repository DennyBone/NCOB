package com.ncob.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "users")
//@Getter
//@Setter
//@NoArgsConstructor
public class User
{
    @Id
    public String id;

    @NotNull
    public String username;

    @NotNull
    public String password;

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

    @Override
    public String toString()
    {
        return String.format(
                "Customer[id=%s, username='%s']",
                id, username);
    }
}
