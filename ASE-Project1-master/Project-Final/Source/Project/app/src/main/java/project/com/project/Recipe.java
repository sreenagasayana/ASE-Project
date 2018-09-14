package project.com.project;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.io.Serializable;

/**
 * Created by gangi on 3/18/2018.
 */

public class Recipe implements Serializable{

    private String sourceName;
    private String sourceURL;
    private String recipeName;
    private static transient JSONArray ingredientsArray;
    private static transient Bitmap bitmap;
    private Integer caloriePerServing;
    private Integer peopleServes;
    private String emailId;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Integer getCaloriePerServing() {
        return caloriePerServing;
    }

    public void setCaloriePerServing(Integer caloriePerServing) {
        this.caloriePerServing = caloriePerServing;
    }

    public Integer getPeopleServes() {
        return peopleServes;
    }

    public void setPeopleServes(Integer peopleServes) {
        this.peopleServes = peopleServes;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getSourceName() {

        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public JSONArray getIngredientsArray() {
        return ingredientsArray;
    }

    public void setIngredientsArray(JSONArray ingredientsArray) {
        this.ingredientsArray = ingredientsArray;
    }
}
