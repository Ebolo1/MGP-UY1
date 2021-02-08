package com.example.mgp_uy1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class EHEMyCoursesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EHEMyCourseAdapter myCourseAdapter;
    private int kim_add_course_request_code = 1;
    private RealmResults<EHEAssessment> assessments;
    private TextView gpaView;
    private TextView expectedGPAView;
    private FloatingActionButton fab;
    private RealmResults<EHECourse> courses;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentKimMyCourses = inflater.inflate(R.layout.fragment_kim_my_courses, container, false);

        fab = fragmentKimMyCourses.findViewById(R.id.kim_add_course_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), EHEAddCourseActivity.class);
                myIntent.putExtra("method", "add");
                startActivityForResult(myIntent, kim_add_course_request_code);
            }
        });

        recyclerView = fragmentKimMyCourses.findViewById(R.id.kim_my_course_list_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        Realm realm = Realm.getDefaultInstance();
        courses = realm.where(EHECourse.class).findAll();

        gpaView = fragmentKimMyCourses.findViewById(R.id.kim_my_course_gpa_view);
        expectedGPAView = fragmentKimMyCourses.findViewById(R.id.kim_my_course_expected_gpa_view);
        gpaView.setText(String.format("GPA %.2f", EHECalculateGrade.calculateOverallGPA(new ArrayList<>(courses))));
        expectedGPAView.setText(String.format("Expected GPA %.2f", EHECalculateGrade.calculateOverallExpectedGPA(new ArrayList<>(courses))));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showExpectedGPA = preferences.getBoolean("show_expected_gpa", true);
        if (showExpectedGPA) {
            expectedGPAView.setVisibility(View.VISIBLE);
        } else {
            expectedGPAView.setVisibility(View.INVISIBLE);
        }

        SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("SharedPreferences", key);
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean showExpected = preferences.getBoolean("show_expected_gpa", true);
                if (showExpected) {
                    expectedGPAView.setVisibility(View.VISIBLE);
                } else {
                    expectedGPAView.setVisibility(View.INVISIBLE);
                }

                Realm realm = Realm.getDefaultInstance();
                RealmResults<EHECourse> courses = realm.where(EHECourse.class).findAll();

                myCourseAdapter.updateAdapter(new ArrayList<>(courses));
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

        realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm realm) {
                gpaView.setText(String.format("GPA %.2f", EHECalculateGrade.calculateOverallGPA(new ArrayList<>(courses))));
                expectedGPAView.setText(String.format("Expected GPA %.2f", EHECalculateGrade.calculateOverallExpectedGPA(new ArrayList<>(courses))));

                RealmResults<EHECourse> courses = realm.where(EHECourse.class).findAll();
                myCourseAdapter.updateAdapter(new ArrayList<>(courses));
            }
        });

        assessments = realm.where(EHEAssessment.class).findAll();
        assessments.addChangeListener(new RealmChangeListener<RealmResults<EHEAssessment>>() {
            @Override
            public void onChange(RealmResults<EHEAssessment> kimAssessments) {
                gpaView.setText(String.format("GPA %.2f", EHECalculateGrade.calculateOverallGPA(new ArrayList<>(courses))));
                expectedGPAView.setText(String.format("Expected GPA %.2f", EHECalculateGrade.calculateOverallExpectedGPA(new ArrayList<>(courses))));

                Realm realm = Realm.getDefaultInstance();
                RealmResults<EHECourse> courses = realm.where(EHECourse.class).findAll();

                myCourseAdapter.updateAdapter(new ArrayList<>(courses));
            }
        });

        myCourseAdapter = new EHEMyCourseAdapter(this.getActivity(), new ArrayList<>(courses), recyclerView);
        recyclerView.setAdapter(myCourseAdapter);

        return fragmentKimMyCourses;
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showExpected = preferences.getBoolean("show_expected_gpa", true);
        if (showExpected) {
            expectedGPAView.setVisibility(View.VISIBLE);
        } else {
            expectedGPAView.setVisibility(View.INVISIBLE);
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<EHECourse> courses = realm.where(EHECourse.class).findAll();

        myCourseAdapter.updateAdapter(new ArrayList<>(courses));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == kim_add_course_request_code) {
//            if (resultCode == RESULT_OK) {
//                Log.d("MY COURSES", "UPDATING COURSES");
//
//                Realm realm = Realm.getDefaultInstance();
//                RealmResults<KimCourse> courses = realm.where(KimCourse.class).findAll();
//
//                myCourseAdapter.updateAdapter(new ArrayList<>(courses));
//            }
//        }
    }
}
