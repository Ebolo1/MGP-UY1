package com.example.mgp_uy1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class EHEMyCourseAdapter extends RecyclerView.Adapter<EHEMyCourseAdapter.CourseViewHolder> {

    private Context context;
    private ArrayList<EHECourse> courses;
    private int kim_add_course_request_code = 1;

    public EHEMyCourseAdapter(Context context, ArrayList<EHECourse> courses, RecyclerView recyclerView) {
        this.context = context;
        this.courses = courses;
        this.recyclerView = recyclerView;
    }

    private RecyclerView recyclerView;

    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        final View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kim_my_course, viewGroup, false);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, EHEMyAssessmentsActivity.class);
                myIntent.putExtra("course", courses.get(recyclerView.getChildAdapterPosition(v)).id);
                context.startActivity(myIntent);
            }
        });
        mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final CharSequence[] items = {"Edit", "Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Select");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
                                Intent editIntent = new Intent(context, EHEAddCourseActivity.class);
                                editIntent.putExtra("method", "edit");
                                editIntent.putExtra("course", courses.get(recyclerView.getChildAdapterPosition(v)).id);
                                ((FragmentActivity) context).startActivityForResult(editIntent, kim_add_course_request_code);
                                break;
                            case 1:
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();

                                final RealmResults<EHECourse> results = realm.where(EHECourse.class).equalTo("id", courses.get(recyclerView.getChildAdapterPosition(v)).id).findAll();
                                results.deleteAllFromRealm();

                                realm.commitTransaction();

                                EHEPushNotification.sendPush(context);

//                                courses.remove(recyclerView.getChildAdapterPosition(v));
//                                notifyDataSetChanged();

                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        return new CourseViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CourseViewHolder courseViewHolder, final int i) {
        courseViewHolder.nameView.setText(courses.get(i).name);

        if (courses.get(i).su) {
            courseViewHolder.gradeView.setText(String.format("S/U Grade %s", EHECalculateGrade.calculateSUGrade(EHECalculateGrade.calculateCourseGPA(courses.get(i), true))));
        } else {
            courseViewHolder.gradeView.setText(String.format("Letter Grade %s", EHECalculateGrade.calculateCourseLetterGrade(EHECalculateGrade.calculateCourseGPA(courses.get(i), true))));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showCredits = preferences.getBoolean("show_credits", true);
        if (showCredits) {
            courseViewHolder.creditView.setVisibility(View.VISIBLE);
        } else {
            courseViewHolder.creditView.setVisibility(View.INVISIBLE);
        }

        courseViewHolder.creditView.setText(String.valueOf(courses.get(i).credit));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateAdapter(ArrayList<EHECourse> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView gradeView;
        public TextView creditView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.kim_my_course_name_view);
            gradeView = itemView.findViewById(R.id.kim_my_course_grade_view);
            creditView = itemView.findViewById(R.id.kim_my_course_credit_view);
        }

    }

}
