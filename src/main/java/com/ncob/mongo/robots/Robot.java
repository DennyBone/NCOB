package com.ncob.mongo.robots;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ncob.mongo.users.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "robots")
public class Robot
{
    @Id
    public String id;

    @NotNull
    public String userName;

    @NotNull
    public String robotName;

    // list of references to subscribed users - may not use
    public final List<String> userIDs = new ArrayList<String>();

    // list of socket names for this robot

    // will declaring this final affect mongodb, lombok, etc in any way?
    // what about removing the getters/setters?
    public final List<String> socketNames = new ArrayList<String>();

    public Robot(){}

    public Robot(String userName, String robotName)
    {
        this.userName = userName;
        this.robotName = robotName;
    }

    public void addSocket(String socketName)
    {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }
}
