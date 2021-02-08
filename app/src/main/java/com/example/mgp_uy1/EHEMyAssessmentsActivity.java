package com.example.mgp_uy1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class EHEMyAssessmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EHEMyAssessmentAdapter eheMyAssessmentAdapter;

    private int kim_add_assessment_request_code = 2;

    private ArrayList<EHEAssessment> assessments = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_kim_my_assessments);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = findViewById(R.id.kim_add_assessment_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(EHEMyAssessmentsActivity.this, EHEAddAssessmentActivity.class);
                myIntent.putExtra("method", "add");
                myIntent.putExtra("course", getIntent().getExtras().getLong("course"));
                startActivityForResult(myIntent, kim_add_assessment_request_code);
            }
        });

        recyclerView = findViewById(R.id.kim_my_assessment_list_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Realm realm = Realm.getDefaultInstance();
        RealmResults<EHEAssessment> assessments = realm.where(EHEAssessment.class).equalTo("course.id", getIntent().getExtras().getLong("course")).findAll();

        this.assessments.addAll(assessments);

        eheMyAssessmentAdapter = new EHEMyAssessmentAdapter(this, getIntent().getExtras().getLong("course"), this.assessments, recyclerView);
        recyclerView.setAdapter(eheMyAssessmentAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == kim_add_assessment_request_code) {
            if (resultCode == RESULT_OK) {
                this.assessments.clear();

                Realm realm = Realm.getDefaultInstance();
                RealmResults<EHEAssessment> assessments = realm.where(EHEAssessment.class).equalTo("course.id", getIntent().getExtras().getLong("course")).findAll();

                this.assessments.addAll(assessments);

                eheMyAssessmentAdapter.updateAdapter(this.assessments);
            }
        }
    }
}
