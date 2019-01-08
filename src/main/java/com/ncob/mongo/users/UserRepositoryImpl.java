package com.ncob.mongo.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImpl implements UserRepositoryCustom
{
    /*
    * This class implements the custom mongodb methods declared in the UserRepositoryCustom interface
    */

    @Autowired
    private UserRepository userRepository;

    public void addUserRobot(String userName, String robotName)
    {
        User user = userRepository.findByUsername(userName);
        if(!user.getUserRobots().contains(robotName))
        {
            //add robot to user object
            user.getUserRobots().add(robotName);
            userRepository.save(user);
        }
    }

    public void registerUser(User user) throws BadCredentialsException
    {
        // check if user already exists
        if(userRepository.findByUsername(user.getUsername()) != null)
        {
            // user already exists, return error
            throw new BadCredentialsException(user.getUsername() + "already exists");
        }
        else
        {
            // user not found, register a new user
            try
            {
                userRepository.insert(user);
            }
            catch(Exception e)
            {
                //log.error()
                System.out.println("Exception caught while registering controllers");
                e.printStackTrace();
            }
        }

        /*
        if(userRepository.findByUsername(controllers.getUsername()) != null)
        {
            // controllers already exists, return error
            return 1;
        }
        else
        {
            // email not found, register a new controllers
            try
            {
                userRepository.insert(controllers);
            }
            catch(Exception e)
            {
                //log.error()
                System.out.println("Exception caught while registering controllers");
                e.printStackTrace();
            }

            return 0;
        }
        */
    }
}
