package com.example.animes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AnimeService {

    @GET("animes/?format=json")
    Call<Anime> getAnime(@Path("anime") String anime);

}
