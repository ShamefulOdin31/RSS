package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

public class settings extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    boolean checked = false;
    boolean dark = false;
    boolean sizeChecked = false;
    boolean webChecked = false;
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("general", MODE_PRIVATE);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Switch s = findViewById(R.id.swap);
        checked = sharedPreferences.getBoolean("swap", false);
        s.setChecked(checked);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checked = b;
            }
        });

        Switch d = findViewById(R.id.dark);
        dark = sharedPreferences.getBoolean("dark", false);
        d.setChecked(dark);

        d.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dark = b;
            }
        });

        Switch size = findViewById(R.id.size);
        sizeChecked = sharedPreferences.getBoolean("size", false);
        size.setChecked(sizeChecked);

        size.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sizeChecked = b;
            }
        });

        Switch web = findViewById(R.id.web);
        webChecked = sharedPreferences.getBoolean("web", false);
        web.setChecked(webChecked);
        web.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                webChecked = b;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("swap", checked);
        editor.putBoolean("dark", dark);
        editor.putBoolean("size", sizeChecked);
        editor.putBoolean("web", webChecked);
        editor.apply();

        Intent result = new Intent();
        result.putExtra("swap", checked);
        result.putExtra("dark", dark);
        result.putExtra("size", sizeChecked);
        result.putExtra("web", webChecked);

        setResult(RESULT_OK, result);
        finish();
    }
}