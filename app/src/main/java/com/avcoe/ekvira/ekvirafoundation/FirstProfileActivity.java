package com.avcoe.ekvira.ekvirafoundation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirstProfileActivity extends AppCompatActivity {

    Button createProfile;
    EditText firstNameText, lastNameText, emailText;
    Spinner genderSpinner;
    //String Data
    public String phoneNumber,firstName,lastName,email,gender;
    boolean check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_profile);

        //get bundle data
        Bundle bundle = getIntent().getExtras();
        phoneNumber = bundle.getString("phoneNumber");
        EditText editText = findViewById(R.id.phoneText);
        editText.setText(phoneNumber, TextView.BufferType.EDITABLE);

        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);
        //phoneNumber get bu intent
        genderSpinner = findViewById(R.id.gender);
        createProfile = findViewById(R.id.createProfile);

        createProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check not empty
                //...
                checkData();
                //CALLING new activity
                Intent intent = new Intent(FirstProfileActivity.this,HomeActivity.class);;
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber",phoneNumber);
                bundle.putString("firstName",firstName);
                bundle.putString("lastName",lastName);
                bundle.putString("email",email);
                bundle.putString("gender",gender);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }
    private void checkData(){
        firstName = firstNameText.getText().toString();
        lastName = lastNameText.getText().toString();
        //phone no inherited
        gender = genderSpinner.getSelectedItem().toString().trim();
        email = emailText.getText().toString();
        check = true;
        if (firstName.isEmpty()){
            firstNameText.setError("Invalid Credential");
            animateMe(firstNameText);
            check = false;
        }
        if (lastName.isEmpty()){
            lastNameText.setError("Invalid Credential");
            animateMe(lastNameText);
            check = false;
        }
        if (gender.equals("GENDER")){
            animateMe(genderSpinner);
            check = false;
        }
        if (check) {
            User user;
            user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setGender(gender);
            user.setEmail(email);
            DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("User");
            reff.push().setValue(user);
        }
    }
    public void animateMe(View myObject){
        Animation shake = AnimationUtils.loadAnimation(FirstProfileActivity.this, R.anim.shake);
        myObject.startAnimation(shake);
    }
}
