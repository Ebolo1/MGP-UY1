package com.example.mgp_uy1;

import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class EHECalculateGrade {
    public static double calculateOverallGPA(ArrayList<EHECourse> courses) {
        double grade = 0.0;
        double totalCredit = 0.0;
        for (EHECourse course : courses) {
            if (!course.su) {
                totalCredit += course.credit;
            }
        }

        for (EHECourse course : courses) {
            if (!course.su) {
                grade += calculateGPAFromGrade(calculateCourseGPA(course, false)) * course.credit / totalCredit;
                Log.d("GPA", String.valueOf(grade));
            }
        }

        return grade;
    }

    public static String calculateSUGrade(double grade) {
        String su = "U";
        if (grade >= 69.5) {
            su = "S";
        }
        return su;
    }

    public static double calculateOverallExpectedGPA(ArrayList<EHECourse> courses) {
        double grade = 0.0;
        double totalCredit = 0.0;
        for (EHECourse course : courses) {
            if (!course.su) {
                totalCredit += course.credit;
            }
        }

        for (EHECourse course : courses) {
            if (!course.su) {
                grade += calculateGPAFromGrade(calculateCourseGPA(course, true)) * course.credit / totalCredit;
                Log.d("GPA", String.valueOf(grade));
            }
        }

        return grade;
    }

    public static double calculateCourseGPA(EHECourse course, boolean expected) {
        double grade = 0.0;

        Realm realm = Realm.getDefaultInstance();

        RealmResults<EHEWeight> weights = realm.where(EHEWeight.class).equalTo("course.id", course.id).findAll();
        RealmResults<EHEAssessment> assessments = realm.where(EHEAssessment.class).equalTo("course.id", course.id).findAll();

        for (int i = 0; i < weights.size(); i++) {
            double tempGrade = 0.0;

            EHEWeight weight = weights.get(i);

            int assessmentWeightTotal = 0;

            for (EHEAssessment a : assessments) {
                if (!expected) {
                    if (!a.expected) {
                        assessmentWeightTotal += a.assessmentWeight;
                    }
                } else {
                    assessmentWeightTotal += a.assessmentWeight;
                }
            }

            for (EHEAssessment a : assessments) {
                if (!expected) {
                    if (!a.expected) {
                        tempGrade += a.grade * a.assessmentWeight / assessmentWeightTotal;
                    }
                } else {
                    tempGrade += a.grade * a.assessmentWeight / assessmentWeightTotal;
                }
            }

            Log.d("GRADE CALCULATION", String.valueOf(tempGrade));
            grade += tempGrade * weight.percent / 100;
        }

        double outOf = 0.0;

        for (EHEWeight weight : weights) {
            outOf += weight.percent;
        }

        Log.d("GRADE CALCULATION", String.valueOf(grade));

        grade = grade / outOf * 100;

        Log.d("GRADE CALCULATION", String.valueOf(grade));

        return grade;
    }

    public static Double calculateGPAFromGrade(double grade) {
        double gpa;

        if (grade >= 80) {
            gpa = 4.0;
        } else if (grade <= 100 && grade >= 80) {
            gpa = 4.0;
        } else if (grade < 80 && grade >= 75) {
            gpa = 3.7;
        } else if (grade < 75 && grade >= 70) {
            gpa = 3.3;
        } else if (grade < 70 && grade >= 65) {
            gpa = 3.0;
        } else if (grade < 65 && grade >= 60) {
            gpa = 2.7;
        } else if (grade < 60 && grade >= 55) {
            gpa = 2.3;
        } else if (grade < 55 && grade >= 50) {
            gpa = 2.0;
        } else if (grade < 50 && grade >= 45) {
            gpa = 1.7;
        } else if (grade < 45 && grade >= 40) {
            gpa = 1.3;
        } else if (grade < 40 && grade >= 35) {
            gpa = 1.0;
        } else {
            gpa = 0.0;
        }

        return gpa;
    }

    public static String calculateCourseLetterGrade(double grade) {
        String letterGrade;

        if (grade >= 97.5) {
            letterGrade = "A+";
        } else if (grade <= 97.5 && grade >= 92.5) {
            letterGrade = "A";
        } else if (grade <= 92.5 && grade >= 89.5) {
            letterGrade = "A-";
        } else if (grade <= 89.5 && grade >= 87.5) {
            letterGrade = "B+";
        } else if (grade <= 87.5 && grade >= 82.5) {
            letterGrade = "B";
        } else if (grade <= 82.5 && grade >= 79.5) {
            letterGrade = "B-";
        } else if (grade <= 79.5 && grade >= 77.5) {
            letterGrade = "C+";
        } else if (grade <= 77.5 && grade >= 72.5) {
            letterGrade = "C";
        } else if (grade <= 72.5 && grade >= 69.5) {
            letterGrade = "C-";
        } else if (grade <= 69.5 && grade >= 59.5){
            letterGrade = "D";
        } else if (grade <= 59.5 && grade >= 0) {
            letterGrade = "F";
        } else {
            letterGrade = "F";
        }

        return letterGrade;
    }

}
