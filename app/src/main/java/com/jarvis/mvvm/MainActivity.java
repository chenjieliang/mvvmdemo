package com.jarvis.mvvm;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.jarvis.mvvm.repository.DataObserver;
import com.jarvis.mvvm.vm.WeatherViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        weatherViewModel =  ViewModelProviders.of(this).get(WeatherViewModel.class);
        weatherViewModel.getAreaLiveData().observe(this, new DataObserver<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }

            @Override
            public void onError(Throwable throwable) {
                textView.setText(throwable.getMessage());
            }
        });
        weatherViewModel.getWeather("china");
    }
}
