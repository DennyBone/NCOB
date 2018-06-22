package com.ncob.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom
{
    User findByUsername(String username);
}
