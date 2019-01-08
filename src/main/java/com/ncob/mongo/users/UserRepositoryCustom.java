package com.ncob.mongo.users;

import org.springframework.security.authentication.BadCredentialsException;

public interface UserRepositoryCustom
{
    void registerUser(User user) throws BadCredentialsException;
    void addUserRobot(String userName, String robotName);
}
