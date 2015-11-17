package com.test.vote.manager.rest;

import com.test.vote.manager.rest.dto.BackendErrors;
import com.test.vote.manager.rest.dto.DTOContainer;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public DTOContainer error() {
        DTOContainer result =new DTOContainer();
        result.setSuccess(false);
        result.setError(BackendErrors.UNAUTHORISED_ERROR);
        return result;
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
