package com.codezilla;

public class WeatherRVmodel {

    private String time;
    private String temperature;
    private String icon;
    private String condition;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }



    public WeatherRVmodel(String time, String temperature, String icon, String windspeed) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.condition = windspeed;
    }
}
