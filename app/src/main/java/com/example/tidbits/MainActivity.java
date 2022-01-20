package com.example.tidbits;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

/*
TODO: add style for toolbar, add options to toolbar menu
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private ImageView tidbitsLogo;

    /**
     * OnCreate function for Main, implements:
     * 1. SubMenu to switch users
     * 2. ActionBar
     * 3. onClick for fragment selection of users
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();

        // TODO: Access database and list Kid users
        SubMenu subMenu = menu.addSubMenu(Menu.NONE, Menu.NONE, 102,R.string.switch_user);
        subMenu.add("User 1");


        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // start the select subject fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new SelectSubjectFragment(), SelectSubjectFragment.class.getName()).commit();

        // listener for logo click
        tidbitsLogo = findViewById(R.id.tidbits_logo);
        tidbitsLogo.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment selectSubjectFragment = getSupportFragmentManager().findFragmentByTag(SelectSubjectFragment.class.getName());
                if (selectSubjectFragment == null) {
                    selectSubjectFragment = new SelectSubjectFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, selectSubjectFragment, SelectSubjectFragment.class.getName())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                }
            }
        });

    }

    /**
     * Program that selects a menu item from the given listings.
     *
     * @param item
     * @return boolean true to signify item selected
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Log.i(TAG, "itemClicked: " + itemId);
        switch(itemId) {
            case R.id.navigation_item_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.about_message) + "\n" + getString(R.string.authors))
                        .setTitle(R.string.about)
                        .setNegativeButton(R.string.close_dialog, null).show();
                return true;
            case R.id.navigation_item_home:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Fragment homeFragment = getSupportFragmentManager().findFragmentByTag(SelectSubjectFragment.class.getName());
                if (homeFragment == null) {
                    homeFragment = new SelectSubjectFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment, homeFragment, SelectSubjectFragment.class.getName())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.navigation_item_settings:
                Fragment settingsFragment = getSupportFragmentManager().findFragmentByTag(SettingsFragment.class.getName());
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, settingsFragment, SettingsFragment.class.getName())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .addToBackStack(null)
                            .commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                return true;
        }
    }

    /**
     * Close drawer on back key-press
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        super.onBackPressed();
    }
}
