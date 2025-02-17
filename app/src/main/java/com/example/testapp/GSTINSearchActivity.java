package com.example.testapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.testapp.apiservice.ApiCallAsyncTask;

public class GSTINSearchActivity extends AppCompatActivity {



    String userid="",user_nm="";
//    String gstin,trade_nm,legal_nm,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gstinsearch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton search_btn;
        EditText gstin;

        search_btn = (ImageButton) findViewById(R.id.imageButton);
        gstin = (EditText)findViewById(R.id.editTextText2);

        userid = getIntent().getStringExtra("userid");
        user_nm = getIntent().getStringExtra("user_nm");
        TextView userid_view = (TextView)findViewById(R.id.userid_text);
        userid_view.setText(user_nm);

        gstin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                gstin.removeTextChangedListener(this);
                String upperCaseText = s.toString().toUpperCase();
                gstin.setText(upperCaseText);
                gstin.setSelection(upperCaseText.length()); // Move cursor to the end
                gstin.addTextChangedListener(this);
            }
        });

        gstin.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        search_btn.setOnClickListener(view -> {

            String gstin_no = gstin.getText().toString();
            if(gstin_no.length()==15) {
                new ApiCallAsyncTask(this).execute(gstin_no);
            }else{
                Toast.makeText(this,"GSTIN must be 15 digit long!!", Toast.LENGTH_SHORT).show();
            }

            //Toast.makeText(this,"App is under construction !",Toast.LENGTH_SHORT).show();

        });
    }

    public void setResulttoUI(String gstn,String tradenm,String legalnm,String addrss){

        //addrss = "Directorate of Commercial Taxes, 14 Beliaghata Main Rd, Kolkata-700015";
//       this.gstin = gstn;
//       this.trade_nm = tradenm;
//       this.legal_nm=legalnm;
//       this.address=addrss;

        Intent i = new Intent(GSTINSearchActivity.this,DealerDetailActivity.class);
        i.putExtra("gstin_no",gstn);
        i.putExtra("userid",userid);
        i.putExtra("trade_nm",tradenm);
        i.putExtra("legal_nm",legalnm);
        i.putExtra("address",addrss);
        i.putExtra("user_nm",user_nm);
        startActivity(i);



    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    hideKeyboard(view);
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}