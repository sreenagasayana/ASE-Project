package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by gangi on 2/23/2018.
 */

public class ShoppingScreen extends AppCompatActivity {

    //Fields from Login Screen
    String emailId,password,firstName,lastName,id = "";
    // Shopping Text on Login
    TextView shoppingText;

    // Database Helper
    DatabaseHelper dbHelper;

    // Cursor Details
    Cursor userDetail;
    Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        profileButton = findViewById(R.id.userprofile);
        dbHelper = new DatabaseHelper(this);
        Intent intent = getIntent();

        if (null != intent) {
            emailId = intent.getStringExtra("emailId");
            // Fetching User Details if Email Id is present from Login Screen via SqlLite
            if(emailId != null) {
                userDetail = dbHelper.checkIfUserExist(emailId);
                if (userDetail.getCount() != 0) {
                    profileButton.setVisibility(View.VISIBLE);
                }
            }
        }

        // Setting Shopping Text
        shoppingText = findViewById(R.id.shoppingText);
        shoppingText.setText("Hello "+emailId+", Welcome to Smart Shopping.. We are currently under Progress," +
                "we will be back soon with something very interesting !!");
    }

    // OnLogout Click
    public void logout(View v) {
        // Redirecting to Login Screen on logout
        Intent loginRedirect = new Intent(ShoppingScreen.this, LoginScreen.class);
        startActivity(loginRedirect);
    }

    // On Update Profile Click
    public void editUserProfile(View view){
        Intent intent = new Intent(ShoppingScreen.this,UpdateProfileScreen.class);
        intent.putExtra("emailId",emailId);
        startActivity(intent);
    }


    public void getStores(View view){
        Intent intent = new Intent(ShoppingScreen.this,Stores.class);
        startActivity(intent);
    }
}
