package edu.nyit.trackmydiet.tasks;

import android.os.AsyncTask;

import edu.nyit.trackmydiet.models.FoodItem;
import edu.nyit.trackmydiet.models.ServiceResult;
import edu.nyit.trackmydiet.interfaces.SearchFoodListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.Attributes;


public class SearchFoodTask extends AsyncTask<String, Void, ServiceResult > {

    SearchFoodListener searchFoodListener;
    ArrayList<FoodItem> attributes;

    public SearchFoodTask(SearchFoodListener searchFoodListener) {
        this.searchFoodListener = searchFoodListener;
    }

    @Override
    protected ServiceResult doInBackground(String... params) {
        String searchQuery = params[0];
        String getFood = "https://trackapi.nutritionix.com/v2/search/instant?query=" + searchQuery;
        ServiceResult result = new ServiceResult();
        String inputLine;
        try {
            URL url = new URL(getFood);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("x-app-id", "83c6512e");
            connection.setRequestProperty("x-app-key", "e4e7b68984189e319d07d8955340197f");
            connection.setRequestProperty("x-remote-user-id", "0");

            connection.connect();

            result.setCode(connection.getResponseCode());

            if (result.getCode() == 200) {

                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String serviceResult = stringBuilder.toString();
                // System.out.println(serviceResult);
                result.setMessage(serviceResult);
                return result;
                //result.setMessage(serviceResult);
            } else {
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getErrorStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                String errorMessage = stringBuilder.toString();
                result.setMessage(errorMessage);
                return result;
            }


        } catch (IOException e) {
            e.printStackTrace();
            //result = null;
        }
        return null;
    }


    @Override
    protected void onPostExecute(ServiceResult message) {
        if (message.getCode() == 200) {
            try {
                attributes = new ArrayList<>();
                JSONObject jsonFoods = new JSONObject(message.getMessage());
                JSONArray jsonFoodsArray = jsonFoods.getJSONArray("common");

                for (int i = 0; i < jsonFoodsArray.length(); i++) {
                    JSONObject foodJson = jsonFoodsArray.getJSONObject(i);
                    FoodItem food = new FoodItem();
                    food.setName(foodJson.getString("food_name"));
                    food.setServingUnit(foodJson.getString("serving_unit"));
                    attributes.add(food);
                }
                searchFoodListener.onFoundFoods(attributes);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (message.getCode() != 500) {
            String errorMessage = message.getMessage();
            try {
                JSONObject json = new JSONObject(errorMessage);
                if (searchFoodListener != null)
                    searchFoodListener.onErrorSearchFood(message.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(message.getCode() == 255) {
            return;
        } else {
            if (searchFoodListener != null)
                searchFoodListener.onErrorSearchFood("System error. Please try again later.");
        }
    }

    //used for when search button is pressed not when the text is changed
    public void onPostExecuteButton(ServiceResult message) {
        if (message.getCode() == 255) {
            try {
                attributes = new ArrayList<>();
                JSONObject jsonFoods = new JSONObject(message.getMessage());
                JSONArray jsonFoodsArray = jsonFoods.getJSONArray("common");
                for (int i = 0; i < jsonFoodsArray.length(); i++) {
                    JSONObject foodJson = jsonFoodsArray.getJSONObject(i);
                    FoodItem food = new FoodItem();
                    food.setName(foodJson.getString("food_name"));
                    food.setServingUnit(foodJson.getString("serving_unit"));
                    attributes.add(food);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (message.getCode() != 500) {
            String errorMessage = message.getMessage();
            try {
                JSONObject json = new JSONObject(errorMessage);
                if (searchFoodListener != null)
                    searchFoodListener.onErrorSearchFood(message.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (searchFoodListener != null)
                searchFoodListener.onErrorSearchFood("System error. Please try again later.");
        }
    }

    public ArrayList<FoodItem> getAttributes() {
        return attributes;
    }
}

