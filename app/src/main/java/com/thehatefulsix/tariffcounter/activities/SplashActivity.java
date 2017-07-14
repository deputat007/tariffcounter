package com.thehatefulsix.tariffcounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thehatefulsix.tariffcounter.R;
import com.thehatefulsix.tariffcounter.activities.core.ActivityWithMethods;
import com.thehatefulsix.tariffcounter.models.City;
import com.thehatefulsix.tariffcounter.models.House;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.ServiceProvider;
import com.thehatefulsix.tariffcounter.utils.RealmHelper;
import com.thehatefulsix.tariffcounter.utils.ServerConnectivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends Activity {
    private static final long SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityWithMethods.setStatusBarColor(this);

        test();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if (RealmHelper.getInstance().getAll(House.class).size() == 0){
                    startActivity(HouseSettingsActivity.newIntent(SplashActivity.this, null));
                }else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }

                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    public void onBackPressed() {}

    // Server Test
    private void test() {
        final List<City> cities = new ArrayList<>();
        final List<ServiceProvider> serviceProviders = new ArrayList<>();

        ServerConnectivity.getAllCity(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                cities.addAll(response.body());
                Log.d("getAllCity", String.valueOf(cities));

                ServerConnectivity.getCityById(cities.get(0).getId(), new Callback<City>() {
                    @Override
                    public void onResponse(Call<City> call, Response<City> response) {
                        Log.d("getCityById(" + cities.get(0).getId() + ")",
                                String.valueOf(response.body()));
                    }

                    @Override
                    public void onFailure(Call<City> call, Throwable t) {
                        Log.d("getCityById(" + cities.get(0).getId() + ")", "onFailure");
                    }
                });
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.d("getAllCity", "onFailure");
            }
        });

        ServerConnectivity.getAllServiceProvider(new Callback<List<ServiceProvider>>() {
            @Override
            public void onResponse(Call<List<ServiceProvider>> call, Response<List<ServiceProvider>> response) {
                serviceProviders.addAll(response.body());
                Log.d("getAllServiceProvider", String.valueOf(serviceProviders));

                ServerConnectivity.getServiceProvidersForCity(cities.get(0).getId(), new Callback<List<ServiceProvider>>() {
                    @Override
                    public void onResponse(Call<List<ServiceProvider>> call, Response<List<ServiceProvider>> response) {
                        serviceProviders.clear();
                        if (response.body() != null) {
                            serviceProviders.addAll(response.body());
                        }
                        Log.d("getServiceProvidersForCity(" + cities.get(0).getId() + ")", String.valueOf(serviceProviders));
                    }

                    @Override
                    public void onFailure(Call<List<ServiceProvider>> call, Throwable t) {
                        Log.d("getServiceProvidersForCity(" + cities.get(0).getId() + ")", "onFailure");
                    }
                });

                ServerConnectivity.getServiceProviderById(serviceProviders.get(0).getId(), new Callback<ServiceProvider>() {
                    @Override
                    public void onResponse(Call<ServiceProvider> call, Response<ServiceProvider> response) {
                        Log.d("getServiceProviderById(" + serviceProviders.get(0).getId() + ")",
                                String.valueOf(response.body()));

                        ServerConnectivity.getRateById(serviceProviders.get(0).getRate().getId(), new Callback<Rate>() {
                            @Override
                            public void onResponse(Call<Rate> call, Response<Rate> response) {
                                Log.d("getRateById(" + serviceProviders.get(0).getRate().getId() + ")",
                                        String.valueOf(response.body()));
                            }

                            @Override
                            public void onFailure(Call<Rate> call, Throwable t) {
                                Log.d("getRateById(" + serviceProviders.get(0).getRate().getId() + ")",
                                        "onFailure");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ServiceProvider> call, Throwable t) {
                        Log.d("getServiceProviderById(" + serviceProviders.get(0).getId() + ")", "onFailure");
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ServiceProvider>> call, Throwable t) {
                Log.d("getAllServiceProvider", "onFailure");
            }
        });
    }
}
