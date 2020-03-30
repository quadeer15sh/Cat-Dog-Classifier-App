package com.example.apiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    TextView textView;

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data=reader.read();
                }

                return result;

            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String result = jsonObject.getString("result");
                Log.i("Result",result);
                String category = "";
                if (result.equals("1")){
                    category = "It is a Dog";
                }
                else {
                    category = "It is a Cat";
                }

                textView.setText("Prediction: "+category);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void downloadImage(View view){
        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        editText = findViewById(R.id.editText);
        String link = editText.getText().toString();
        textView = findViewById(R.id.textView);
        textView.setText(link);

        try {
            myImage = task.execute(link).get();

            imageView.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findAnimal(View view){
        DownloadTask task = new DownloadTask();
        String link = editText.getText().toString();
        task.execute("http://192.168.0.10/home/"+link);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView2);
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(in);

                return myBitmap;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }
}
