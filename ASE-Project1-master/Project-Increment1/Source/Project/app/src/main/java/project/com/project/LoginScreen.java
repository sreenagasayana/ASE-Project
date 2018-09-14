package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Kranthi on 2/22/2018.
 */

public class LoginScreen extends AppCompatActivity {

    private EditText loginEmail;
    private EditText loginPassword;
    private TextView errorText;

    // Database Helper
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmailAddress);
        loginPassword = findViewById(R.id.loginPassword);
        errorText = findViewById(R.id.loginError);

        dbHelper = new DatabaseHelper(this);
    }

    // OnClick of Register
    public void navigateToRegisterScreen(View view) {
        Intent redirect = new Intent(LoginScreen.this, RegisterScreen.class);
        startActivity(redirect);
    }

    // OnClick of Login
    public void login(View view) {
        // Basic Validations.
        if (!(returnValidationForField(loginEmail) && returnValidationForField(loginPassword))) {
            errorText.setText("Fields cannot be empty");
            errorText.setVisibility(View.VISIBLE);
        } else{
            Cursor userDetail = dbHelper.checkIfUserExist(loginEmail.getText().toString());
            if(userDetail.getCount() == 0){
                // Throw Error Saying Invalid Email, SignUp to Continue
                errorText.setText("You "+loginEmail.getText().toString()+" haven't signedup yet, Please signup for Smart" +
                        "Shopping");
                errorText.setVisibility(View.VISIBLE);
            }else{
                // If User Mail Exist, Check if Password is correct
                Cursor user = dbHelper.checkIfPasswordIsCorrect(loginEmail.getText().toString(),
                        loginPassword.getText().toString());
                if(user.getCount() == 0){
                    // Throw Error for Wrong Password
                    errorText.setText("Invalid Password for "+ loginEmail.getText().toString() +", Please try with Valid One");
                    errorText.setVisibility(View.VISIBLE);
                }else{
                    while(user.moveToNext()) {
                        Intent pageRedirect = new Intent(LoginScreen.this, ShoppingScreen.class);
                        pageRedirect.putExtra("emailId",user.getString(3));
                        startActivity(pageRedirect);
                    }
                }
            }
        }
    }

    // Common Method for Checking Not Null, StringUtils Empty
    private boolean returnValidationForField(EditText text) {
        if (text != null && StringUtils.isNotBlank(text.getText().toString())) {
            return true;
        }
        return false;
    }

}