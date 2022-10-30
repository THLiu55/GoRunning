package com.example.gorunning.sharings;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.gorunning.models.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class SharedViewModel extends ViewModel {

    private static double EARTH_RADIUS = 6371000;//赤道半径(单位m)

    MutableLiveData<User> user = new MutableLiveData<>();
    // location
    MutableLiveData<String> location = new MutableLiveData<>();
    MutableLiveData<MyLocationData> locData = new MutableLiveData<>();
    // weather
    MutableLiveData<String> weatherInfo = new MutableLiveData<>();
    MutableLiveData<String> temperature = new MutableLiveData<>();
    MutableLiveData<String> windScale = new MutableLiveData<>();
    MutableLiveData<String> windDirection = new MutableLiveData<>();
    MutableLiveData<String> humidity = new MutableLiveData<>();
    MutableLiveData<Integer> iconId = new MutableLiveData<>();
    MutableLiveData<String> recommendation = new MutableLiveData<>();
    MutableLiveData<List<Integer>> next24Weather = new MutableLiveData<>();
    // track
    MutableLiveData<List<LatLng>> track = new MutableLiveData<>();

    double distance;
    boolean startTrace = false;

    public MutableLiveData<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = new MutableLiveData<>(user);
    }

    public MutableLiveData<String> getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location.setValue(location);
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo.postValue(weatherInfo);
    }

    public MutableLiveData<String> getWeatherInfo() {
        return weatherInfo;
    }

    public MutableLiveData<String> getHumidity() {
        return humidity;
    }

    public MutableLiveData<String> getTemperature() {
        return temperature;
    }

    public MutableLiveData<String> getWindDirection() {
        return windDirection;
    }

    public MutableLiveData<String> getWindScale() {
        return windScale;
    }

    public void setHumidity(String humidity) {
        this.humidity.postValue(humidity);
    }

    public void setTemperature(String temperature) {
        this.temperature.postValue(temperature);
    }

    public void setWindScale(String windScale) {
        this.windScale.postValue(windScale);
    }

    public void setWindDirection(String windDirection) {
        this.windDirection.postValue(windDirection);
    }

    public MutableLiveData<Integer> getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId.postValue(iconId);
    }

    public void setRecommendation(String recommendation) {
        this.recommendation.postValue(recommendation);
    }

    public MutableLiveData<String> getRecommendation() {
        return recommendation;
    }

    public MutableLiveData<List<Integer>> getNext24Weather() {
        return next24Weather;
    }

    public void setNext24Weather(List<Integer> weathers) {
        next24Weather.postValue(weathers);
    }

    public MutableLiveData<MyLocationData> getLocData() {
        return locData;
    }

    public void setLocData(MyLocationData locData) {
        this.locData.postValue(locData);
    }

    public void setTrack(List<LatLng> track) {
        if (startTrace) {
            if (track.size() > 1) {
                this.track.postValue(track);
            }
        }
    }

    public MutableLiveData<List<LatLng>> getTrack() {
        return track;
    }

    public void resetRunningState() {
        startTrace = !startTrace;
        if (!startTrace) {
            distance = 0;
            track.setValue(new LinkedList<>());
        }
    }

    public double getDistance() {
        return distance;
    }

    public boolean getRunningState() {
        return startTrace;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
