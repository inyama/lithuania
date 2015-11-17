package com.test.vote.manager.rest;

import com.test.vote.manager.dao.VoteRepository;
import com.test.vote.manager.dao.entity.VoteEntity;
import com.test.vote.manager.helper.DateController;
import com.test.vote.manager.rest.dto.DTOContainer;
import com.test.vote.manager.rest.dto.StatisticItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class VoteStatisticController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoteStatisticController.class);
    @Autowired
    private VoteRepository voteRepository;

    @RequestMapping(value="/voteStatistics", method = RequestMethod.GET, params = {"restaurantId","currentTime"})
    public DTOContainer getVoteStatisticsAtTime(@RequestParam Integer restaurantId, @RequestParam Long currentTime){
        List<VoteEntity> byVoteDate = voteRepository.findByVoteDateAndRestaurantId(DateController.getInstance().getCurrentDay(currentTime), restaurantId);
        DTOContainer result = new DTOContainer();
        StatisticItemDTO dto = new StatisticItemDTO();
        dto.setRestaurantId(restaurantId);
        dto.setVotesCount(byVoteDate.size());
        result.setData(dto);
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value="/voteStatistics", method = RequestMethod.GET)
    public DTOContainer getVotes(){
        List<VoteEntity> byVoteDate = voteRepository.findByVoteDate(DateController.getInstance().getCurrentDay());
        DTOContainer result = new DTOContainer();
        Map<Integer, Integer> statistics = new HashMap<Integer, Integer>();
        for (VoteEntity entity: byVoteDate){
            int restaurantId = entity.getRestaurantId();
            int count = 0;
            if (statistics.containsKey(restaurantId)){
                count = statistics.get(restaurantId);
            }
            count++;
            statistics.put(restaurantId,count);
        }
        List<StatisticItemDTO> statisticsList = new LinkedList<StatisticItemDTO>();
        for (Integer key :statistics.keySet()){
            StatisticItemDTO item = new StatisticItemDTO();
            item.setRestaurantId(key);
            item.setVotesCount(statistics.get(key));
            statisticsList.add(item);
        }
        result.setData(statisticsList);
        result.setSuccess(true);
        return result;
    }
}
