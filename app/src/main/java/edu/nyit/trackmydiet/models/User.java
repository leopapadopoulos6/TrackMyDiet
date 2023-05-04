package edu.nyit.trackmydiet.models;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String firstName;
    private String lastName;
    private boolean gender;
    private double weight;
    private double height;
    private int age;
    private int dailyCalories;
    private int dailyCarbs;
    private int dailyProtein;
    private int dailyFat;
    private boolean firstSignIn;

    /*
      Constructor for the User class that sets the proper fields
      from user input and gives everything else default values
      @params [String firstName, String lastName, boolean gender]
      @return [no return value]
     */
    public User(String firstName, String lastName, boolean gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.weight = -1.0;
        this.height = -1.0;
        this.age = -1;
        this.dailyCalories = -1;
        this.dailyCarbs = -1;
        this.dailyProtein = -1;
        this.dailyFat = -1;
        this.firstSignIn = true;
    }

    /*
     * Getter methods for all fields
     */
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isGender() {
        return gender;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public int getAge() {
        return age;
    }

    public int getDailyCalories() {
        return dailyCalories;
    }

    public int getDailyCarbs() {
        return dailyCarbs;
    }

    public int getDailyProtein() {
        return dailyProtein;
    }

    public int getDailyFat() {
        return dailyFat;
    }

    public boolean isFirstSignIn() {
        return firstSignIn;
    }

    /*
     * Converts the current user object to a HashMap data structure
     * @params [no params]
     * @return [HashMap<String, Object>]
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("age", this.age);
        result.put("dailyCalories", this.dailyCalories);
        result.put("dailyCarbs", this.dailyCarbs);
        result.put("dailyFat", this.dailyFat);
        result.put("dailyProtein", this.dailyProtein);
        result.put("firstName", this.firstName);
        result.put("firstSignIn", this.firstSignIn);
        result.put("gender", this.gender);
        result.put("height", this.height);
        result.put("lastName", this.lastName);
        result.put("weight", this.weight);

        return result;
    }
}
