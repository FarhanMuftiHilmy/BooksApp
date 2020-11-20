package com.rechit.booksapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.BitSet;

public class DetailActivity extends AppCompatActivity {
    private TextView textAuthor;
    private TextView textTitle;
    private TextView textDesc;
    private ImageView imageCover;
    private ItemData data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        textTitle = findViewById(R.id.textTitle);
        textAuthor = findViewById(R.id.textAuthor);
        textDesc = findViewById(R.id.textDesc);
        imageCover = findViewById(R.id.imageCover);

        // untuk menerima data
        Intent intent = getIntent();
        if(intent.hasExtra("DATA")){
            data = intent.getParcelableExtra("DATA");
           // masukkan masing2 text
            textTitle.setText(data.itemTitle);
            textAuthor.setText(data.itemAuthor);
            textDesc.setText(data.itemDescription);
            new LoadImage(imageCover).execute(data.itemImage);

        }
    }

    // buat class baru untuk mendownload image
    private static class LoadImage extends AsyncTask<String, Void, Bitmap>{
        private ImageView imageView;
        public LoadImage(ImageView imageView){
            this.imageView = imageView;
        }
        protected Bitmap doInBackground(String... strings){
            URL url = null;
            Bitmap bitmap = null;
            try {
                url = new URL(strings[0]);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        // tampilkan di image view
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}