package com.ncob.mongo;

import org.springframework.security.authentication.BadCredentialsException;

public interface UserRepositoryCustom
{
    void registerUser(User user) throws BadCredentialsException;
}
