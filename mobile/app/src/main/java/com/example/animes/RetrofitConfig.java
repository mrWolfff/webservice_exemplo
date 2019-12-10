package com.example.animes;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitConfig {
    private final Retrofit retrofit;

    public RetrofitConfig() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("http://10.153.15.86:8000/api/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
    public AnimeService getAnimeService() {
        return this.retrofit.create(AnimeService.class);
    }
}
