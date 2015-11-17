package com.test.vote.manager.dao;

import com.test.vote.manager.dao.entity.MenuEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MenuRepository extends CrudRepository<MenuEntity,Integer>{
    List<MenuEntity> findByTimeAndRestaurantId(Long time, Integer restaurantId);
}
