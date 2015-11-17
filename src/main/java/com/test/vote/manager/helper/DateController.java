package com.test.vote.manager.helper;

import java.time.*;

public class DateController {
    private static DateController instance;
    private int day;
    private LocalDateTime presetTime;
    private boolean isOverride;

    private DateController(){

    }
    public static DateController getInstance(){
        if (instance == null){
            synchronized (DateController.class){
                if (instance==null){
                    instance = new DateController();
                }
            }
        }
        return instance;
    }

    public LocalDateTime getCurrentTime(){
        if (!isOverride){
            return LocalDateTime.now();
        }
        return presetTime;
    }

    public void notOverride(){
        isOverride = false;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setPresetTime(LocalDateTime presetTime){
        this.presetTime = presetTime;
        isOverride = true;
    }


    public long getCurrentDay(){
        LocalDateTime currentTime = getCurrentTime();
        LocalDate localDate =  LocalDate.of(currentTime.getYear(), currentTime.getMonth(), currentTime.getDayOfMonth());
        return  localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public long getCurrentDay(Long currentTime){
        LocalDateTime tempDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentTime), ZoneId.systemDefault());
        LocalDate localDate =  LocalDate.of(tempDateTime.getYear(), tempDateTime.getMonth(), tempDateTime.getDayOfMonth());
        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public boolean isAppropriateTime(){
        LocalDateTime currentTime = getCurrentTime();
        return currentTime.getHour()<=11;
    }

}
