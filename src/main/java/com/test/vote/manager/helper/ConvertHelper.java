package com.test.vote.manager.helper;

import org.dozer.DozerBeanMapper;
import org.dozer.converters.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class ConvertHelper<D, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertHelper.class);
    private DozerBeanMapper mapper = DozerManager.getInstance().getMapper();
    private Constructor<D> dConstructor;
    private Constructor<E> eConstructor;

    private static ConvertHelper instance;

    private ConvertHelper() {
    }

    public ConvertHelper(Class<D> clzD, Class<E> clzE) {
        try {
            this.dConstructor = clzD.getConstructor();
            this.eConstructor = clzE.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public List<D> getListDTO(Iterable<E> entities) throws ConversionException {
        List<D> result = new LinkedList<D>();
        try {
            for (E entity : entities) {
                D dto = dConstructor.newInstance();
                mapper.map(entity, dto);
                result.add(dto);
            }
        } catch (InstantiationException e) {
            LOGGER.error("InstantiationException:",e);
            throw new ConversionException(e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException:",e);
            throw new ConversionException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException:",e);
            throw new ConversionException(e);
        }
        return result;
    }

    public E getEntity(D dto) throws ConversionException {
        E result = null;
        try {
            result = eConstructor.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("InstantiationException:",e);
            throw new ConversionException(e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException:",e);
            throw new ConversionException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException:",e);
            throw new ConversionException(e);
        }
        mapper.map(dto, result);
        return result;
    }

    public void mixEntity(D dto, E mixWidth) {
        mapper.map(dto, mixWidth);
    }

    public D getDTO(E entity) throws ConversionException {
        D result = null;
        try {
            result = dConstructor.newInstance();
        } catch (InstantiationException e) {
            LOGGER.error("InstantiationException:",e);
            throw new ConversionException(e);
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException:",e);
            throw new ConversionException(e);
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException:",e);
            throw new ConversionException(e);
        }
        mapper.map(entity, result);
        return result;
    }
}
