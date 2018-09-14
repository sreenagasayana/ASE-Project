package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gangi on 3/18/2018.
 */

public class RecipeScreen extends AppCompatActivity {

    ImageView recipeImageView;
    TextView recipeNameView;
    TextView recipeSourceURLView;
    TextView recipeIngredientsView;
    RatingBar ratingBar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameView = findViewById(R.id.recipeName);
        recipeSourceURLView = findViewById(R.id.fullRecipe);
        recipeIngredientsView = findViewById(R.id.ingredients);
        ratingBar = findViewById(R.id.ratingBar);
        dbHelper = new DatabaseHelper(this);

        try {
            Intent intent = getIntent();

            final Recipe recipe = (Recipe) intent.getExtras().getSerializable("recipeObj");

            recipeImageView.setImageBitmap(recipe.getBitmap());
            recipeNameView.setText(recipe.getRecipeName());

            String fullRecipeURL = "See Full Recipe at : <a href='"+recipe.getSourceURL()+"'>"+
                    recipe.getSourceName()+"</a>";
            recipeSourceURLView.setText(Html.fromHtml(fullRecipeURL));
            recipeSourceURLView.setMovementMethod(LinkMovementMethod.getInstance());

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("<strong><u>"+recipe.getIngredientsArray().length()+" Ingredients</u></strong><br/><ul>");
            for(int ingr = 0;ingr < recipe.getIngredientsArray().length();ingr++){
                stringBuilder.append("<li>"+recipe.getIngredientsArray().get(ingr)+"</li>");
            }
            stringBuilder.append("</ul><br/>");
            stringBuilder.append("<strong><u>Nutrients</u></strong><br/>");
            stringBuilder.append("Number of People Dish Serves:"+recipe.getPeopleServes()+"<br/>");
            stringBuilder.append("Calories/Serving : "+recipe.getCaloriePerServing());

            recipeIngredientsView.setText(Html.fromHtml(stringBuilder.toString()));


            // Check if Rating is Present for the user and the selected Recipe Combination.
            Cursor recipeRating = dbHelper.checkRatingOfRecipe(recipe.getEmailId(),recipe.getRecipeName(),recipe.getSourceName());

            if(recipeRating.getCount() != 0){
                // Set ratings given by user
                while (recipeRating.moveToNext()) {
                    ratingBar.setRating(Float.parseFloat(recipeRating.getString(4)));
                }
            }

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    // When User Modified Rating or added a Rating..
                    String rateValue = String.valueOf(ratingBar.getRating());
                    // Insert/Update User Rating..
                    Cursor recipeRating = dbHelper.checkRatingOfRecipe(recipe.getEmailId(),recipe.getRecipeName(),recipe.getSourceName());
                    if(recipeRating.getCount() == 0){
                        // No records found, Insert
                        dbHelper.insertRecipeRating(recipe.getEmailId(),recipe.getRecipeName(),recipe.getSourceName(),rateValue);
                    }else{
                        // Update Rating
                        dbHelper.updateRecipeRating(recipe.getEmailId(),recipe.getRecipeName(),recipe.getSourceName(),rateValue);
                    }
                }
            });


        }catch (JSONException jsex) {
            jsex.printStackTrace();
        }
    }

    public void getNearByLocations(View view){
        Intent intent = new Intent(RecipeScreen.this, MapsActivity.class);
        startActivity(intent);
    }
}
