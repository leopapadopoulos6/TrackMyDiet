package edu.nyit.trackmydiet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import edu.nyit.trackmydiet.enums.MealType;

public class NavigationActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        handleFloatingActionButton();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_goals, R.id.nav_results, R.id.nav_profile,
                R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    /*
     * Once the floating action button is clicked,
     * a popup menu will show that allows a user to pick
     * which meal they are adding foods to
     * @params [no params]
     * @return [void]
     */
    private void handleFloatingActionButton() {
        fab.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(NavigationActivity.this, v);
            popupMenu.setOnMenuItemClickListener(NavigationActivity.this);
            popupMenu.setGravity(Gravity.END);
            popupMenu.inflate(R.menu.homepage_popup_menu);
            popupMenu.show();
        });
    }

    /*
     * Performs the action for the chosen menu option
     * @params [MenuItem item]
     * @return [boolean]
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.breakfast_item:
                Intent breakfastIntent = new Intent(NavigationActivity.this, SearchFoodActivity.class);
                breakfastIntent.putExtra("meal_type", MealType.BREAKFAST);
                startActivity(breakfastIntent);
                return true;
            case R.id.lunch_item:
                Intent lunchIntent = new Intent(NavigationActivity.this, SearchFoodActivity.class);
                lunchIntent.putExtra("meal_type", MealType.LUNCH);
                startActivity(lunchIntent);
                return true;
            case R.id.dinner_item:
                Intent dinnerIntent = new Intent(NavigationActivity.this, SearchFoodActivity.class);
                dinnerIntent.putExtra("meal_type", MealType.DINNER);
                startActivity(dinnerIntent);
                return true;
            case R.id.snack_item:
                Intent snackIntent = new Intent(NavigationActivity.this, SearchFoodActivity.class);
                snackIntent.putExtra("meal_type", MealType.SNACK);
                startActivity(snackIntent);
                return true;
            case R.id.water_item:
                //TODO Make Water activity
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
