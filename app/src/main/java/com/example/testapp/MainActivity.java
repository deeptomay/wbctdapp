package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    //userid is hard coded. need to be dynamic later with login
    String userid = "",user_nm="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        userid=getIntent().getStringExtra("userid");
        user_nm=getIntent().getStringExtra("user_nm");
        Button gstn_search_btn;
        Button btn_upload;
        TextView userid_view = (TextView)findViewById(R.id.userid_text);
        userid_view.setText(user_nm);


        gstn_search_btn = (Button)findViewById(R.id.btn_search_gstin);

        gstn_search_btn.setOnClickListener(view -> {

            Intent i =new Intent(MainActivity.this,GSTINSearchActivity.class);
            i.putExtra("userid",userid);
            i.putExtra("user_nm",user_nm);


            startActivity(i);

        });
    btn_upload =  (Button)findViewById(R.id.btn_show_dealer_details);

    btn_upload.setOnClickListener(view -> {

        Toast.makeText(this,"under construction!",Toast.LENGTH_SHORT).show();
    });

    }
}