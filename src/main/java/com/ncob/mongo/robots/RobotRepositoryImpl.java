package com.ncob.mongo.robots;

import org.springframework.beans.factory.annotation.Autowired;
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
}
