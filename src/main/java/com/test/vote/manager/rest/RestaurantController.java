package com.test.vote.manager.rest;

import com.test.vote.manager.dao.MenuRepository;
import com.test.vote.manager.dao.RestaurantRepository;
import com.test.vote.manager.dao.entity.MenuEntity;
import com.test.vote.manager.dao.entity.RestaurantEntity;
import com.test.vote.manager.dao.entity.UserRole;
import com.test.vote.manager.helper.ConvertHelper;
import com.test.vote.manager.helper.DateController;
import com.test.vote.manager.helper.ErrorHelper;
import com.test.vote.manager.rest.dto.BackendErrors;
import com.test.vote.manager.rest.dto.DTOContainer;
import com.test.vote.manager.rest.dto.MenuDTO;
import com.test.vote.manager.rest.dto.RestaurantDTO;
import org.dozer.converters.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RestaurantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestaurantController.class);
    @Autowired
    private RestaurantRepository restaurantRepo;
    @Autowired
    private MenuRepository menuRepo;

    private ConvertHelper<RestaurantDTO, RestaurantEntity> helper =
            new ConvertHelper<RestaurantDTO, RestaurantEntity>(RestaurantDTO.class, RestaurantEntity.class);

    private ConvertHelper<MenuDTO, MenuEntity> menuHelper =
            new ConvertHelper<MenuDTO, MenuEntity>(MenuDTO.class, MenuEntity.class);


    @RequestMapping(value = "/restaurants", method = RequestMethod.GET)
    public DTOContainer getRestaurants() {
        Iterable<RestaurantEntity> restaurantRepoAll = restaurantRepo.findAll();
        DTOContainer result = new DTOContainer();
        try {
            result.setData(helper.getListDTO(restaurantRepoAll));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/restaurants/{id}", method = RequestMethod.GET)
    public DTOContainer getRestaurant(@PathVariable Integer id) {
        DTOContainer result = new DTOContainer();
        RestaurantEntity restaurantEntity = restaurantRepo.findOne(id);
        try {
            result.setData(helper.getDTO(restaurantEntity));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/restaurants", method = RequestMethod.POST)
    public DTOContainer addRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        DTOContainer result = new DTOContainer();
        RestaurantEntity restaurantEntity;
        try {
            restaurantEntity = helper.getEntity(restaurantDTO);
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        RestaurantEntity saved = restaurantRepo.save(restaurantEntity);
        try {
            result.setData(helper.getDTO(saved));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        result.setSuccess(true);
        return result;
    }


    @RequestMapping(value = "/restaurants/{id}/menu", method = RequestMethod.GET)
    public DTOContainer getMenu(@PathVariable Integer id) {
        long currentDay = DateController.getInstance().getCurrentDay();
        List<MenuEntity> menu = menuRepo.findByTimeAndRestaurantId(currentDay, id);
        DTOContainer result = new DTOContainer();
        if (menu != null) {
            try {
                result.setData(menuHelper.getListDTO(menu));
            } catch (ConversionException e) {
                LOGGER.error("Error while converts data", e);
                return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
            }
        } else {
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/restaurants/{id}/menu", method = RequestMethod.GET, params = {"currentTime"})
    public DTOContainer getMenu(@PathVariable Integer id, @RequestParam("currentTime") Long currentTime, @RequestBody MenuDTO restaurantDTO) {
        long currentDay = DateController.getInstance().getCurrentDay(currentTime);
        List<MenuEntity> menu = menuRepo.findByTimeAndRestaurantId(currentDay, id);
        DTOContainer result = new DTOContainer();
        if (menu != null) {
            try {
                result.setData(menuHelper.getListDTO(menu));
            } catch (ConversionException e) {
                LOGGER.error("Error while converts data", e);
                return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
            }
        } else {
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        result.setSuccess(true);
        return result;
    }

    @RequestMapping(value = "/restaurants/{id}/menu", method = RequestMethod.POST)
    public DTOContainer addMenu(@PathVariable Integer id, @RequestBody MenuDTO restaurantDTO) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        long currentDay = DateController.getInstance().getCurrentDay();
        DTOContainer result = new DTOContainer();
        MenuEntity menu;
        try {
            menu = menuHelper.getEntity(restaurantDTO);
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        menu.setTime(currentDay);
        menu.setRestaurantId(id);
        MenuEntity saved = menuRepo.save(menu);
        try {
            result.setData(menuHelper.getDTO(saved));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        result.setSuccess(true);
        return result;
    }


    @RequestMapping(value = "/restaurants/{id}/menu/{menuId}", method = RequestMethod.DELETE)
    public DTOContainer modifyMenu(@PathVariable Integer id, @PathVariable Integer menuId) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        menuRepo.delete(menuId);
        DTOContainer result = new DTOContainer();
        result.setSuccess(true);
        return result;
    }


    @RequestMapping(value = "/restaurants/{id}", method = RequestMethod.PUT)
    public DTOContainer modifyRestaurant(@PathVariable Integer id, @RequestBody RestaurantDTO restaurantDTO) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        RestaurantEntity one = restaurantRepo.findOne(id);
        int oldId = one.getId();
        helper.mixEntity(restaurantDTO, one);
        one.setId(oldId);
        RestaurantEntity saved = restaurantRepo.save(one);
        DTOContainer container = new DTOContainer();
        try {
            container.setData(helper.getDTO(saved));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        container.setSuccess(true);
        return container;
    }

    @RequestMapping(value = "/restaurants/{id}", method = RequestMethod.DELETE)
    public DTOContainer deleteRestaurant(@PathVariable Integer id) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        restaurantRepo.delete(id);
        DTOContainer result = new DTOContainer();
        result.setSuccess(true);
        return result;
    }
}
