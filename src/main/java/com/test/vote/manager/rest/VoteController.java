package com.test.vote.manager.rest;

import com.test.vote.manager.dao.VoteRepository;
import com.test.vote.manager.dao.entity.VoteEntity;
import com.test.vote.manager.helper.ConvertHelper;
import com.test.vote.manager.helper.DateController;
import com.test.vote.manager.helper.ErrorHelper;
import com.test.vote.manager.rest.dto.BackendErrors;
import com.test.vote.manager.rest.dto.DTOContainer;
import com.test.vote.manager.rest.dto.VoteDTO;
import com.test.vote.manager.security.CustomUserDetails;
import org.dozer.converters.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class VoteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VoteController.class);
    @Autowired
    private VoteRepository voteRepository;

    private ConvertHelper<VoteDTO, VoteEntity> helper =
            new ConvertHelper<VoteDTO, VoteEntity>(VoteDTO.class, VoteEntity.class);


    @RequestMapping(value = "/votes", method = RequestMethod.GET, params = {"currentTime", "userId"})
    public DTOContainer getVoteFromUserAtTime(@RequestParam Long currentTime, @RequestParam Integer userId) {
        long currentDay = DateController.getInstance().getCurrentDay(currentTime);
        VoteEntity vote = voteRepository.findFirstByVoteDateAndUserId(currentDay, userId);
        DTOContainer result = new DTOContainer();
        if (vote == null) {
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        try {
            VoteDTO listDTO = helper.getDTO(vote);
            result.setSuccess(true);
            result.setData(listDTO);
            return result;
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
    }

    @RequestMapping(value = "/votes", method = RequestMethod.GET, params = {"currentTime", "restaurantId"})
    public DTOContainer getVoteForRestaurantAtTime(@RequestParam Long currentTime, @RequestParam Integer restaurantId) {
        long currentDay = DateController.getInstance().getCurrentDay(currentTime);
        List<VoteEntity> votes = voteRepository.findByVoteDateAndRestaurantId(currentDay, restaurantId);
        DTOContainer result = new DTOContainer();
        if (votes == null) {
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        result.setSuccess(true);
        try {
            result.setData(helper.getListDTO(votes));
            return result;
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
    }

    @RequestMapping(value = "/votes", method = RequestMethod.GET, params = {"restaurantId"})
    public DTOContainer getVoteForRestaurant(@RequestParam Integer restaurantId) {
        long currentDay = DateController.getInstance().getCurrentDay();
        List<VoteEntity> votes = voteRepository.findByVoteDateAndRestaurantId(currentDay, restaurantId);
        DTOContainer result = new DTOContainer();
        if (votes == null) {

            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        try {
            List<VoteDTO> listDTO = helper.getListDTO(votes);
            result.setSuccess(true);
            result.setData(listDTO);
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        return result;
    }

    @RequestMapping(value = "/votes", method = RequestMethod.GET, params = {"currentTime"})
    public DTOContainer getVotesAtTime(@RequestParam Long currentTime) {
        List<VoteEntity> votes = voteRepository.findByVoteDate(DateController.getInstance().getCurrentDay(currentTime));
        DTOContainer result = new DTOContainer();
        try {
            List<VoteDTO> listDTO = helper.getListDTO(votes);
            result.setSuccess(true);
            result.setData(listDTO);
            return result;
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
    }

    @RequestMapping(value = "/votes/{id}", method = RequestMethod.GET)
    public DTOContainer getVotesForRestaurant(@PathVariable Integer id) {
        VoteEntity one = voteRepository.findOne(id);
        DTOContainer result = new DTOContainer();
        result.setSuccess(false);
        if (one == null) {
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        result.setSuccess(true);
        try {
            result.setData(helper.getDTO(one));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        return result;
    }

    @RequestMapping(value = "/votes", method = RequestMethod.GET)
    public DTOContainer getVotes() {
        List<VoteEntity> votes = voteRepository.findByVoteDate(DateController.getInstance().getCurrentDay());
        DTOContainer result = new DTOContainer();
        try {
            List<VoteDTO> listDTO = helper.getListDTO(votes);
            result.setSuccess(true);
            result.setData(listDTO);
            return result;
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
    }

    @RequestMapping(value = "/votes", method = RequestMethod.POST)
    public DTOContainer doVote(@RequestBody VoteDTO voteDTO) {
        DTOContainer result = new DTOContainer();
        VoteEntity entity;
        try {
            entity = helper.getEntity(voteDTO);
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = userDetails.getUserData().getId();
        VoteEntity voteEntity = voteRepository.findFirstByVoteDateAndUserId(DateController.getInstance().getCurrentDay(), userId);
        if (voteEntity == null) {
            entity.setUserId(userId);
            entity.setVoteDate(DateController.getInstance().getCurrentDay());
            VoteEntity saved = voteRepository.save(entity);
            result.setData(helper.getDTO(saved));
        } else {
            LOGGER.error("Entity is already presented");
            return ErrorHelper.getInstance().error(BackendErrors.ALREADY_PRESENT_ERROR);
        }
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/votes", method = RequestMethod.PUT)
    public DTOContainer doVoteAgain(@RequestBody VoteDTO voteDTO) {
        DTOContainer result = new DTOContainer();
        result.setSuccess(false);
        if (DateController.getInstance().isAppropriateTime()) {
            CustomUserDetails userDetails =
                    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int userId = userDetails.getUserData().getId();
            VoteEntity voteEntity = voteRepository.findFirstByVoteDateAndUserId(DateController.getInstance().getCurrentDay(), userId);
            if (voteEntity == null) {
                try {
                    voteEntity = helper.getEntity(voteDTO);
                } catch (ConversionException e) {
                    LOGGER.error("Error while converts data", e);
                    return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
                }
                voteEntity.setUserId(userId);
                voteEntity.setVoteDate(DateController.getInstance().getCurrentDay());
            } else {
                int oldId = voteEntity.getId();
                helper.mixEntity(voteDTO, voteEntity);
                voteEntity.setUserId(userId);
                voteEntity.setId(oldId);
                voteEntity.setVoteDate(DateController.getInstance().getCurrentDay());
            }
            VoteEntity saved = voteRepository.save(voteEntity);
            try {
                result.setData(helper.getDTO(saved));
            } catch (ConversionException e) {
                LOGGER.error("Error while converts data", e);
                return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
            }
        } else {
            LOGGER.error("Time period error");
            return ErrorHelper.getInstance().error(BackendErrors.INAPPROPRIATE_TIME_ERROR);
        }
        result.setSuccess(true);
        return result;
    }


}
