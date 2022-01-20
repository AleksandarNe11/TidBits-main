package com.example.tidbits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.tidbits.firebase.ChildUser;

import java.util.ArrayList;

public class ToolbarNoNavigation extends AppCompatActivity {
    private Toolbar toolbar;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar_no_navigation);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // find out where it is called from
        Bundle bundle = getIntent().getExtras();


        if (bundle.getString("callingClass").equals(UserSwitcherActivity.class.getName())) {
            YourKidsFragment yourKidsFragment = new YourKidsFragment();
            yourKidsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, yourKidsFragment, YourKidsFragment.class.getName()).commit();
        }
        else if (bundle.getString("callingClass").equals(SelectSubjectFragment.class.getName())) {
            SubjectFragment subjectFragment = new SubjectFragment();
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putString("subject", bundle.getString("subject"));
            subjectFragment.setArguments(fragmentBundle);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.nav_host_fragment, subjectFragment, SubjectFragment.class.getName()).commit();
        }
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            // check which fragment
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(YourKidsFragment.class.getName());
            if (currentFragment != null && currentFragment.isVisible()) {
                finish();
                return true;
            }
            else if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, YourKidsFragment.class.getName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                return true;
            }
            currentFragment = getSupportFragmentManager().findFragmentByTag(SubjectFragment.class.getName());
            if (currentFragment != null && currentFragment.isVisible()) {
                finish();
                return true;
            }
            else if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, SubjectFragment.class.getName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                return true;
            }
            currentFragment = getSupportFragmentManager().findFragmentByTag(QuestionFragment.class.getName());
            if (currentFragment != null && currentFragment.isVisible()) {
                finish();
                return true;
            }
            else if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, currentFragment, QuestionFragment.class.getName())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .commit();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(YourKidsFragment.class.getName());
        // need to check which fragment is inflated. the SubjectFragment could be inflated instead
        Fragment subjectFragment = getSupportFragmentManager().findFragmentByTag(SubjectFragment.class.getName());
        if (currentFragment != null && currentFragment.isVisible())
            finish();
        else if (subjectFragment != null && subjectFragment.isVisible())
            finish();
        else
            super.onBackPressed();
    }
}