package com.example.mgp_uy1;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EHEAssessment extends RealmObject {

    @PrimaryKey
    public Long id;
    public EHECourse course;
    public String name;
    public boolean expected;
    public EHEWeight weight;
    public Double grade;
    public Double assessmentWeight;
}
