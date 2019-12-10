package com.example.animes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditAnimeActivity extends AppCompatActivity {

    EditText tituloEditText;
    EditText generoEditText;
    EditText anoEditText;
    EditText episodiosEditText;
    private ImageView imageView;
    EditText urlImage;
    Anime anime;
    Bitmap bitmap;
    int position;
    int cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_anime);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        urlImage = findViewById(R.id.urlImage);
        imageView = findViewById(R.id.imageView);
        tituloEditText = findViewById(R.id.tituloEditText);
        generoEditText = findViewById(R.id.generoEditText);
        episodiosEditText = findViewById(R.id.episodiosEditText);
        anoEditText = findViewById(R.id.anoEditText);

        Bundle bundle = getIntent().getExtras();
        final int requestCode = (int) bundle.getSerializable("request_code");
        if (requestCode == 1) {
            anime = bundle.getParcelable("anime");
            position = (int) bundle.getSerializable("position");
            imageView.setImageBitmap(anime.getImagem());
            tituloEditText.setText(anime.getTitulo());
            generoEditText.setText(anime.getGenero());
            episodiosEditText.setText(String.valueOf(anime.getEpisodios()));
            anoEditText.setText(String.valueOf(anime.getAno()));
        }else
            anime = new Anime();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anime.setTitulo(tituloEditText.getText().toString());
                anime.setGenero(generoEditText.getText().toString());
                anime.setEpisodios(Integer.valueOf(episodiosEditText.getText().toString()));
                anime.setAno(Integer.valueOf(anoEditText.getText().toString()));
                sendPost();

                Intent returnIntent = new Intent();
                Bundle returnBundle = new Bundle();
                returnBundle.putParcelable("anime", anime);
                if (requestCode == 1)
                    returnBundle.putInt("position",position);
                returnIntent.putExtras(returnBundle);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

    protected void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("url", anime.getImagem().toString());
                    jsonParam.put("titulo", anime.getTitulo());
                    jsonParam.put("genero", anime.getGenero());
                    jsonParam.put("episodios", anime.getEpisodios());
                    jsonParam.put("ano", anime.getAno());

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Uri targetUri = data.getData();
                //uriTextView.setText(targetUri.toString());
                Bitmap bitmap;
                try{
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                    anime.setImagem(bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                anime.setImagem(imageBitmap);
                imageView.setImageBitmap(imageBitmap);
            }
        }
    }
    public void getFromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }
    public void getFromCamera(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }
    public void getFromUrl(View view){
        bitmap = new HttpHandler().getBitmap(urlImage.getText().toString());
        anime.setImagem(bitmap);
    }
    public void saveImage(View view) {
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }else {

            String image_name = "Image_" + cont++;

            String root = Environment.getExternalStorageDirectory().toString() + File.separator + "DCIM";
            File myDir = new File(root);
            myDir.mkdirs();
            String fname = image_name + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                MediaStore.Images.Media.insertImage(getContentResolver()
                        ,file.getAbsolutePath(),file.getName(),file.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}