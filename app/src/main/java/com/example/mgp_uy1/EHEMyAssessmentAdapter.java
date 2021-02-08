package com.example.mgp_uy1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;

public class EHEMyAssessmentAdapter extends RecyclerView.Adapter<EHEMyAssessmentAdapter.EHEMyAssessmentViewHolder> {

    private final long courseId;
    private Context context;
    private ArrayList<EHEAssessment> assessments;
    private int kim_add_assessment_request_code = 2;

    public EHEMyAssessmentAdapter(Context context, long courseId, ArrayList<EHEAssessment> assessments, RecyclerView recyclerView) {
        this.context = context;
        this.assessments = assessments;
        this.courseId = courseId;
        this.recyclerView = recyclerView;
    }

    private RecyclerView recyclerView;

    @Override
    public EHEMyAssessmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_kim_my_assessment, viewGroup, false);
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
                                Intent editIntent = new Intent(context, EHEAddAssessmentActivity.class);
                                editIntent.putExtra("method", "edit");
                                editIntent.putExtra("assessment", assessments.get(recyclerView.getChildAdapterPosition(v)).id);
                                editIntent.putExtra("course", courseId);
                                ((AppCompatActivity)context).startActivityForResult(editIntent, kim_add_assessment_request_code);
                                break;
                            case 1:
                                Realm realm = Realm.getDefaultInstance();
                                realm.beginTransaction();

                                final RealmResults<EHEAssessment> results = realm.where(EHEAssessment.class).equalTo("id", assessments.get(recyclerView.getChildAdapterPosition(v)).id).findAll();
                                results.deleteAllFromRealm();

                                realm.commitTransaction();
                                assessments.remove(recyclerView.getChildAdapterPosition(v));
                                notifyDataSetChanged();

                                EHEPushNotification.sendPush(context);

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
        return new EHEMyAssessmentViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull EHEMyAssessmentViewHolder kimMyAssessmentViewHolder, int i) {
        kimMyAssessmentViewHolder.nameView.setText(this.assessments.get(i).name);
        kimMyAssessmentViewHolder.gradeView.setText(EHECalculateGrade.calculateCourseLetterGrade(this.assessments.get(i).grade));
    }

    @Override
    public int getItemCount() {
        return assessments.size();
    }

    public static class EHEMyAssessmentViewHolder extends RecyclerView.ViewHolder {
        public TextView nameView;
        public TextView gradeView;

        public EHEMyAssessmentViewHolder(@NonNull View itemView) {
            super(itemView);

            nameView = itemView.findViewById(R.id.kim_my_assessment_name_view);
            gradeView = itemView.findViewById(R.id.kim_my_assessment_grade_view);
        }
    }

    public void updateAdapter(ArrayList<EHEAssessment> assessments){
        this.assessments = assessments;
        notifyDataSetChanged();
    }
}
