package com.test.vote.manager.helper;

import org.dozer.DozerBeanMapper;

public class DozerManager {
    private static DozerManager instance;

    private DozerBeanMapper mapper;
    private DozerManager(){
        mapper = new DozerBeanMapper();
    }

    public static DozerManager getInstance(){
        if (instance==null){
            synchronized (DozerManager.class){
                instance = new DozerManager();
            }
        }
        return instance;
    }

    public DozerBeanMapper getMapper(){
        return mapper;
    }
}
