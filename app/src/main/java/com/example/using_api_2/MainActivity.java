package com.example.using_api_2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.text.method.ScrollingMovementMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String sunSign = "Aries";
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonView = findViewById(R.id.button);
        buttonView.setOnClickListener(view -> {
            CompletableFuture<String> futureResult = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                futureResult = CompletableFuture.supplyAsync(() -> {
                    try {
                        return callAztroAPI("https://sameer-kumar-aztro-v1.p.rapidapi.com/?sign=" + sunSign + "&day=today");
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                futureResult.thenAcceptAsync(this::onResponse);
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sunsigns, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        resultView = findViewById(R.id.resultView);
        resultView.setMovementMethod(new ScrollingMovementMethod());
    }

    private String callAztroAPI(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // set headers for the request
        // set host name
        connection.setRequestProperty("x-rapidapi-host", "sameer-kumar-aztro-v1.p.rapidapi.com");

        // set the rapid-api key
        connection.setRequestProperty("x-rapidapi-key", "18c935d513msh8cff4fe15c38f82p17f7aejsnef2a8a9efb44");
        connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        // set the request method - POST
        connection.setRequestMethod("POST");
        InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

        // read the response data
        int data = inputStreamReader.read();
        StringBuilder result = new StringBuilder();
        while (data != -1) {
            char current = (char) data;
            result.append(current);
            data = inputStreamReader.read();
        }
        return result.toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        sunSign = "Aries";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent != null) {
            sunSign = parent.getItemAtPosition(position).toString();
        }
    }

    private void onResponse(String result) {
        try {
            // convert the string to JSON object for better reading
            JSONObject resultJson = new JSONObject(result);

            // Initialize prediction text
            String prediction = "Today's prediction \n";
            prediction += this.sunSign + "\n";

            // Update text with various fields from response
            prediction += resultJson.getString("date_range") + "\n\n";
            prediction += resultJson.getString("description");

            // Update the prediction to the view
            setText(this.resultView, prediction);

        } catch (JSONException e) {
            e.printStackTrace();
            this.resultView.setText("Oops!! something went wrong, please try again");
        }
    }

    private void setText(final TextView text, final String value) {
        runOnUiThread(() -> text.setText(value));
    }
}


