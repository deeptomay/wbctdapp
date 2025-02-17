package com.example.testapp.splash;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.testapp.R;
import com.example.testapp.Login2Activity;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // UI Elements
        ImageView logo = findViewById(R.id.top_image);
        TextView header = findViewById(R.id.header_text);
        TextView subHeader = findViewById(R.id.sub_header_text);
        TextView govText = findViewById(R.id.government_text);
        TextView footer = findViewById(R.id.textView2);

        // Fade-in Animation
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000);

        // Start Animations
        logo.startAnimation(fadeIn);
        header.startAnimation(fadeIn);
        subHeader.startAnimation(fadeIn);
        govText.startAnimation(fadeIn);
        footer.startAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, Login2Activity.class);
                startActivity(intent);
                finish(); // Optional: close CurrentActivity
            }
        }, 3000);

    }
}