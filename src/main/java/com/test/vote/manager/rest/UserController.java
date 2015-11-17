package com.test.vote.manager.rest;

import com.test.vote.manager.dao.UserRepository;
import com.test.vote.manager.dao.entity.UserEntity;
import com.test.vote.manager.dao.entity.UserRole;
import com.test.vote.manager.dao.entity.VoteEntity;
import com.test.vote.manager.helper.ConvertHelper;
import com.test.vote.manager.helper.DozerManager;
import com.test.vote.manager.helper.ErrorHelper;
import com.test.vote.manager.rest.dto.BackendErrors;
import com.test.vote.manager.rest.dto.DTOContainer;
import com.test.vote.manager.rest.dto.UserDTO;
import com.test.vote.manager.rest.dto.VoteDTO;
import com.test.vote.manager.security.CustomUserDetails;
import org.dozer.DozerBeanMapper;
import org.dozer.converters.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserRepository userRepository;

    private ConvertHelper<UserDTO, UserEntity> helper =
            new ConvertHelper<UserDTO, UserEntity>(UserDTO.class, UserEntity.class);

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public DTOContainer getUsers() {
        List<UserDTO> result = new LinkedList<UserDTO>();
        Iterable<UserEntity> allUsers = userRepository.findAll();
        boolean appropriateRole = ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN);
        DTOContainer container = new DTOContainer();
        try {
            for (UserEntity user : allUsers) {
                UserDTO dto = helper.getDTO(user);
                if (!appropriateRole){
                    dto.setPassword(null);
                }
                result.add(dto);
            }
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        container.setSuccess(true);
        container.setData(result);
        return container;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public DTOContainer addUser(@RequestBody UserDTO user) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        UserEntity entity = new UserEntity();
        DozerManager.getInstance().getMapper().map(user, entity);
        UserEntity save = userRepository.save(entity);
        DTOContainer container = new DTOContainer();
        try {
            container.setData(helper.getDTO(save));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        container.setSuccess(true);
        return container;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public DTOContainer getUserById(@PathVariable Integer id) {
        UserEntity userEntity = userRepository.findOne(id);
        boolean appropriateRole = ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN);
        if (userEntity==null){
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        DTOContainer container = new DTOContainer();
        try {
            UserDTO dto = helper.getDTO(userEntity);
            CustomUserDetails userDetails =
                    (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!appropriateRole && userDetails.getUserData().getId()!=id){
                dto.setPassword(null);
            }
            container.setData( dto);
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        container.setSuccess(true);
        return container;
    }


    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public DTOContainer updateUser(@PathVariable Integer id, @RequestBody UserDTO user) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        UserEntity userEntity = userRepository.findOne(id);
        if (userEntity==null){
            LOGGER.error("Entity not found");
            return ErrorHelper.getInstance().error(BackendErrors.NOT_FOUND_ERROR);
        }
        int oldId = userEntity.getId();
        helper.mixEntity(user, userEntity);
        userEntity.setId(oldId);
        UserEntity modified = userRepository.save(userEntity);
        DTOContainer container = new DTOContainer();
        try {
             container.setData(helper.getDTO(modified));
        } catch (ConversionException e) {
            LOGGER.error("Error while converts data", e);
            return ErrorHelper.getInstance().error(BackendErrors.CONVERSION_ERROR);
        }
        container.setSuccess(true);
        return container;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public DTOContainer deleteUser(@PathVariable Integer id) {
        if (!ErrorHelper.getInstance().isAppropriateRole(UserRole.ROLE_ADMIN)) {
            return ErrorHelper.getInstance().error();
        }
        userRepository.delete(id);
        DTOContainer result = new DTOContainer();
        result.setSuccess(true);
        return result;
    }


}
