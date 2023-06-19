package com.example.getweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private EditText userField;
    private Button mainBtn;
    private TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.editText);
        mainBtn = findViewById(R.id.mainButton);
        resultInfo = findViewById(R.id.resultInfo);

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = userField.getText().toString();
                String key = "301ba812f9847f182319f09b27d2823e";
                String url_s = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&lang=ru&units=metric";
                if (userField.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.ToastForUser, Toast.LENGTH_SHORT).show();
                else{
                    resultInfo.setText("Загрузка...");
                    new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String result = getResult(url_s);
                            resultInfo.post(new Runnable() { // ОБЯЗАТЕЛЬНО!! возвращаемся в UI поток, иначе ошибка
                                public void run() {
                                    try {
                                        JSONObject jobj = new JSONObject(result);
                                        resultInfo.setText("Температура: " + jobj.getJSONObject("main").getDouble("temp") + "\n"
                                        + "Ощущается: " +jobj.getJSONObject("main").getDouble("feels_like")+ "\n\n");
//                                        + "Осадки: " );
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }catch ( IOException ex){
                            resultInfo.setText("Ошибка: " + ex.getMessage());
                        }
                    }
                }).start();
                }
            }
        });
    }

    private String getResult(String url_s) throws IOException{
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        InputStream istream = null;
        try {
            URL url = new URL(url_s); // открыли URL соединение
            connection = (HttpURLConnection)url.openConnection(); // открыли http соединение
            connection.connect();
            istream = connection.getInputStream(); //считали весь поток, который получили
            reader = new BufferedReader(new InputStreamReader(istream)); // считываем поток как сторку
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            return buffer.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException( e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            if (reader != null) {
                reader.close();
            }
            if (istream != null) {
                istream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
//            if (connection != null)
//                connection.disconnect();
//            try {
//                if (reader != null)
//                    reader.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

    }
}


