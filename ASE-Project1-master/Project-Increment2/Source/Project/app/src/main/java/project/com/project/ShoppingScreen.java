package project.com.project;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gangi on 2/23/2018.
 */

public class ShoppingScreen extends AppCompatActivity {

    //Fields from Login Screen
    String emailId, password, firstName, lastName, id = "";
    // Shopping Text on Login
    TextView shoppingText;

    // Database Helper
    DatabaseHelper dbHelper;

    RelativeLayout shoppinglayout;
    ImageView imageView;
    String selectedRecipe;

    // Linear Layout to create Dynamic Data
    LinearLayout shoppingLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        dbHelper = new DatabaseHelper(this);

        if(getIntent() != null){
            selectedRecipe = getIntent().getStringExtra("selectedRecipe");
        }

        // Get the widgets reference from XML layout
        shoppinglayout = findViewById(R.id.shoppinglayout);

        shoppingLinear = new LinearLayout(getApplicationContext());
        //shoppingLinear = findViewById(R.id.shoppinglinearlayout);

        Intent intent = getIntent();

        if (null != intent) {
            emailId = intent.getStringExtra("emailId");
        }

        // Get Recipe Details
        getRecipeDetails(selectedRecipe);
    }

    // OnLogout Click
    public void logout(View v) {
        // Redirecting to Login Screen on logout
        Intent loginRedirect = new Intent(ShoppingScreen.this, LoginScreen.class);
        startActivity(loginRedirect);
    }

    // On Update Profile Click
    public void editUserProfile(View view) {
        Intent intent = new Intent(ShoppingScreen.this, UpdateProfileScreen.class);
        intent.putExtra("emailId", emailId);
        startActivity(intent);
    }

    // API call to get Recipes
    private void getRecipeDetails(String selectedRecipe) {
        // API Service
        String getURL = "https://api.edamam.com/search?q=" + selectedRecipe + "&app_id=0da1fc3a" +
                "&app_key=1e189e176e3f058fab55715279137a81&from=0&to=10";
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    try {
                        jsonResult = new JSONObject(result);
                        JSONArray convertedTextArray = jsonResult.getJSONArray("hits");
                        for(int itr = 0;itr<convertedTextArray.length();itr++){
                            JSONObject recipeHitsObj = convertedTextArray.getJSONObject(itr);

                            final JSONObject recipeObj = recipeHitsObj.getJSONObject("recipe");
                            final String recipeImageUrl = recipeObj.getString("image");
                            final Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(recipeImageUrl).getContent());
                            final String recipeName = recipeObj.getString("label");
                            final String recipeSource = recipeObj.getString("source");
                            final String recipeSourceURL = recipeObj.getString("url");
                            final Double peopleRecipeServes = (Double) recipeObj.get("yield");
                            final Double calories = (Double) recipeObj.get("calories");

                            System.out.println(recipeImageUrl);
                            final int iteratingInt = itr+1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   /* ImageView image = new ImageView(getApplicationContext());
                                    image.setImageBitmap(bitmap);
                                    image.setPadding(0,0,0,20);
                                    shoppingLinear.addView(image);*/

                                   /*LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(shoppinglayout.getLayoutParams());

                                    TextView textView = new TextView(getApplicationContext());
                                    Drawable imgDrawable = new BitmapDrawable(bitmap);
                                    imgDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                                    textView.setCompoundDrawables(null, imgDrawable , null, null);
                                    textView.setText(recipeName);
                                    textView.setTextColor(Color.parseColor("#FFBDBDBD"));

                                    shoppingLinear.addView(textView);*/

                                   System.out.println(recipeImageUrl);
                                    TextView txtView = findViewById(R.id.textView+iteratingInt);
                                    txtView.setText(Html.fromHtml("<strong>"+recipeName+"</strong><br/><br/>"));
                                    BitmapDrawable drawableLeft = new BitmapDrawable(getResources(), bitmap);
                                    txtView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
                                    String linkText = "Recipe Source : <a href='"+recipeSourceURL+"'>"+recipeSource+"</a>";
                                    txtView.append(Html.fromHtml(linkText));
                                    txtView.setMovementMethod(LinkMovementMethod.getInstance());
                                    txtView.setVisibility(View.VISIBLE);

                                    txtView.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            Intent intent = new Intent(ShoppingScreen.this, RecipeScreen.class);
                                            try {
                                                Recipe recipe = new Recipe();
                                                recipe.setEmailId(emailId);
                                                recipe.setSourceName(recipeSource);
                                                recipe.setSourceURL(recipeSourceURL);
                                                recipe.setRecipeName(recipeName);
                                                recipe.setIngredientsArray((JSONArray) recipeObj.get("ingredientLines"));
                                                recipe.setBitmap(bitmap);
                                                recipe.setPeopleServes((int)Math.round(peopleRecipeServes));
                                                recipe.setCaloriePerServing((int)Math.round(calories/peopleRecipeServes));
                                                intent.putExtra("recipeObj", recipe);
                                            }catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        }
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }catch (Exception ex) {
           ex.printStackTrace();
        }
    }
}
