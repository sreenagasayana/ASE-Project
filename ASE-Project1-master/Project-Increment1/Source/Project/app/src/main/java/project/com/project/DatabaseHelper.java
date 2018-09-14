package project.com.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("On Create");
        // Creating Table First
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FIRST_NAME TEXT,LAST_NAME TEXT,EMAIL_ADDRESS TEXT,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // On DB Upgrade, Dropping and Creating Table Again
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
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
        // Inserting the Data, Return Type will be 'long'
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
}
