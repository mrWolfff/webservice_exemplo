package com.example.animes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Actions {
    private List<Anime> listaAnimes;
    private AnimeAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private static final int REQUEST_EDIT = 1;
    private static final int REQUEST_INSERT = 2;
    private String TAG = MainActivity.class.getSimpleName();
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new GetFilmesJson().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this,adapter.getListaAnimes().get(0).getTitulo()+" "+adapter.getListaAnimes().get(1).getTitulo()+" "+adapter.getListaAnimes().get(2).getTitulo(),Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void setRecyclerView(){
        adapter = new AnimeAdapter(listaAnimes, this);
        recyclerView = (RecyclerView) findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new TouchHelp(adapter));
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void setFloatActionButton(){
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inserirAnime();
            }
        });
    }

    private void inserirAnime(){
        Intent intent = new Intent(this, EditAnimeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("request_code", REQUEST_INSERT);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_INSERT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Anime f = (Anime) bundle.getParcelable("anime");
                int position = bundle.getInt("position");
                adapter.update(f, position);
            }
        }
        if (requestCode == REQUEST_INSERT) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                Anime f = (Anime) bundle.getParcelable("anime");
                adapter.inserir(f);
            }
        }
    }

    @Override
    public void undo() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.constraintLayout),"Item removido.",Snackbar.LENGTH_LONG);
        snackbar.setAction("Desfazer", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restaurar();
            }
        });
        snackbar.show();
    }

    @Override
    public void toast(Anime anime) {
        Toast.makeText(this,anime.getTitulo()+" "+anime.getGenero()+" "+anime.getEpisodios()+" "+anime.getAno(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void edit(int position) {
        Intent intent = new Intent(this, EditAnimeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("request_code", REQUEST_EDIT);
        bundle.putParcelable("anime", adapter.getListaAnimes().get(position));
        bundle.putSerializable("position", position);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    private class GetFilmesJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            listaAnimes = new ArrayList<Anime>();
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall("http://10.153.15.86:8000/api/animes/?format=json");//ip localhost nao funciona aqui
            String url = "";
            if (jsonStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(jsonStr);
                    //JSONArray jsonArray = object.getJSONArray("animes");
                    //Toast.makeText(getApplicationContext(), " >>"+ object  ,Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), " >>"+ jsonArray  ,Toast.LENGTH_LONG).show();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Anime f = new Anime();

                        url = jsonArray.getJSONObject(i).getString("url");
                        Log.e(TAG, url);
                        f.setImagem(new HttpHandler().getBitmap(url));
                        f.setTitulo(jsonArray.getJSONObject(i).getString("titulo"));
                        f.setGenero(jsonArray.getJSONObject(i).getString("genero"));
                        f.setEpisodios(jsonArray.getJSONObject(i).getInt("episodios"));
                        f.setAno(jsonArray.getJSONObject(i).getInt("ano"));
                        listaAnimes.add(f);
                    }
                }  catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            setRecyclerView();
            setFloatActionButton();
        }
    }


}
