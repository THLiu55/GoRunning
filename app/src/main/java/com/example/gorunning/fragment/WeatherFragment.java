package com.example.gorunning.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gorunning.R;
import com.example.gorunning.sharings.SharedViewModel;
import com.example.gorunning.utils.LineView;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Unit;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.LinkedList;
import java.util.List;

public class WeatherFragment extends Fragment {
    TextView locationInfo, weatherInfo, temperatureInfo, windScaleInfo, windDirectionInfo, humidityInfo, recommendInfo;
    ImageView weatherIcon;
    SharedViewModel sharedViewModel;
    LineView lineView;
    private boolean weatherChecked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationInfo = requireView().findViewById(R.id.location);
        weatherInfo = requireView().findViewById(R.id.weather_text);
        temperatureInfo = requireView().findViewById(R.id.temperature_text);
        windScaleInfo = requireView().findViewById(R.id.windscale_text);
        windDirectionInfo = requireView().findViewById(R.id.wind_direction_text);
        humidityInfo = requireView().findViewById(R.id.humidity_text);
        weatherIcon = requireView().findViewById(R.id.weather_icon);
        recommendInfo = requireView().findViewById(R.id.recommend_text);
        lineView= (LineView) requireView().findViewById(R.id.lineView);
        weatherChecked = false;
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getLocation().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String loc) {
                locationInfo.setText(loc);
                if (!weatherChecked) {
                    QWeather.getWeather24Hourly(requireActivity(), loc, Lang.EN, Unit.METRIC, new QWeather.OnResultWeatherHourlyListener() {
                        @Override
                        public void onError(Throwable throwable) {
                            Log.d("error message", throwable.getMessage());
                        }

                        @Override
                        public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                            List<WeatherHourlyBean.HourlyBean> weathers = weatherHourlyBean.getHourly();
                            WeatherHourlyBean.HourlyBean currentWeather = weathers.get(0);
                            sharedViewModel.setWeatherInfo(currentWeather.getText());
                            sharedViewModel.setTemperature(currentWeather.getTemp());
                            sharedViewModel.setHumidity(currentWeather.getHumidity());
                            sharedViewModel.setWindDirection(currentWeather.getWindDir());
                            sharedViewModel.setWindScale(currentWeather.getWindScale());
                            setWeatherIcon(currentWeather.getIcon());
                            List<Integer> next24Weather = new LinkedList<>();
                            for (int i = 0; i < 8; i++) {
                                next24Weather.add(Integer.parseInt(weathers.get(i).getTemp()));
                            }
                            sharedViewModel.setNext24Weather(next24Weather);
                            weatherChecked = true;
                        }
                    });
                }
            }
        });

        sharedViewModel.getWeatherInfo().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String info) {
                weatherInfo.setText(info);
            }
        });

        sharedViewModel.getTemperature().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                temperatureInfo.setText(s);
            }
        });

        sharedViewModel.getWindScale().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                windScaleInfo.setText(s);
            }
        });

        sharedViewModel.getWindDirection().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                windDirectionInfo.setText(s);
            }
        });

        sharedViewModel.getHumidity().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                humidityInfo.setText(s);
            }
        });

        sharedViewModel.getIconId().observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                weatherIcon.setImageResource(integer);
            }
        });

        sharedViewModel.getRecommendation().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                recommendInfo.setText(s);
            }
        });

        sharedViewModel.getNext24Weather().observe(requireActivity(), new Observer<List<Integer>>() {
            @Override
            public void onChanged(List<Integer> integers) {
                List<LineView.Point> points = new LinkedList<>();
                for (int i = 0; i < integers.size(); i++) {
                    points.add(new LineView.Point(i, integers.get(i)));
                }
                lineView.setmListPoint(points);
                lineView.postInvalidate();
            }
        });
    }

    void setWeatherIcon(String s) {
        int code = Integer.parseInt(s);
        if (code >= 300 && code < 400) {
            sharedViewModel.setIconId(R.drawable.rainny);
            sharedViewModel.setRecommendation("Running is not recommended today");
        } else if (code == 100 || code == 150 || code == 900 || code == 901) {
            sharedViewModel.setIconId(R.drawable.sunny);
            sharedViewModel.setRecommendation("Today is suitable for running");
        } else if (code < 300) {
            if (code == 101 || code == 104) {
                sharedViewModel.setIconId(R.drawable.clouds);
            } else {
                sharedViewModel.setIconId(R.drawable.sun_cloud);
            }
            sharedViewModel.setRecommendation("Today is suitable for running");
        } else if (code < 500) {
            sharedViewModel.setIconId(R.drawable.snow);
            sharedViewModel.setRecommendation("Running is not recommended today");
        } else if (code <= 515) {
            sharedViewModel.setIconId(R.drawable.clouds);
            sharedViewModel.setRecommendation("Running is not recommended today");
        } else {
            sharedViewModel.setIconId(R.drawable.sunny);
            sharedViewModel.setRecommendation("Today is suitable for running");
        }
    }
}