package edu.nyit.trackmydiet.tasks;

import android.os.AsyncTask;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import edu.nyit.trackmydiet.interfaces.SearchNutrientsListener;
import edu.nyit.trackmydiet.models.FoodAttributes;
import edu.nyit.trackmydiet.models.ServiceResult;

import static android.content.ContentValues.TAG;

//TODO add comments
public class SearchNutrientsTask  extends AsyncTask<String, Void, ServiceResult> {

    SearchNutrientsListener searchNutrientsListener;

    public SearchNutrientsTask(SearchNutrientsListener searchNutrientsListener) {
        this.searchNutrientsListener = searchNutrientsListener;
    }

    @Override
    protected ServiceResult doInBackground(String... params) {
        String searchQuery = params[0];
        String getFood = "https://trackapi.nutritionix.com/v2/natural/nutrients/";
        ServiceResult result = new ServiceResult();
        String inputLine;
        try {
            URL url = new URL(getFood);
            HttpURLConnection connection = (HttpURLConnection)
                    url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("x-app-id", "83c6512e");
            connection.setRequestProperty("x-app-key", "e4e7b68984189e319d07d8955340197f");
            connection.setRequestProperty("x-remote-user-id", "0");

//            String body = ("{query: cheese}");
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("query", searchQuery);
            String body = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(body);
            writer.flush();
            writer.close();
            os.close();

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
                System.out.println(serviceResult);
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
                JSONObject jsonFoods = new JSONObject(message.getMessage());
                JSONArray jsonFoodsArray = jsonFoods.getJSONArray("foods");
                JSONObject foodJson = jsonFoodsArray.getJSONObject(0);
                FoodAttributes food = new FoodAttributes();
                food.setNameCaps(foodJson.getString("food_name"));
                food.setServingUnit(foodJson.getString("serving_unit"));
                setServingWeight(foodJson, food);
                setCalories(foodJson, food);
                setCarbs(foodJson, food);
                setCholesterol(foodJson, food);
                setFiber(foodJson, food);
                setPotassium(foodJson, food);
                setProtein(foodJson, food);
                setSaturatedFats(foodJson, food);
                setSodium(foodJson, food);
                setSugar(foodJson, food);
                setTotalFats(foodJson, food);
                Log.d(TAG, food.getName());
                searchNutrientsListener.onFoundNutrients(food);
            } catch (JSONException | CloneNotSupportedException e) {
                e.printStackTrace();
            }
        } else if (message.getCode() != 500) {
            String errorMessage = message.getMessage();
            try {
                JSONObject json = new JSONObject(errorMessage);
                if (searchNutrientsListener != null)
                    searchNutrientsListener.onErrorSearchNutrients(message.getMessage());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (searchNutrientsListener != null)
                searchNutrientsListener.onErrorSearchNutrients("System error. Please try again later.");
        }
    }

    private void setServingWeight(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if(foodJson.get("serving_weight_grams").toString().equals("null")) {
            food.setServingWeightOz(0.0);
        } else {
            food.setServingWeightOz(foodJson.getDouble("serving_weight_grams"));
        }
    }

    private void setCalories(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_calories").toString().equals("null")) {
            food.setCalories(0.0);
        } else {
            food.setCalories(foodJson.getDouble("nf_calories"));
        }
    }

    private void setTotalFats(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_total_fat").toString().equals("null")) {
            food.setTotalFats(0.0);
        } else {
            food.setTotalFats(foodJson.getDouble("nf_total_fat"));
        }
    }

    private void setSaturatedFats(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_saturated_fat").toString().equals("null")) {
            food.setSaturatedFats(0.0);
        } else {
            food.setSaturatedFats(foodJson.getDouble("nf_saturated_fat"));
        }
    }

    private void setCholesterol(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_cholesterol").toString().equals("null")) {
            food.setCholesterol(0.0);
        } else {
            food.setCholesterol(foodJson.getDouble("nf_cholesterol"));
        }
    }

    private void setSodium(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_sodium").toString().equals("null")) {
            food.setSodium(0.0);
        } else {
            food.setSodium(foodJson.getDouble("nf_sodium"));
        }
    }

    private void setCarbs(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_total_carbohydrate").toString().equals("null")) {
            food.setCarbs(0.0);
        } else {
            food.setCarbs(foodJson.getDouble("nf_total_carbohydrate"));
        }
    }

    private void setFiber(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_dietary_fiber").toString().equals("null")) {
            food.setFiber(0.0);
        } else {
            food.setFiber(foodJson.getDouble("nf_dietary_fiber"));
        }
    }

    private void setSugar(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_sugars").toString().equals("null")) {
            food.setSugar(0.0);
        } else {
            food.setSugar(foodJson.getDouble("nf_sugars"));
        }
    }

    private void setProtein(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_protein").toString().equals("null")) {
            food.setProtein(0.0);
        } else {
            food.setProtein(foodJson.getDouble("nf_protein"));
        }
    }

    private void setPotassium(JSONObject foodJson, FoodAttributes food) throws JSONException {
        if (foodJson.get("nf_potassium").toString().equals("null")) {
            food.setPotassium(0.0);
        } else {
            food.setPotassium(foodJson.getDouble("nf_potassium"));
        }
    }
}


