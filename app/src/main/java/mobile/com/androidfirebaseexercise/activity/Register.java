package mobile.com.androidfirebaseexercise.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mobile.com.androidfirebaseexercise.R;
import mobile.com.androidfirebaseexercise.global.Config;
import mobile.com.androidfirebaseexercise.global.FirebaseTables;
import mobile.com.androidfirebaseexercise.global.Global;
import mobile.com.androidfirebaseexercise.model.Cities;
import mobile.com.androidfirebaseexercise.model.Users;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private List<Cities> citiesList;

    private EditText EditTextName;
    private EditText EditTextEmail;
    private EditText EditTextPassword;
    private EditText EditTextPhoneNumber;
    private EditText EditTextUserKeyID;
    private Spinner SpinnerCities;

    private LinearLayout LinearLayoutName;
    private LinearLayout LinearLayoutEmail;
    private LinearLayout LinearLayoutPhoneNumber;
    private LinearLayout LinearLayoutCities;

    private Button btnSubmit;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public FirebaseAuth firebaseAuth;

    private Boolean firstTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        LinearLayoutName=(LinearLayout) findViewById(R.id.LinearLayoutName);
        LinearLayoutEmail=(LinearLayout) findViewById(R.id.LinearLayoutEmail);
        LinearLayoutPhoneNumber=(LinearLayout) findViewById(R.id.LinearLayoutPhoneNumber);
        LinearLayoutCities=(LinearLayout) findViewById(R.id.LinearLayoutCities);

        EditTextName=(EditText) findViewById(R.id.EditTextName);
        EditTextEmail=(EditText) findViewById(R.id.EditTextEmail);
        EditTextPassword=(EditText) findViewById(R.id.EditTextPassword);
        EditTextPhoneNumber=(EditText) findViewById(R.id.EditTextPhoneNumber);
        EditTextUserKeyID=(EditText) findViewById(R.id.EditTextUserKeyID);
        SpinnerCities=(Spinner) findViewById(R.id.SpinnerCities);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener( (View.OnClickListener) this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        Global.showDialog(Register.this);
        getAllCities();
    }

    public void getAllCities(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dataReference = firebaseDatabase.getReference().child(FirebaseTables.TABLE_CITIES);
        final boolean[] gotResult = new boolean[1];
        gotResult[0] = false;
        final Query queryData = dataReference.orderByChild("active").equalTo(true);
        final ValueEventListener dataFetchEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user information
                if (dataSnapshot.exists()) {
                    citiesList = new ArrayList<Cities>();
                    gotResult[0] = true;
                    for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                        Cities cities = messageSnapshot.getValue(Cities.class);
                        if (cities != null){
                            citiesList.add(cities);
                        }
                    }

                    if (citiesList.size() > 0){
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(Config.DEFAULT_VALUE_SPINNER);
                        for (int i = 0; i < citiesList.size(); i++) {
                            String val = citiesList.get(i).getText();
                            list.add(val);
                        }
                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<String>(getApplicationContext(),  R.layout.spinner_item, list);
                        adapter.setDropDownViewResource( R.layout.spinner_item);

                        adapter.notifyDataSetChanged();
                        SpinnerCities.setAdapter(adapter);
                    }else{
                        Toast.makeText(Register.this,"Failed to get City list from firebase database",Toast.LENGTH_LONG).show();
                        backAction();
                    }

                    Global.dismissDialog();


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                gotResult[0] = false;
                Global.dismissDialog();
                Toast.makeText(Register.this,"Failed to get City list from firebase database",Toast.LENGTH_LONG).show();
                Register.this.finish();
            }
        };

        queryData.addListenerForSingleValueEvent(dataFetchEventListener);
        final Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (gotResult[0] == false) { //  Timeout
                    dataReference.removeEventListener(dataFetchEventListener);
                    Register.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Global.dismissDialog();
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Register.this);
                            alertBuilder.setCancelable(false);
                            alertBuilder.setTitle("Failed to communicate !");
                            alertBuilder.setMessage("Application failed to communicate with firebase database !");
                            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int which) {
                                    Global.dismissDialog();
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        }
                    });

                    // Your timeout code goes here
                }
            }
        };
        // Setting timeout of 10 sec to the request
        timer.schedule(timerTask, 10000L);


    }

    @Override
    public void onBackPressed() {
        backAction();
    }

    private void backAction(){
        Intent Intent = new Intent(Register.this, Login.class);
        Register.this.startActivity(Intent);
        Register.this.finish();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmit) {
            doRegister();
        }
    }

    private void doRegister(){
        if (TextUtils.isEmpty(EditTextName.getText().toString())){
            Toast.makeText(Register.this, "Please fill your name.", Toast.LENGTH_SHORT).show();
            EditTextName.requestFocus();
            EditTextName.performClick();
            return;
        }

        if (TextUtils.isEmpty(EditTextEmail.getText().toString())){
            Toast.makeText(Register.this, "Please fill your email.", Toast.LENGTH_SHORT).show();
            EditTextEmail.requestFocus();
            EditTextEmail.performClick();
            return;
        }

        if (TextUtils.isEmpty(EditTextPassword.getText().toString())){
            Toast.makeText(Register.this, "Please fill your password.", Toast.LENGTH_SHORT).show();
            EditTextPassword.requestFocus();
            EditTextPassword.performClick();
            return;
        }

        if (TextUtils.isEmpty(EditTextPhoneNumber.getText().toString())){
            Toast.makeText(Register.this, "Please fill your phone number.", Toast.LENGTH_SHORT).show();
            EditTextPhoneNumber.requestFocus();
            EditTextPhoneNumber.performClick();
            return;
        }

        if (TextUtils.isEmpty(EditTextUserKeyID.getText().toString())){
            Toast.makeText(Register.this, "Please fill your ID number.", Toast.LENGTH_SHORT).show();
            EditTextUserKeyID.requestFocus();
            EditTextUserKeyID.performClick();
            return;
        }

        if (SpinnerCities.getSelectedItem().toString().equals(Config.DEFAULT_VALUE_SPINNER)){
            Toast.makeText(Register.this, "Please fill your City.", Toast.LENGTH_SHORT).show();
            SpinnerCities.requestFocus();
            SpinnerCities.performClick();
            return;
        }

        Global.showConfirmDialog(Register.this,"Please Confirm","Are you sure want to submit the registration data?");
        Global.alertDialog.setPositiveButton(Global.PositiveButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Global.showDialog(Register.this);
                processRegister();
            }
        });
        Global.alertDialog.setNegativeButton(Global.NegativeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        Global.alertDialog.show();

    }

    private void processRegister(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(FirebaseTables.TABLE_USER).orderByChild("email").equalTo(EditTextEmail.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Global.dismissDialog();
                    Toast.makeText(Register.this, "Your email or user data was active.", Toast.LENGTH_SHORT).show();
                } else {
                    createAccount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Global.dismissDialog();
                Toast.makeText(Register.this,"Failed to get the user data form Firebase Authentication",Toast.LENGTH_LONG).show();
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
                Register.this.finish();
            }
        });
    }

    private void saveUsersToFirebaseDB(String userId, Users users){
        mDatabase.child(FirebaseTables.TABLE_USER).child(userId).setValue(users);
    }

    private void createAccount(){
        firebaseAuth.createUserWithEmailAndPassword(EditTextEmail.getText().toString().trim(), EditTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String userID = firebaseUser.getUid();
                            Cities tmpCities = new Cities();

                            for (int i = 0; i < citiesList.size(); i++) {
                                if (SpinnerCities.getSelectedItem().toString().trim().equals(citiesList.get(i).getText().trim())){
                                    tmpCities = citiesList.get(i);
                                    break;
                                }
                            }

                            Users users = new Users(userID,EditTextName.getText().toString(),
                                    EditTextEmail.getText().toString(),EditTextPhoneNumber.getText().toString(),
                                    EditTextUserKeyID.getText().toString(),Global.getAndroidID(Register.this),"user",tmpCities);
                            saveUsersToFirebaseDB(userID,users);

                            SharedPreferences mPreferences = Register.this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
                            firstTime = mPreferences.getBoolean("firstTime", true);
                            if (firstTime) {
                                SharedPreferences.Editor editor = mPreferences.edit();
                                editor.putBoolean("firstTime", false);
                                editor.commit();
                            }

                            Global.dismissDialog();


                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Global.dismissDialog();
                            Toast.makeText(Register.this,"Failed to create your account",Toast.LENGTH_LONG).show();
                            backAction();
                        }

                    }
                });

    }
}
