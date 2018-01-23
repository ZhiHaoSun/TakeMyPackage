package org.team7.TakeMyPackage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.team7.sports.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private TextInputLayout Username;
    private TextInputLayout Password;
    private TextInputLayout Phone;
    private TextInputLayout Address;
    private TextInputLayout NRICNumber;
    private Button RegisterBtn;
    private ProgressDialog RegisterProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        Username = findViewById(R.id.reg_username);
        Password = findViewById(R.id.reg_password);
        Phone = findViewById(R.id.reg_phone);
        Address = findViewById(R.id.reg_address);
        NRICNumber = findViewById(R.id.reg_nric);
        RegisterBtn = findViewById(R.id.reg_register_btn);

        RegisterProgress = new ProgressDialog(this);

        RegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String username = Username.getEditText().getText().toString();
                String email = username + "@gmail.com";
                String password = Password.getEditText().getText().toString();
                String homeAddress = Address.getEditText().getText().toString();
                String phone = Phone.getEditText().getText().toString();
                String nricNumber = NRICNumber.getEditText().getText().toString();

                if (TextUtils.isEmpty(username)) Username.setError("Username cannot be empty");
                else if (TextUtils.isEmpty(password)) Password.setError("Password cannot be empty");
                else {
                    RegisterProgress.setTitle(R.string.registering);
                    RegisterProgress.setCanceledOnTouchOutside(false);
                    RegisterProgress.show();
                    registerNewUser(email, username, password, homeAddress, phone, nricNumber);
                }

            }
        });
    }

    private void registerNewUser(String email, final String username, String password,
                                 final String homeAddress, final String phone, final String nricNumber){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUse = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUse.getUid();

                    databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, String> userMap = new HashMap();
                    userMap.put("name", username);
                    userMap.put("image", "default");
                    userMap.put("address", homeAddress);
                    userMap.put("phone", phone);
                    userMap.put("nric", nricNumber);
                    databaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    RegisterProgress.dismiss();
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                } else {
                                    Toast.makeText(RegisterActivity.this,R.string.register_failed, Toast.LENGTH_LONG).show();
                                    RegisterProgress.dismiss();
                                }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    RegisterProgress.dismiss();
                }
            }
        });
    }
}
