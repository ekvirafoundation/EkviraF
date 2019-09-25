package com.avcoe.ekvira.ekvirafoundation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText mobileTxt,otpTxt;
    Button generateBtn,loginBtn;
    String phoneNumber;

    //var for timer
    public int sec = 30;
    public int min = 1;

    //Firebase Auth
    FirebaseAuth mAuth;
    String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        generateBtn = (Button) findViewById(R.id.generateBtn);
        mobileTxt = (EditText) findViewById(R.id.mobileTxt);
        otpTxt = (EditText) findViewById(R.id.otpText);

        //initialize mAuth
        mAuth = FirebaseAuth.getInstance();

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = "+91"+mobileTxt.getText().toString();
                if (phoneNumber.length() == 13){
                    sendVerificationCode();
                    changeUI();
                    setTimer();
                }
                else {
                    mobileTxt.setError("Invalid Credential");
                    mobileTxt.requestFocus();
                    animateMe(mobileTxt);
                }
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otpTxt.getText().toString();
                if (code.length()==6) {
                    verifySignInCode();
                }
                else {
                    animateMe(otpTxt);
                }
            }
        });
    }

    private void sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacksPhoneAuthActivity.java
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            //Log.d(TAG, "onVerificationCompleted:" + credential);
            Toast.makeText(getApplicationContext(),"Instant Retrival by Google",Toast.LENGTH_SHORT).show();
            signInWithPhoneAuthCredential(credential);
            openForNewUser();
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            //Log.w(TAG, "onVerificationFailed", e);
            //Toast.makeText(getApplicationContext(),"Verification Faild",Toast.LENGTH_SHORT).show();

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                //Toast.makeText(getApplicationContext(),"Faild: Invalid Req",Toast.LENGTH_SHORT).show();
                mobileTxt.setError("Mobile: Invalid Credential");
                mobileTxt.requestFocus();
                animateMe(mobileTxt);
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Toast.makeText(getApplicationContext(),"The SMS quota for the project has been exceeded",Toast.LENGTH_SHORT).show();
            }

            // Show a message and update the UI
            // ...
        }
        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            //Log.d(TAG, "onCodeSent:" + verificationId);
            Toast.makeText(getApplicationContext(),"Code Sent"+verificationId,Toast.LENGTH_SHORT).show();

            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            //mResendToken = token;

            // ...
        }
    };

    private void changeUI(){
        //graphics enable
        otpTxt.setVisibility(View.VISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
        //otp button disable
        generateBtn.setEnabled(false);
        generateBtn.setBackgroundColor(Color.parseColor("#999999"));
    }

    private void setTimer(){
        Timer buttonTimer = new Timer();
        buttonTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        generateBtn.setText("WAIT "+String.valueOf(min)+":"+String.valueOf(sec));
                        sec -= 1;
                        if (min == 0 & sec == 0){
                            generateBtn.setText("REGENERATE OTP");
                            generateBtn.setEnabled(true);
                            generateBtn.setBackgroundColor(Color.parseColor("#0a4d60"));
                            min = 1;
                            sec = 30;
                            cancel();
                        }
                        if (sec == 0){
                            generateBtn.setText("WAIT "+String.valueOf(min)+":"+String.valueOf(sec));
                            sec = 60;
                            min = min - 1;
                        }
                    }
                });
            }
        },0,1000);
    }

    private void verifySignInCode(){
        String code = otpTxt.getText().toString();
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Exception: verifySignInCode()"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
                            //FirebaseUser user = task.getResult().getUser();
                            openForNewUser();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),"WRONG CODE | "+mVerificationId,Toast.LENGTH_SHORT).show();
                                animateMe(otpTxt);
                            }
                        }
                    }
                });
    }

    public void animateMe(View myObject){
        Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
        myObject.startAnimation(shake);
    }

    private void openForNewUser(){
        Intent intent = new Intent(LoginActivity.this,FirstProfileActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber",phoneNumber);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
