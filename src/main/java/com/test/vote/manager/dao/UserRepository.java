package com.test.vote.manager.dao;

import com.test.vote.manager.dao.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface  UserRepository extends CrudRepository<UserEntity, Integer> {
    UserEntity findByName(String name);
}
