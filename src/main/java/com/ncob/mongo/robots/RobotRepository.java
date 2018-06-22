package com.ncob.mongo.robots;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RobotRepository extends MongoRepository<Robot, String>, RobotRepositoryCustom
{
    Robot findByUserName(String userName);
    Robot findByRobotName(String robotName);
    Robot findByUserNameAndRobotName(String userName, String robotName);
}
