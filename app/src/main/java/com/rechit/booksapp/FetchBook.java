package com.rechit.booksapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

//kelas untuk mengakses data dari api, diparsing lalu dihubungkan ke adapter dan ditampilkan di recycler view
public class FetchBook extends AsyncTask<String, Void, String> {
//    tambah parameter dan constructor dulu
    private ArrayList<ItemData> values;
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;
    Context context;

    public FetchBook(Context context, ArrayList<ItemData> values,ItemAdapter itemAdapter, RecyclerView recyclerView){
        this.values = values;
        this.itemAdapter = itemAdapter;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @Override
    //untuk mengakses data dari internet yang berproses di background
    protected String doInBackground(String... strings) {
        String queryString = strings[0]; // ini adalah array of string maka harus diambil yg pertama
        HttpURLConnection urlConnection = null; // untuk membangun koneksinya
        BufferedReader reader=null;
        String bookJSONString = null;
        String BOOK_BASE_URL="https://www.googleapis.com/books/v1/volumes?";
        String QUERY_PARAM = "q";
        Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon().appendQueryParameter(QUERY_PARAM, queryString).build();
        // tambahkan untuk akses APInya
        try {
            URL requestURL = new URL(builtURI.toString()); // ubah uri jadi url
            urlConnection = (HttpURLConnection)requestURL.openConnection(); // buka koneksi
            urlConnection.setRequestMethod("GET"); // tergantung api nya bisa post atau get
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder(); // untuk membaca data
            reader = new BufferedReader(new InputStreamReader(inputStream));// untuk membaca input string
            String line;
            while((line=reader.readLine()) != null){
                builder.append(line+"\n");
            }
            if(builder.length() == 0){
                return null;
            }
            bookJSONString=builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookJSONString;
    }

    // setelah itu masuk ke proses parsing di ui thread
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        values=new ArrayList<>();
        //memasukkan object - array - object - data

        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemArray = jsonObject.getJSONArray("items");
            String title=null;
            String author=null;
            String image=null;
            String desc=null;
            int i = 0;
            while(i<itemArray.length()){
                JSONObject book = itemArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                try{
                    title=volumeInfo.getString("title");
                    if(volumeInfo.has("authors")){
                        author=volumeInfo.getString("authors");
                    } else{
                        author="";
                    }
                    if(volumeInfo.has("description")){
                        desc=volumeInfo.getString("description");
                    } else{
                        desc="";
                    }
                    if(volumeInfo.has("imageLinks")){
                        image = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
                    } else{
                        image="";
                    }

                    // jika parameter sudah di dapat masukkan ke objek
                    ItemData itemData = new ItemData();
                    itemData.itemTitle = title;
                    itemData.itemDescription = desc;
                    itemData.itemAuthor = author;
                    itemData.itemImage = image;
                    values.add(itemData);

                } catch (Exception e){
                    e.printStackTrace();
                }
                i++; // jangan lupa ini haha

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // untuk memasukkan data ke adapter dan memasukkan ke recycler view
        this.itemAdapter = new ItemAdapter(context, values);
        this.recyclerView.setAdapter(this.itemAdapter);

    }
}
