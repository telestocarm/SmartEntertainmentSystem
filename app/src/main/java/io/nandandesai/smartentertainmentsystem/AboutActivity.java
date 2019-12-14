package io.nandandesai.smartentertainmentsystem;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView buildVersionText=findViewById(R.id.buildVersion);
        buildVersionText.setText("Build Version: "+BuildConfig.VERSION_NAME);
    }
}
