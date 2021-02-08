package com.example.mgp_uy1;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EHECourse extends RealmObject {
    @PrimaryKey
    public Long id;
    public String name;
    public String grade;

    public int credit;
    public boolean su;
}
