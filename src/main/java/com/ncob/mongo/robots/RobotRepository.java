package com.ncob.mongo.robots;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RobotRepository extends MongoRepository<Robot, String>, RobotRepositoryCustom
{
    Robot findByPrimaryUser(String primaryUser);
    Robot findByRobotName(String robotName);
    Robot findByPrimaryUserAndRobotName(String primaryUser, String robotName);
}
