package project.com.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gangi on 2/24/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "SmartShoppingRegistration.db";
    private static final String TABLE_NAME = "USER_REGISTRATION";
    private static final String COLUMN_1 = "FIRST_NAME";
    private static final String COLUMN_2 = "LAST_NAME";
    private static final String COLUMN_3 = "EMAIL_ADDRESS";
    private static final String COLUMN_4 = "PASSWORD";

    // Recipe Table
    private static final String RECIPE_TABLE_NAME = "SHOPPING_RECIPE";
    private static final String RECIPE_COLUMN_1 = "RECIPE_NAME";
    private static final String RECIPE_COLUMN_2 = "RECIPE_TYPE";

    // Recipe Rating Table
    private static final String RECIPE_RATING_TABLE_NAME = "RECIPE_RATING";
    private static final String RECIPE_RATING_COLUMN_1 = "RECIPE_NAME";
    private static final String RECIPE_RATING_COLUMN_2 = "SOURCE_NAME";
    private static final String RECIPE_RATING_COLUMN_3 = "EMAIL_ADDRESS";
    private static final String RECIPE_RATING_COLUMN_4 = "RECIPE_RATING";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating Users Table
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FIRST_NAME TEXT,LAST_NAME TEXT,EMAIL_ADDRESS TEXT,PASSWORD TEXT)");

        // Creating Recipe Table
        db.execSQL("create table " + RECIPE_TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "RECIPE_NAME TEXT,RECIPE_TYPE TEXT)");

        // Creating Recipe Table
        db.execSQL("create table " + RECIPE_RATING_TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "RECIPE_NAME TEXT,SOURCE_NAME TEXT,EMAIL_ADDRESS TEXT,RECIPE_RATING TEXT)");

        // Non-Veg
        insertRecipe(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On DB Upgrade, Dropping and Creating Table Again
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+RECIPE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+RECIPE_RATING_TABLE_NAME);
        onCreate(db);
    }

    // Inserting data on signUp
    public boolean insertData(String firstName,String lastName,String emailId, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_1,firstName);
        contentValues.put(COLUMN_2,lastName);
        contentValues.put(COLUMN_3,emailId.toLowerCase());
        contentValues.put(COLUMN_4,password);
        // Inserting the Data, Return Type will be 'long'
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1) {
            //f Insert haven't happened properly, it will return a "long" value '-1'
            // as Output
            return false;
        }
        else {
            // If Insertion is Successful
            return true;
        }
    }

    // Check if data exists for the EmailId
    public Cursor checkIfUserExist(String emailId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where LOWER(EMAIL_ADDRESS) = '"+emailId.toLowerCase()+"'"
                ,null);
        return res;
    }

    // Check if Password is correct for the EmailId
    public Cursor checkIfPasswordIsCorrect(String emailId, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where LOWER(EMAIL_ADDRESS) = '"+emailId.toLowerCase()+"'"
               +" AND PASSWORD = '"+password+"'" ,null);
        return res;
    }


    public boolean updateData(String firstName,String lastName,String emailId, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        String filter = "LOWER(EMAIL_ADDRESS)='"+emailId.toLowerCase()+"'";
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_1,firstName);
        contentValues.put(COLUMN_2,lastName);
        contentValues.put(COLUMN_3,emailId.toLowerCase());
        contentValues.put(COLUMN_4,password);
        // Updating the Data, Return Type will be 'long'
        long result = db.update(TABLE_NAME,contentValues ,filter,null);
        if(result == -1) {
            //f Insert haven't happened properly, it will return a "long" value '-1'
            // as Output
            return false;
        }
        else {
            // If Insertion is Successful
            return true;
        }
    }

    private void insertRecipe(SQLiteDatabase db) {

        insertRecipeData("Chicken","Non-Veg",db);
        insertRecipeData("Beef","Non-Veg",db);
        insertRecipeData("Pork","Non-Veg",db);
        insertRecipeData("Mutton","Non-Veg",db);

        // Veg
        insertRecipeData("Potatoes","Veg",db);
        insertRecipeData("Peas","Veg",db);
        insertRecipeData("Tomatoes","Veg",db);

        // Others
        insertRecipeData("Low Fat","others",db);
        insertRecipeData("Juice","others",db);
        insertRecipeData("Smoothies","others",db);
    }

    // Inserting one time recipe data
    public boolean insertRecipeData(String recipeName,String recipeType,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_COLUMN_1,recipeName);
        contentValues.put(RECIPE_COLUMN_2,recipeType);
        // Inserting the Data, Return Type will be 'long'
        long result = db.insert(RECIPE_TABLE_NAME,null ,contentValues);
        if(result == -1) {
            //f Insert haven't happened properly, it will return a "long" value '-1'
            // as Output
            return false;
        }
        else {
            // If Insertion is Successful
            return true;
        }
    }

    // Fetch Recipe Details
    public List<ShowRecipe> fetchRecipes() {
        List<ShowRecipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+RECIPE_TABLE_NAME,null);

        if (res.getCount() != 0) {
            try {
                while (res.moveToNext()) {
                    ShowRecipe recipe = new ShowRecipe();
                    recipe.setRecipeName(res.getString(1));
                    recipe.setRecipeType(res.getString(2));
                    recipeList.add(recipe);
                }
            }finally {
                res.close();
            }
        }
        return recipeList;
    }

    // Check if Recipe Rating is present for the Input user,RecipeName,SourceName
    public Cursor checkRatingOfRecipe(String emailId,String recipeName, String sourceName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+RECIPE_RATING_TABLE_NAME+" where LOWER(EMAIL_ADDRESS) = '"+emailId.toLowerCase()
                        +"' AND RECIPE_NAME ='"+recipeName+"' AND SOURCE_NAME = '"+sourceName+"'"
                ,null);
        return res;
    }


    // Inserting Rating Data
    public boolean insertRecipeRating(String emailId,String recipeName, String sourceName, String rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_RATING_COLUMN_1,recipeName);
        contentValues.put(RECIPE_RATING_COLUMN_2,sourceName);
        contentValues.put(RECIPE_RATING_COLUMN_3,emailId);
        contentValues.put(RECIPE_RATING_COLUMN_4,rating);
        // Inserting the Data, Return Type will be 'long'
        long result = db.insert(RECIPE_RATING_TABLE_NAME,null ,contentValues);
        if(result == -1) {
            //f Insert haven't happened properly, it will return a "long" value '-1'
            // as Output
            return false;
        }
        else {
            // If Insertion is Successful
            return true;
        }
    }

    // Updating Rating Data
    public boolean updateRecipeRating(String emailId,String recipeName, String sourceName, String rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_RATING_COLUMN_1,recipeName);
        contentValues.put(RECIPE_RATING_COLUMN_2,sourceName);
        contentValues.put(RECIPE_RATING_COLUMN_3,emailId);
        contentValues.put(RECIPE_RATING_COLUMN_4,rating);
        // Inserting the Data, Return Type will be 'long'

        String filter = "LOWER(EMAIL_ADDRESS) = '"+emailId.toLowerCase()
                +"' AND RECIPE_NAME ='"+recipeName+"' AND SOURCE_NAME = '"+sourceName+"'";
        // Updating the Data, Return Type will be 'long'
        long result = db.update(RECIPE_RATING_TABLE_NAME,contentValues ,filter,null);
        if(result == -1) {
            //f Insert haven't happened properly, it will return a "long" value '-1'
            // as Output
            return false;
        }
        else {
            // If Insertion is Successful
            return true;
        }
    }

}
