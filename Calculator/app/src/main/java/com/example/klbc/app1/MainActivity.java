package com.example.klbc.app1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.urledtxt);
    }

    public void goToCalculator(View v){
        //create intent binds between main activity and calculator activity
        Intent intent = new Intent(this, Calculator.class);

        //you can get info from current activity and pass it to the target activity
        //by intent.putExtra(key, value);
        startActivity(intent);
    }

    public void browseUrl(View v){

//        String url = editText.getText().toString();
        String url = "http://www.google.com";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Chrome is probably not installed
            // Try with the default browser
            Log.d("error","exception");
            intent.setPackage(null);
            startActivity(intent);
        }
    }



}
