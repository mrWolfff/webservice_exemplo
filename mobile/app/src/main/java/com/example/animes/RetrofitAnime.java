package com.example.animes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitAnime extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.retrofit_anime);

            final EditText anime = findViewById(R.id.get_anime);
            final TextView resposta = findViewById(R.id.response);
            Button btn_busca = findViewById(R.id.button_busca);
            btn_busca.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    Call<Anime> call = new RetrofitConfig().getAnimeService().getAnime(anime.getText().toString());

                    call.enqueue(new Callback<Anime>() {
                        @Override
                        public void onResponse(Call<Anime> call, Response<Anime> response) {
                            Anime anime = response.body();
                            resposta.setText(anime.toString());
                        }

                        @Override
                        public void onFailure(Call<Anime> call, Throwable t) {
                            Log.e("Animes   ", "Erro ao buscar o anime:" + t.getMessage());
                        }
                    });
                }
            });
        }
}
