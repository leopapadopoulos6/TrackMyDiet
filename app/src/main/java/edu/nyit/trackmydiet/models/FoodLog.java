package edu.nyit.trackmydiet.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

//TODO add comments
public class FoodLog implements Serializable {

    private String date;
    private String dayOfTheWeek;
    private Double consumedCal;
    private Double currentWeight;
    private Double consumedProtein;
    private Double consumedFat;
    private Double consumedCarbs;
    private HashMap<String, MealEntry> mealEntries;

    public FoodLog(Double consumedCal, Object currentWeight) {
        this.date = new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(new Date());
        this.dayOfTheWeek = this.initDayOfTheWeek();
        this.consumedCal = consumedCal;
        if(currentWeight instanceof Long) {
            this.currentWeight= ((Long) currentWeight).doubleValue();
        } else {
            this.currentWeight = (Double) currentWeight;
        }
        this.mealEntries = new HashMap<>();
    }

    public Double getConsumedProtein() {
        return consumedProtein;
    }

    public void setConsumedProtein(Double consumedProtein) {
        this.consumedProtein = Math.round(consumedProtein * 10)/ 10.0;
    }

    public Double getConsumedFat() {
        return consumedFat;
    }

    public void setConsumedFat(Double consumedFat) {
        this.consumedFat = Math.round(consumedFat * 10)/ 10.0;
    }

    public Double getConsumedCarbs() {
        return consumedCarbs;
    }

    public void setConsumedCarbs(Double consumedCarbs) {
        this.consumedCarbs = Math.round(consumedCarbs * 10)/ 10.0;
    }

    public String getDate() {
        return this.date;
    }

    public String getDayOfTheWeek() { return this.dayOfTheWeek; }

    public Double getConsumedCal() {
        return consumedCal;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public HashMap<String, MealEntry> getMealEntries() {
        return mealEntries;
    }

    public void addMealEntry(String mealName, MealEntry meal) { this.mealEntries.put(mealName, meal); }

    public void removeMeal(String mealName) { this.mealEntries.remove(mealName); }

    private String initDayOfTheWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch(day) {
            case Calendar.SUNDAY:
                return "Sun";
            case Calendar.MONDAY:
                return "Mon";
            case Calendar.TUESDAY:
                return "Tues";
            case Calendar.WEDNESDAY:
                return "Wed";
            case Calendar.THURSDAY:
                return "Thurs";
            case Calendar.FRIDAY:
                return "Fri";
            case Calendar.SATURDAY:
                return "Sat";
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return this.toMap().toString();
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", this.date);
        result.put("dayOfTheWeek", this.dayOfTheWeek);
        result.put("consumedCal", this.consumedCal);
        result.put("currentWeight", this.currentWeight);
        result.put("meals", this.mealEntries);
        return result;
    }
}
