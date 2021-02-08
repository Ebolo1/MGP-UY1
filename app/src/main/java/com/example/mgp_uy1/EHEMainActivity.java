package com.example.mgp_uy1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EHEMainActivity extends AppCompatActivity {

    private BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = new EHEMyCoursesFragment();

            switch (item.getItemId()) {
                case R.id.navigation_my_courses:
                    setTitle(R.string.title_activity_kim_my_courses);
                    fragment = new EHEMyCoursesFragment();

                    break;
                case R.id.navigation_my_gpa:
                    setTitle(R.string.title_activity_kim_my_gpa);
                    fragment = new EHEMyGPAFragment();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.commit();

            item.setChecked(true);

            return false;
        }
    };

    private int kim_settings_request_code = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kim_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Settings").setIcon(R.drawable.settings)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent myIntent = new Intent(EHEMainActivity.this, EHESettingsActivity.class);
                startActivityForResult(myIntent, kim_settings_request_code);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("RESULT : ", String.valueOf(requestCode));
        Log.d("RESULT : ", String.valueOf(resultCode));

        if (requestCode == kim_settings_request_code) {
            if (resultCode == RESULT_CANCELED) {
//                setTitle(R.string.title_activity_kim_my_courses);
//                Fragment fragment = new KimMyCoursesFragment();
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.main_fragment, fragment);
//                transaction.addToBackStack(null);
//                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                transaction.commit();
            }
        }
    }
}
