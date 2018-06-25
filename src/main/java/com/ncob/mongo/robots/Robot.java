package com.ncob.mongo.robots;

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

    // may or may not use
    private String primaryUser;

    @NotNull
    private String robotName;

    // list of socket names for this robot
    // will declaring this final affect mongodb, lombok, etc in any way?
    private final List<String> socketNames = new ArrayList<String>();

    // list of references to subscribed users - may not use
    //public final List<String> userIDs = new ArrayList<String>();

    //public Robot(){}

    public Robot(String robotName)
    {
        this.robotName = robotName;
    }

    public void addSocket(String socketName)
    {
        if(!socketNames.contains(socketName))
        {
            socketNames.add(socketName);
        }
    }

    public String getPrimaryUser() {
        return primaryUser;
    }

    public void setPrimaryUser(String primaryUser) {
        this.primaryUser = primaryUser;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

}
