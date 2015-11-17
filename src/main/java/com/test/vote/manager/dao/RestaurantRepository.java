package com.test.vote.manager.dao;

import com.test.vote.manager.dao.entity.RestaurantEntity;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<RestaurantEntity, Integer> {
}
