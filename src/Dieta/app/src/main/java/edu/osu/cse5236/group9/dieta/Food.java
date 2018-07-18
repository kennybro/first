package edu.osu.cse5236.group9.dieta;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Siyuan on 10/29/16.
 */

public class Food implements Parcelable {
    private static final String sNutritionix_Address = "https://api.nutritionix.com/v1_1/search/";
    private String mName;
    private double mEstimated_Weight;
    private double mCalories;
    private double mTotal_Fat;
    private double mSodium;
    private double mProtein;
    private double mCholesterol;
    private double mTotal_Carbohydrates;

    private void setFoodNutritionFacts(JsonReader reader) {
        try {
            reader.beginObject();
            if(!reader.nextName().equals("total_hits")) {
                Log.d("reader","Fetch fail");
                return;
            }
            if(reader.nextInt()==0) {
                Log.d("reader","No matching");
                return;
            }
            while(reader.hasNext() && !reader.nextName().equals("hits")) {
                reader.skipValue();
            }
          
    }

    public double getProtein() {
        return mProtein;
    }

    public void setProtein(double protein) {
        mProtein = protein;
    }

    public double getCholesterol() {
        return mCholesterol;
    }

    public void setCholesterol(double cholesterol) {
        mCholesterol = cholesterol;
    }

    public double getTotal_Carbohydrates() {
        return mTotal_Carbohydrates;
    }

    public void setTotal_Carbohydrates(double total_Carbohydrates) {
        mTotal_Carbohydrates = total_Carbohydrates;
    }

    public int describeContents() {
        return 0;
    }

    private Food(Parcel in) {
        this.mName = in.readString();
        this.mEstimated_Weight = in.readDouble();
        this.mCalories = in.readDouble();
        this.mTotal_Fat = in.readDouble();
        this.mSodium = in.readDouble();
        this.mProtein = in.readDouble();
        this.mCholesterol = in.readDouble();
        this.mTotal_Carbohydrates = in.readDouble();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeDouble(mEstimated_Weight);
        dest.writeDouble(mCalories);
        dest.writeDouble(mTotal_Fat);
        dest.writeDouble(mSodium);
        dest.writeDouble(mProtein);
        dest.writeDouble(mCholesterol);
        dest.writeDouble(mTotal_Carbohydrates);
    }

    public static final Parcelable.Creator<Food> CREATOR
            = new Parcelable.Creator<Food>() {
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
}
