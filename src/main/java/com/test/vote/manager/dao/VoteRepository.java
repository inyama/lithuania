package com.test.vote.manager.dao;

import com.test.vote.manager.dao.entity.VoteEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VoteRepository  extends CrudRepository<VoteEntity, Integer> {
    List<VoteEntity> findByVoteDate(Long voteDate);
    VoteEntity findFirstByVoteDateAndUserId(Long voteDate, Integer userId);
    List<VoteEntity> findByVoteDateAndRestaurantId(Long voteDate, Integer restaurantId);
}
