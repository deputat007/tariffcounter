package com.thehatefulsix.tariffcounter.utils;


import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.thehatefulsix.tariffcounter.models.Bill;
import com.thehatefulsix.tariffcounter.models.City;
import com.thehatefulsix.tariffcounter.models.Rate;
import com.thehatefulsix.tariffcounter.models.ServiceProvider;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ServerConnectivity {
    private static final String BASE_URL = "http://46.201.227.187:8080/";

    private static final String ALL_SERVICE_PROVIDERS = "getAllServiceProviderList";
    private static final String SERVICE_PROVIDER_FOR_CITY = "getServiceProviderListForCity";
    private static final String SERVICE_PROVIDER = "serviceProvider";

    private static final String ALL_CITIES = "getAllCityList";
    private static final String CITY = "city";

    private static final String SEND_BILL = "billdummy";

    private static final String RATE_BY_ID = "rate";

    private static <T> T createServer(final Class<T> aClass){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(aClass);
    }

    public static void getAllCity(@NonNull final Callback<List<City>> callback){
        createServer(CityService.class).listCity().enqueue(callback);
    }

    public static void getCityById(@NonNull final String id,
                                   @NonNull final Callback<City> callback){
        createServer(CityService.class).city(id).enqueue(callback);
    }

    public static void getAllServiceProvider(@NonNull final Callback<List<ServiceProvider>> callback){
        createServer(ServiceProviderService.class).allServiceProvider().enqueue(callback);
    }

    public static void getServiceProviderById(@NonNull final String id,
                                              @NonNull final Callback<ServiceProvider> callback){
        createServer(ServiceProviderService.class).serviceProvider(id).enqueue(callback);
    }

    public static void getServiceProvidersForCity(@NonNull final String cityId,
                                                  @NonNull final Callback<List<ServiceProvider>> callback){
        createServer(ServiceProviderService.class).listServiceProvider(cityId).enqueue(callback);
    }

    public static void getRateById(@NonNull final String id,
                                   @NonNull final Callback<Rate> callback){
        createServer(RateService.class).rate(id).enqueue(callback);
    }

    public static void sendBill(@NonNull final Bill bill,
                                @NonNull final Callback<Bill> callback){
        createServer(BillService.class).sendBill(new Gson().toJson(bill)).enqueue(callback);
    }

    private interface CityService{
        @GET(ALL_CITIES)
        Call<List<City>> listCity();

        @GET(CITY)
        Call<City> city(@Query("id") String id);
    }

    private interface ServiceProviderService{
        @GET(ALL_SERVICE_PROVIDERS)
        Call<List<ServiceProvider>> allServiceProvider();

        @GET(SERVICE_PROVIDER_FOR_CITY)
        Call<List<ServiceProvider>> listServiceProvider(@Query("id") String cityId);

        @GET(SERVICE_PROVIDER)
        Call<ServiceProvider> serviceProvider(@Query("id") String id);
    }

    private interface BillService{
        @POST(SEND_BILL)
        Call<Bill> sendBill(@Query("bill") String bill);
    }

    private interface RateService{
        @GET(RATE_BY_ID)
        Call<Rate> rate(@Query("id") String id);
    }
}