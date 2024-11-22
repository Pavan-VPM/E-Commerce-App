package com.example.ecommerceapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    EditText name,email,password;
    private FirebaseAuth auth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        auth= FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
            finish();


        }

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);

        sharedPreferences=getSharedPreferences("onBoardingScreen",MODE_PRIVATE);

        boolean isFirstTime= sharedPreferences.getBoolean("firstTime",true);

        if (isFirstTime){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putBoolean("firstTime",false);
            editor.commit();

            Intent intent=new Intent(RegistrationActivity.this,OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void signup(View view)
    {
        String userName=name.getText().toString();
        String userEmail=email.getText().toString();
        String userPassword=password.getText().toString();
        if(TextUtils.isEmpty(userName))
        {
            Toast.makeText(this,"ENTER NAME !",Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userEmail))
        {
            Toast.makeText(this,"ENTER EMAIL !",Toast.LENGTH_SHORT).show();
            return;

        }

        if(TextUtils.isEmpty(userPassword))
        {
            Toast.makeText(this,"ENTER PASSWORD !",Toast.LENGTH_SHORT).show();
            return;
        }
         if(userPassword.length()<6)
         {
             Toast.makeText(this," PASSWORD TOO SHORT !",Toast.LENGTH_SHORT).show();
             return;
         }

        auth.createUserWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this,"SUCCESSFULLY REGISTERED",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this,OnBoardingActivity.class));
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this,"REGISTRATION FAILED"+task.getException(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //startActivity(new Intent(RegistrationActivity.this,MainActivity.class));

    }

    public void signin(View view)
    {







       startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));

    }
}
