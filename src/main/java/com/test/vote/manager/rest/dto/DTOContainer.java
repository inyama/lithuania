package com.test.vote.manager.rest.dto;

public class DTOContainer {
    private Boolean success;
    private BackendErrors error;

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public BackendErrors getError() {
        return error;
    }

    public void setError(BackendErrors error) {
        this.error = error;
    }
}
