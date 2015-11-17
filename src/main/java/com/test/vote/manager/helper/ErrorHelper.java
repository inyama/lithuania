package com.test.vote.manager.helper;

import com.test.vote.manager.dao.entity.UserRole;
import com.test.vote.manager.rest.dto.BackendErrors;
import com.test.vote.manager.rest.dto.DTOContainer;
import com.test.vote.manager.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class ErrorHelper {

    private static  ErrorHelper instance;

    private ErrorHelper(){

    }

    public static  ErrorHelper getInstance(){
        if (instance==null){
            instance= new ErrorHelper();
        }
        return  instance;
    }

    public boolean isAppropriateRole(UserRole role){
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         return userDetails.getUserData().getRole()==role;
    }

    public DTOContainer error() {
        DTOContainer result =new DTOContainer();
        result.setSuccess(false);
        result.setError(BackendErrors.FORBIDDEN_ERROR);
        return result;
    }

    public DTOContainer error(BackendErrors error) {
        DTOContainer result =new DTOContainer();
        result.setSuccess(false);
        result.setError(error);
        return result;
    }
}
