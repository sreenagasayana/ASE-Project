package project.com.project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by gangi on 3/5/2018.
 */

public class UpdateProfileScreen extends AppCompatActivity{

    String emailId;

    DatabaseHelper dbHelper;
    EditText firstName,lastName,password,confirmPassword;
    TextView greeting,errormessage;
    Cursor userDetail;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        firstName = findViewById(R.id.updateFirstName);
        lastName = findViewById(R.id.updateLastName);
        password = findViewById(R.id.updatePassword);
        confirmPassword = findViewById(R.id.updateConfirmPassword);
        greeting = findViewById(R.id.updateprofilegreeting);
        errormessage = findViewById(R.id.updateProfileError);

        progressDialog = new ProgressDialog(this);

        dbHelper = new DatabaseHelper(this);
        if(null != getIntent()){
            emailId = getIntent().getStringExtra("emailId");
            // Fetching User Details if Email Id is present from Shopping Screen via SqlLite
            if(emailId != null) {
                userDetail = dbHelper.checkIfUserExist(emailId);
                if (userDetail.getCount() != 0) {
                    while (userDetail.moveToNext()) {
                        greeting.setText("Update Profile: " + emailId);
                        // Setting Fetched Values into Textboxes..
                        firstName.setText(userDetail.getString(1));
                        lastName.setText(userDetail.getString(2));
                        password.setText(userDetail.getString(4));
                        confirmPassword.setText(userDetail.getString(4));
                    }
                }
            }
        }
    }

    // On Home Screen Click.
    public  void shoppingRedirect(View view){
        Intent intent = new Intent(UpdateProfileScreen.this,ShowRecipesScreen.class);
        intent.putExtra("emailId",emailId);
        startActivity(intent);
    }

    // On Update Profile Click
    public void updateProfile(View view) {
        // Validations First..
        // Update the Profile
        progressDialog.setMessage("Updating your Profile");
        progressDialog.show();

        if (!(returnValidationForField(firstName)
                && returnValidationForField(lastName)
                && returnValidationForField(password)
                && returnValidationForField(confirmPassword))) {
            errormessage.setText("Please fill all the fields,Fields cannot be empty");
        } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            errormessage.setText("Password & Confirm passwords should match");
        } else {
            boolean isUpdateSuccessful = dbHelper.updateData(firstName.getText().toString(),lastName.getText().toString(),
                    emailId,password.getText().toString());

            // If Update is Successful, return proper message to let the User know
            // that he got updated Successfully and reload to same page to show the update fields
            if (isUpdateSuccessful) {
                progressDialog.hide();
                // Redirecting to Same page again..
                Toast.makeText(UpdateProfileScreen.this,"Profile Updated Successfully",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(UpdateProfileScreen.this,UpdateProfileScreen.class);
                intent.putExtra("emailId",emailId);
                startActivity(intent);
            }else{
                errormessage.setText("Technical Issues occured while updating details,Please try later");
            }
        }
        progressDialog.hide();
    }

    // Common Method for Checking Not Null, StringUtils Empty
    private boolean returnValidationForField(EditText text) {
        if (text != null && StringUtils.isNotBlank(text.getText().toString())) {
            return true;
        }
        return false;
    }
}
