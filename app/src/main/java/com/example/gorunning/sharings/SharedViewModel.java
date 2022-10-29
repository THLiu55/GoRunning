package com.example.gorunning.sharings;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.gorunning.models.User;

import java.util.List;

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
            this.track.postValue(track);
            for (LatLng latLng : track) {
                System.out.print(latLng.latitude + ", " +  latLng.longitude + "; ");
            }
            System.out.println();
            if (track.size() > 1) {
                LatLng prev = track.get(track.size() - 2);
                LatLng now = track.get(track.size() - 1);
                distance += getDistanceByLoc(prev.longitude, prev.latitude, now.longitude, now.latitude);
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
        }
    }


    /**
     * 转化为弧度(rad)
     * */
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    /**
     * @param lon1 第一点的精度
     * @param lat1 第一点的纬度
     * @param lon2 第二点的精度
     * @param lat2 第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static double getDistanceByLoc(double lon1, double lat1, double lon2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lon1) - rad(lon2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public double getDistance() {
        return distance;
    }

    public boolean getRunningState() {
        return startTrace;
    }
}
