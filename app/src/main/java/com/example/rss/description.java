package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class description extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TextView title = findViewById(R.id.title);
        TextView desc = findViewById(R.id.desc);
        TextView pubDate = findViewById(R.id.pubDate);
        Button btn = findViewById(R.id.browser);

        sharedPreferences = getSharedPreferences("general", MODE_PRIVATE);
        ConstraintLayout layout = findViewById(R.id.layout);
        if(sharedPreferences.getBoolean("dark", false)){
            layout.setBackgroundColor(Color.GRAY);
        }
        if(sharedPreferences.getBoolean("size", false)){
            desc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        } else {
            desc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        }

        if(sharedPreferences.getBoolean("web", false)){
            btn.setEnabled(false);
        } else {
            btn.setEnabled(true);
        }



        Intent intent = getIntent();

        title.setText(intent.getStringExtra("title"));
        desc.setText(intent.getStringExtra("desc"));
        pubDate.setText(intent.getStringExtra("pubDate"));

        final String link = intent.getStringExtra("link");

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

}