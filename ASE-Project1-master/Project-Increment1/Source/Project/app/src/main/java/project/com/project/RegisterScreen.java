package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by gangi on 2/23/2018.
 */

public class RegisterScreen extends AppCompatActivity {

    private EditText registerFirstName;
    private EditText registerLastName;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerConfirmPassword;
    private TextView displayText;

    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";

    // Database Helper
    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registerFirstName = findViewById(R.id.registerFirstName);
        registerLastName = findViewById(R.id.registerLastName);
        registerEmail = findViewById(R.id.registerEmailAddress);
        registerPassword = findViewById(R.id.registerPassword);
        registerConfirmPassword = findViewById(R.id.registerConfirmPassword);
        displayText = findViewById(R.id.displayText);

        dbHelper = new DatabaseHelper(this);
    }

    // On SignUp Click
    public void registerUser(View view) {
        // Do Basic Validations.
        System.out.println(registerFirstName.getText().toString() + "," + registerLastName.getText().toString() + "," +
                registerEmail.getText().toString() + "," + registerPassword.getText().toString() + "," +
                registerConfirmPassword.getText().toString());
        if (!(returnValidationForField(registerFirstName)
                && returnValidationForField(registerLastName)
                && returnValidationForField(registerEmail)
                && returnValidationForField(registerPassword)
                && returnValidationForField(registerConfirmPassword))) {
            displayText.setText("Please fill all the fields,Fields cannot be empty");
            displayText.setVisibility(View.VISIBLE);
        } else if (!registerPassword.getText().toString().equals(registerConfirmPassword.getText().toString())) {
            displayText.setText("Password & Confirm passwords should match");
            displayText.setVisibility(View.VISIBLE);
        } else if (!Pattern.matches(EMAIL_PATTERN, registerEmail.getText().toString())) {
            displayText.setText("Please enter Valid Email,Ex: Kranthi@gmail.com");
            displayText.setVisibility(View.VISIBLE);
        } else {
            // Checking If User already exist
            Cursor userDetail = dbHelper.checkIfUserExist(registerEmail.getText().toString());
            if(userDetail.getCount() == 0){
                boolean isInsertSuccessful = dbHelper.insertData(registerFirstName.getText().toString(),
                        registerLastName.getText().toString(), registerEmail.getText().toString(),
                        registerPassword.getText().toString());
                // If Insertion is Successful, return proper message to let the User know
                // that he got signed Up Successfully
                if (isInsertSuccessful) {
                    // Setting Successful Message
                    displayText.setText("You '" + registerEmail.getText().toString() + "' have successfully signed up, LOGIN to continue");
                    displayText.setVisibility(View.VISIBLE);
                }else{
                    // Setting Successful Message
                    displayText.setText("SignUp is unsuccessful due to some technical Issues, Please try again later");
                    displayText.setVisibility(View.VISIBLE);
                }
            }else {
                // If User Already Exist
                // Throwing Error
                displayText.setText("You '" + registerEmail.getText().toString() + "' have already signed up, Please LOGIN to continue");
                displayText.setVisibility(View.VISIBLE);
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

    // OnLogin Click
    public void loginRedirect(View view) {
        Intent loginRedirect = new Intent(RegisterScreen.this, LoginScreen.class);
        startActivity(loginRedirect);
    }

    // Reset Button Click
    public void reset(View view){
        registerFirstName.setText("");
        registerLastName.setText("");
        registerEmail.setText("");
        registerPassword.setText("");
        registerConfirmPassword.setText("");
        displayText.setText("");
        displayText.setVisibility(View.INVISIBLE);
    }
}
