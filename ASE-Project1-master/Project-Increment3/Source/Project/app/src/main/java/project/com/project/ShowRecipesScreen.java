package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangi on 3/19/2018.
 */

public class ShowRecipesScreen extends AppCompatActivity {

    List<ShowRecipe> recipeList;
    DatabaseHelper dbHelper;
    TextView veg;
    TextView nonveg;
    TextView others;
    String emailId;
    Cursor userDetail;
    Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showrecipes);

        dbHelper = new DatabaseHelper(this);
        recipeList = new ArrayList<>();
        profileButton = findViewById(R.id.userdetails);

        Intent intent = getIntent();

        if (null != intent) {
            emailId = intent.getStringExtra("emailId");
            // Fetching User Details if Email Id is present from Login Screen via SqlLite
            if (emailId != null) {
                userDetail = dbHelper.checkIfUserExist(emailId);
                if (userDetail.getCount() != 0) {
                    profileButton.setVisibility(View.VISIBLE);
                }
            }
        }

        veg = findViewById(R.id.veg1);
        nonveg = findViewById(R.id.nv1);
        others = findViewById(R.id.others1);
        // Fetch Recipe List
        recipeList = dbHelper.fetchRecipes();

        int ve = 1,nv = 1,othr = 1;
        for(ShowRecipe recipe : recipeList){
            if(recipe != null){
                if(recipe.getRecipeType().equalsIgnoreCase("Veg")){
                    veg = findViewById(R.id.veg+ve);
                    veg.setText(recipe.getRecipeName());
                    veg.setTag(recipe.getRecipeName());
                    ve++;
                }else if(recipe.getRecipeType().equalsIgnoreCase("Non-Veg")){
                    nonveg = findViewById(R.id.nv+nv);
                    nonveg.setText(recipe.getRecipeName());
                    nonveg.setTag(recipe.getRecipeName());
                    nv++;
                }else{
                    others = findViewById(R.id.others+othr);
                    others.setText(recipe.getRecipeName());
                    others.setTag(recipe.getRecipeName());
                    othr++;
                }
            }
        }
    }

    public void goToShoppingScreen(View view){
        String selectedRecipe = view.getTag().toString();
        // Redirecting to another Screen by passing selected Recipe from Intent
        Intent intent = new Intent(ShowRecipesScreen.this,ShoppingScreen.class);
        intent.putExtra("selectedRecipe",selectedRecipe);
        intent.putExtra("emailId",emailId);
        startActivity(intent);
    }


    // OnLogout Click
    public void shoppingLogout(View v) {
        // Redirecting to Login Screen on logout
        Intent loginRedirect = new Intent(ShowRecipesScreen.this, LoginScreen.class);
        startActivity(loginRedirect);
    }

    // On Update Profile Click
    public void editUserProfileShopping(View view) {
        Intent intent = new Intent(ShowRecipesScreen.this, UpdateProfileScreen.class);
        intent.putExtra("emailId", emailId);
        startActivity(intent);
    }
}
