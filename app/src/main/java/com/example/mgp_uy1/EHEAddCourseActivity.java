package com.example.mgp_uy1;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;
import static android.widget.LinearLayout.HORIZONTAL;

public class EHEAddCourseActivity extends AppCompatActivity {

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_my_courses:
//                    finish();
//                    return true;
//                case R.id.navigation_my_gpa:
//
//                    return true;
//            }
//            return false;
//        }
//    };

    private LinearLayout weightLayout;
    private ArrayList<EditText> weightNameViews = new ArrayList<>();
    private ArrayList<EditText> weightPercentViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kim_add_course);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Realm realm = Realm.getDefaultInstance();

        weightLayout = findViewById(R.id.kim_add_class_weight);
        final TextView nameView = findViewById(R.id.kim_add_class_class_name);
        final Spinner creditView = findViewById(R.id.kim_add_course_credits_dropdown);
        creditView.setSelection(0);

        final Spinner gradeSystemView = findViewById(R.id.kim_add_course_grading_system_dropdown);
        gradeSystemView.setSelection(0);

        EHECourse course = null;

        final String method;
        if (getIntent().getExtras() != null) {
            method = getIntent().getExtras().getString("method");
        } else {
            method = "add";
        }

        final long courseId = getIntent().getExtras().getLong("course");

        if (method.equals("edit")) {
            setTitle("Edit Course");

            RealmResults<EHECourse> courses = realm.where(EHECourse.class).equalTo("id", courseId).findAll();
            RealmResults<EHEWeight> weights = realm.where(EHEWeight.class).equalTo("course.id", courseId).findAll();

            course = courses.get(0);

            if (course != null) {
                nameView.setText(course.name);
                creditView.setSelection(course.credit - 1);
                gradeSystemView.setSelection(course.su ? 1 : 0);

                for (EHEWeight o : weights) {
                    createWeightEditText(o);
                }
            }

        } else {
            setTitle("Add Course");
        }

        Button saveButton = findViewById(R.id.kim_add_course_save_button);

        final EHECourse finalCourse = course;
        final AppCompatActivity finalActivity = this;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EHECourse eheCourse = null;

                Realm realm = Realm.getDefaultInstance();

                if (TextUtils.getTrimmedLength(nameView.getText()) == 0) {
                    Toast.makeText(finalActivity, "You have not entered course name.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (weightNameViews.size() == 0) {
                    Toast.makeText(finalActivity, "You have not entered any weights.", Toast.LENGTH_LONG).show();
                    return;
                }

                double weightSum = 0.0;

                for (int i = 0; i < weightNameViews.size(); i++) {
                    if (TextUtils.getTrimmedLength(weightNameViews.get(i).getText()) == 0) {
                        Toast.makeText(finalActivity, "You have not entered weight name.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (TextUtils.getTrimmedLength(weightPercentViews.get(i).getText()) == 0) {
                        Toast.makeText(finalActivity, "You have not entered weight percent.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    weightSum += Double.valueOf(String.valueOf(weightPercentViews.get(i).getText()));
                }

                if (weightSum > 100 || weightSum < 0) {
                    Toast.makeText(finalActivity, "Weight is over 100 or less than 0.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (weightSum < 100) {
                    Toast.makeText(finalActivity, "Sum of all weights must be 100.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.getTrimmedLength(nameView.getText()) == 0) {
                    Toast.makeText(finalActivity, "You have not entered weight name.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (method.equals("edit")) {
                    if (finalCourse != null) {
                        realm.beginTransaction();

                        finalCourse.credit = Integer.valueOf(String.valueOf(creditView.getSelectedItem()));
                        finalCourse.name = String.valueOf(nameView.getText());
                        finalCourse.su = gradeSystemView.getSelectedItemPosition() != 0;
                        eheCourse = finalCourse;

                        realm.copyToRealmOrUpdate(finalCourse);
                        realm.commitTransaction();
                    }
                } else {
                    realm.beginTransaction();

                    eheCourse = new EHECourse();
                    Number currentIdNum = realm.where(EHECourse.class).max("id");
                    long nextId;
                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    eheCourse.id = nextId;
                    eheCourse.name = String.valueOf(nameView.getText());
                    eheCourse.grade = "0";
                    eheCourse.credit = Integer.valueOf(String.valueOf(creditView.getSelectedItem()));
                    eheCourse.su = gradeSystemView.getSelectedItemPosition() != 0;
                    realm.copyToRealmOrUpdate(eheCourse);
                    realm.commitTransaction();
                }

                realm.beginTransaction();
                final RealmResults<EHEWeight> results = realm.where(EHEWeight.class).equalTo("course.id", courseId).findAll();
                results.deleteAllFromRealm();
                realm.commitTransaction();

                for (int i = 0; i < weightNameViews.size(); i++) {
                    realm.beginTransaction();

                    EditText weightPercentView = weightPercentViews.get(i);
                    EHEWeight weight = new EHEWeight();
                    Number currentIdNum = realm.where(EHEWeight.class).max("id");

                    long nextId;

                    if (currentIdNum == null) {
                        nextId = 1;
                    } else {
                        nextId = currentIdNum.intValue() + 1;
                    }
                    weight.id = nextId;
                    weight.name = String.valueOf(weightNameViews.get(i).getText());
                    weight.percent = Double.valueOf(String.valueOf(weightPercentView.getText()));
                    weight.course = eheCourse;
                    weightSum += weight.percent;

                    realm.copyToRealmOrUpdate(weight);
                    realm.commitTransaction();
                }

                EHEPushNotification.sendPush(EHEAddCourseActivity.this);

                setResult(RESULT_OK);
                finish();
            }
        });

        final Button weightButton = findViewById(R.id.kim_add_course_add_weight_button);
        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createWeightEditText(null);
            }
        });
    }

    private void createWeightEditText(EHEWeight o) {
        double weightTotal = 0.0;
        for (int i = 0; i < weightPercentViews.size(); i++ ) {
            if (TextUtils.getTrimmedLength(weightPercentViews.get(i).getText()) == 0) {
                Toast.makeText(this, "Weight percent is empty.", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.getTrimmedLength(weightNameViews.get(i).getText()) == 0) {
                Toast.makeText(this, "Weight name are empty.", Toast.LENGTH_LONG).show();
                return;
            }
            weightTotal += Double.valueOf(String.valueOf(weightPercentViews.get(i).getText()));
        }

        if (weightTotal >= 100) {
            Toast.makeText(this, "Weight is already equal to and over 100.", Toast.LENGTH_LONG).show();
            return;
        }

        LinearLayout textViewLayout = new LinearLayout(EHEAddCourseActivity.this);
        textViewLayout.setOrientation(HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewLayout.setLayoutParams(layoutParams);

        EditText weightNameView = (EditText) getLayoutInflater().inflate(R.layout.kim_edit_text, null);
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                Resources.getSystem().getDisplayMetrics().widthPixels / 2, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textViewParams.setMargins(0, 50, 0, 50);
        if (o != null) {
            weightNameView.setText(o.name);
        }
        weightNameView.setLayoutParams(textViewParams);
        weightNameView.setHint("Weight Name");

        weightNameViews.add(weightNameView);

        EditText weightPercentView = (EditText) getLayoutInflater().inflate(R.layout.kim_edit_text, null);
        LinearLayout.LayoutParams textViewParams2 = new LinearLayout.LayoutParams(
                Resources.getSystem().getDisplayMetrics().widthPixels / 2, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textViewParams2.setMargins(0, 50, 50, 50);
        weightPercentView.setLayoutParams(textViewParams2);
        weightPercentView.setHint(String.valueOf(100 - weightTotal));
        weightPercentView.setInputType(InputType.TYPE_CLASS_NUMBER);

        if (o != null) {
            weightPercentView.setText(String.valueOf(o.percent));
        }

        weightPercentViews.add(weightPercentView);

        textViewLayout.addView(weightNameView);
        textViewLayout.addView(weightPercentView);

        weightLayout.addView(textViewLayout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
