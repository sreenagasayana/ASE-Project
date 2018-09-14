package project.com.project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.models.Tweet;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by gangi on 3/18/2018.
 */

public class RecipeScreen extends AppCompatActivity{

    ImageView recipeImageView;
    TextView recipeNameView;
    TextView recipeSourceURLView;
    TextView recipeIngredientsView;
    TextView twitterDetailsView;
    RatingBar ratingBar;
    RatingBar avgRatingBar;
    DatabaseHelper dbHelper;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 10) {
                Intent intent = new Intent(RecipeScreen.this,UnityPlayerProxyActivity.class);
                startActivity(intent);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeNameView = findViewById(R.id.recipeName);
        recipeSourceURLView = findViewById(R.id.fullRecipe);
        recipeIngredientsView = findViewById(R.id.ingredients);
        twitterDetailsView = findViewById(R.id.twitterdetails);
        ratingBar = findViewById(R.id.ratingBar);
        avgRatingBar = findViewById(R.id.avgRatingBar);
        dbHelper = new DatabaseHelper(this);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

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

            // Twitter tags fetch
            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            configurationBuilder.setDebugEnabled(true).setOAuthConsumerKey("rp0v0X6R57uNLHo8zH2UAhN71").
                    setOAuthConsumerSecret("B8FmARiycq0UqR1bV9ZNS1sIbTOmVXctxPNEHt6G79LwRD1myQ")
                    .setOAuthAccessToken("968744935993364486-4jdDSWRTey9tN8t0LGH5vlfFNs3tvOD").
                    setOAuthAccessTokenSecret("LYOxuwdxcEBEyL3FWtbrbiqIvhdNc0GRunh6UGlrJx1mW");

            TwitterFactory twitterFactory = new TwitterFactory(configurationBuilder.build());
            Twitter twitter = twitterFactory.getInstance();

            // Querying for HashTag
            Query query = new Query("#"+ StringUtils.normalizeSpace(recipe.getRecipeName()));
            query.setCount(6);

            /*QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                System.out.println("@" + status.getUser().getScreenName() + " : " + status.getText() + " : " + status.getGeoLocation());
            }*/

            QueryResult result = twitter.search(query);
            ArrayList tweets = (ArrayList) result.getTweets();

            StringBuilder twitterStringBuilder = new StringBuilder();
            for (int i = 0; i < tweets.size(); i++) {
                Status t = (Status) tweets.get(i);
                    tweets.get(i++);
                    String user = t.getUser().getScreenName();
                    String msg = t.getText();
                twitterStringBuilder.append("<li> @"+user+ " - "+msg+"</li>");
            }
            twitterDetailsView.setText(Html.fromHtml(twitterStringBuilder.toString()));

            Cursor avgRatingCursor = dbHelper.getRatingsOfRecipe(recipe.getRecipeName(),recipe.getSourceName());
            Float avgRating = 0.0f;
            Float totalRating = 0.0f;
            if(avgRatingCursor.getCount() != 0){
                // Set ratings given by user
                while (avgRatingCursor.moveToNext()) {
                    if(StringUtils.isNotEmpty(avgRatingCursor.getString(4))){
                        totalRating = totalRating+Float.parseFloat(avgRatingCursor.getString(4));
                    }
                }
                avgRating = totalRating/avgRatingCursor.getCount();
            }
            avgRatingBar.setRating(avgRating);

        }catch (JSONException | TwitterException jsex) {
            jsex.printStackTrace();
        }
    }

    public void getNearByLocations(View view){
        Intent intent = new Intent(RecipeScreen.this, MapsActivity.class);
        startActivity(intent);
    }
}
