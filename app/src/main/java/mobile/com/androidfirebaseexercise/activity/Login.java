package mobile.com.androidfirebaseexercise.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;

import mobile.com.androidfirebaseexercise.R;
import mobile.com.androidfirebaseexercise.global.FirebaseTables;
import mobile.com.androidfirebaseexercise.global.Global;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button btnRegister;
    private Button btnLogin;

    private DatabaseReference mDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;

    public FirebaseAuth mAuth;

    private LinearLayout LinearLayoutEmail;
    private LinearLayout LinearLayoutPassword;

    private EditText EditTextEmail;
    private EditText EditTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            Login.this.finish();
        }

        EditTextEmail=(EditText) findViewById(R.id.EditTextEmail);
        EditTextPassword=(EditText) findViewById(R.id.EditTextPassword);

        LinearLayoutEmail=(LinearLayout) findViewById(R.id.LinearLayoutEmail);
        LinearLayoutPassword=(LinearLayout) findViewById(R.id.LinearLayoutPassword);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener( (View.OnClickListener) this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener( (View.OnClickListener) this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseInstance.getReference(FirebaseTables.TABLE_USER);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegister) {
            doRegister();
        }

        if (v.getId() == R.id.btnLogin) {
            doLogin();
        }
    }

    private void doRegister(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
        Login.this.finish();
    }

    private void doLogin(){
        if (TextUtils.isEmpty(EditTextEmail.getText().toString())){
            Toast.makeText(Login.this, "Please fill your email.", Toast.LENGTH_SHORT).show();
            EditTextEmail.requestFocus();
            EditTextEmail.performClick();
            return;
        }

        if (TextUtils.isEmpty(EditTextPassword.getText().toString())){
            Toast.makeText(Login.this, "Please fill your password.", Toast.LENGTH_SHORT).show();
            EditTextPassword.requestFocus();
            EditTextPassword.performClick();
            return;
        }

        //isValid
        Global.showDialog(Login.this);
        processLogin();
    }

    private void processLogin(){
        mAuth.signInWithEmailAndPassword(EditTextEmail.getText().toString(), EditTextPassword.getText().toString())
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Global.dismissDialog();
                            Toast.makeText(Login.this, "Your email or password is not valid !", Toast.LENGTH_SHORT).show();
                            EditTextPassword.setText("");
                            EditTextPassword.requestFocus();
                            EditTextPassword.performClick();

                        } else {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            HashMap<String, Object> value = new HashMap<>();
                            value.put("androidID", Global.getAndroidID(Login.this));
                            mDatabase.child(firebaseUser.getUid()).updateChildren(value);

                            SharedPreferences mPreferences = Login.this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
                            Boolean firstTime  = mPreferences.getBoolean("firstTime", true);
                            if (firstTime) {
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putBoolean("firstTime", false);
                                editor.commit();
                            }

                            Global.dismissDialog();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            Login.this.finish();
                        }
                    }
                });
    }
}
