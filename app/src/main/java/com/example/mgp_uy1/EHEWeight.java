package com.example.mgp_uy1;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EHEWeight extends RealmObject {
    @PrimaryKey
    public Long id;
    public String name;
    public Double percent;
    public EHECourse course;
}
