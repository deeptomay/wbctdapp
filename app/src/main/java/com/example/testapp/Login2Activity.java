package com.example.testapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.testapp.apiservice.LoginAsyncTask;
import com.example.testapp.apiservice.OtpAsyncTask;

public class Login2Activity extends AppCompatActivity {

    private Button btnLogin, btnResendOTP, verify_otp;
    private LinearLayout otpSection;
    private EditText userid, password, otp1, otp2, otp3, otp4;

    String userid_text = "", password_text = "", usrnm = "",otp1_text="",otp2_text="",otp3_text="",otp4_text="",otp_text="";


    private ImageButton togglePasswordButton;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        userid = findViewById(R.id.et_userid);
        password = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        verify_otp=findViewById(R.id.verify_otp);
        btnResendOTP = findViewById(R.id.btn_resend_otp);
        otpSection = findViewById(R.id.otp_section);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        btnLogin.setOnClickListener(view -> {
            userid_text=userid.getText().toString();
            password_text = password.getText().toString();
            System.out.println("UserId:::: " + userid_text);
            if (userid_text.isBlank() || password_text.isBlank()) {

                Toast.makeText(this, "Userid or Password cannot be left blank!!!", Toast.LENGTH_SHORT).show();

            }
            else {


                new LoginAsyncTask(this).execute(userid_text, password_text);
            }


        });
        verify_otp.setOnClickListener(view -> {
            otp1_text = otp1.getText().toString();
            otp2_text = otp2.getText().toString();
            otp3_text = otp3.getText().toString();
            otp4_text = otp4.getText().toString();
            otp_text = otp1_text+otp2_text+otp3_text+otp4_text;

            new OtpAsyncTask(this).execute(userid_text, otp_text);
        });


        password.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableRightIndex = 2; // Index for drawableEnd
                Drawable drawableRight = password.getCompoundDrawables()[drawableRightIndex];

                if (drawableRight != null) {
                    int iconWidth = drawableRight.getBounds().width();
                    int touchPadding = 10; // Reduce the touchable area (default ~40px)

                    int touchAreaStart = password.getRight() - iconWidth - touchPadding;
                    int touchAreaEnd = password.getRight() + touchPadding;

                    if (event.getRawX() >= touchAreaStart && event.getRawX() <= touchAreaEnd) {
                        togglePasswordVisibility();
                        return true;
                    }
                }
            }
            return false;
        });


    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableIcon(R.drawable.ic_visibility_off), null);
        } else {
            // Show password
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            password.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawableIcon(R.drawable.ic_visibility), null);
        }
        password.setSelection(password.getText().length()); // Keep cursor at the end
        isPasswordVisible = !isPasswordVisible;
    }

    private Drawable getDrawableIcon(int drawableId) {
        return ContextCompat.getDrawable(this, drawableId);
    }

    public void setResulttoUI(String login_status, String user_nm) {

        if (login_status.equals("Y")) {
            hideKeyboard();
            otpSection.setVisibility(View.VISIBLE);
            startResendTimer();
            setupOTPAutoMove();

            btnResendOTP.setOnClickListener(v -> {
                Toast.makeText(Login2Activity.this, "OTP Resent!", Toast.LENGTH_SHORT).show();
                btnResendOTP.setEnabled(false);
                startResendTimer();
                // Call API to resend OTP here
            });
            usrnm = user_nm;
        }


    }

    public void setResulttoUIforOTP(String otp_status) {

        if (otp_status.equals("Y")) {
            Intent i = new Intent(Login2Activity.this, MainActivity.class);
            i.putExtra("userid", userid_text);
            i.putExtra("user_nm", usrnm);
            startActivity(i);
            System.out.println("Userid:: " + userid_text + "  Password:: " + password_text);
        } else {
            Toast.makeText(Login2Activity.this, "OTP doesn't match!", Toast.LENGTH_SHORT).show();
        }


    }

    private void startResendTimer() {
        btnResendOTP.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                btnResendOTP.setText("Resend OTP (" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                btnResendOTP.setText("Resend OTP");
                btnResendOTP.setEnabled(true);
            }
        }.start();
    }

    private void setupOTPAutoMove() {
        EditText[] otpFields = {otp1, otp2, otp3, otp4};
        for (int i = 0; i < otpFields.length; i++) {
            int finalI = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && finalI < otpFields.length - 1) {
                        otpFields[finalI + 1].requestFocus();
                    }
                    //System.out.println("i>>>>>> "+finalI);
                    if(finalI==3){
                        hideKeyboard();

                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
            });

        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}