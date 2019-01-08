package com.ncob.mongo.robots;

import com.ncob.mongo.users.User;
import com.ncob.mongo.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class RobotRepositoryImpl implements RobotRepositoryCustom
{
    @Autowired
    private RobotRepository robotRepository;

    public void persistRobot(Robot robot)
    {
        // add robot object to DB
        robotRepository.insert(robot);
    }

    public void registerRobot(Robot robot) throws BadCredentialsException {
        // check if robot already exists
        if(robotRepository.findByRobotName(robot.getRobotName()) != null)
        {
            // robot already exists, return error
            throw new BadCredentialsException(robot.getRobotName() + "already exists");
        }
        else
        {
            // robot not found, register a new robot
            try {
                persistRobot(robot);
            } catch (Exception e) {
                //log.error()
                System.out.println("Exception caught while registering robot");
                e.printStackTrace();
            }
        }
    }

}
