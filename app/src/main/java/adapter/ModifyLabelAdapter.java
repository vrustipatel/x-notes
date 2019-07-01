package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import e.wolfsoft1.Xnotes.LabelCreation;
import e.wolfsoft1.Xnotes.MainActivity;
import e.wolfsoft1.Xnotes.R;
import e.wolfsoft1.Xnotes.TrashActivity;
import model.ModifyLabelModeL;

public class ModifyLabelAdapter extends RecyclerView.Adapter<ModifyLabelAdapter.ViewHolder> {


    Context context;
    ArrayList<ModifyLabelModeL> editLabelModeLS;
    Boolean isNavDrawer;
    Boolean isEnable;

    FirebaseAuth fAuth;
    String data;
    private DatabaseReference fNoteDatabaseRef, labelDBreference;
    Intent intent;
    private String text;

    public ModifyLabelAdapter(Context context, ArrayList<ModifyLabelModeL> editLabelModeLS, Boolean isEnable) {

        this.context = context;
        this.editLabelModeLS = editLabelModeLS;
        this.isEnable = isEnable;
    }

    @NonNull
    @Override
    public ModifyLabelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_edit_labels, viewGroup, false);

        return new ModifyLabelAdapter.ViewHolder(view, i);

    }

    @Override
    public void onBindViewHolder(@NonNull final ModifyLabelAdapter.ViewHolder viewHolder, final int i) {

        //firebase tables
        fAuth = FirebaseAuth.getInstance();
        fNoteDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Labels").child(fAuth.getCurrentUser().getUid());
        labelDBreference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        viewHolder.delete.setVisibility(View.GONE);

        //get variables on position from model class

        final ModifyLabelModeL editLabelModeL = editLabelModeLS.get(i);
        final String key = editLabelModeL.getKey();

        viewHolder.labelCreated.setText(editLabelModeLS.get(i).getLabelCreated());

        /*---------------show content in navigation drawer------------------*/


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity){
                    final String tag = editLabelModeL.getLabelCreated();
                    ((MainActivity) context).releseItem();
//                    intent = new Intent(context, MainActivity.class);
//                    intent.putExtra("labelTag", tag);
//                    intent.putExtra("key", key);
//                    intent.putExtra("labelkey", key);
//                    context.startActivity(intent);
                    ((MainActivity) context).updateSomething(tag,key);
                }
                else {

                }


            }
        });

        if (context instanceof MainActivity) {

            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.tagLabel.setImageResource(R.drawable.ic_tag);
            viewHolder.labelCreated.setEnabled(false);
            viewHolder.done.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.line1.setVisibility(View.GONE);
            viewHolder.line2.setVisibility(View.GONE);
            viewHolder.labelCreated.setSingleLine(true);
            viewHolder.labelCreated.setMaxLines(1);
            viewHolder.labelCreated.setKeyListener(null);

//            if(editLabelModeL.getLabelCreated().length()>2){
//
////                text  =  text.substring(0,4)+"...";
//
//                viewHolder.labelCreated.setText(text);
//            }else{
//
//                viewHolder.labelCreated.setText(text);
//
//            }
            LinearLayout.LayoutParams paramscontent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            paramscontent.setMargins(10, 0, 20, 0);

            viewHolder.labelCreated.setLayoutParams(paramscontent);


        } else {

            //display content in label creation activity


            viewHolder.labelCreated.setSingleLine(false);
            if (isEnable) {

                viewHolder.line1.setVisibility(View.GONE);
                viewHolder.line2.setVisibility(View.GONE);
                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.done.setVisibility(View.GONE);
                viewHolder.edit.setVisibility(View.VISIBLE);
                viewHolder.tagLabel.setVisibility(View.VISIBLE);

                viewHolder.labelCreated.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        viewHolder.delete.setVisibility(View.VISIBLE);
                        viewHolder.done.setVisibility(View.VISIBLE);
                        viewHolder.tagLabel.setVisibility(View.GONE);
                        viewHolder.edit.setVisibility(View.GONE);
                        viewHolder.line1.setVisibility(View.VISIBLE);
                        viewHolder.line2.setVisibility(View.VISIBLE);

                        data = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                viewHolder.labelLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        viewHolder.delete.setVisibility(View.VISIBLE);
                        viewHolder.done.setVisibility(View.VISIBLE);
                        viewHolder.tagLabel.setVisibility(View.GONE);
                        viewHolder.edit.setVisibility(View.GONE);
                        viewHolder.line1.setVisibility(View.VISIBLE);
                        viewHolder.line2.setVisibility(View.VISIBLE);

                    }
                });

                //delete label
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editLabelModeLS.remove(i);
                        notifyItemRemoved(i);
                        notifyItemChanged(i, editLabelModeLS.size());

                        labelDBreference.orderByChild("labelKey").equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    String labelKey = (String) snapshot.child("key").getValue();
                                    labelDBreference.child(labelKey).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        fNoteDatabaseRef.child(editLabelModeL.getKey()).removeValue();
                    }
                });

                //update a label
                viewHolder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.line1.setVisibility(View.GONE);
                        viewHolder.line2.setVisibility(View.GONE);
                        viewHolder.tagLabel.setVisibility(View.VISIBLE);
                        viewHolder.edit.setVisibility(View.VISIBLE);

                        fNoteDatabaseRef.orderByChild("labelCreated").equalTo(data).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {

                                    fNoteDatabaseRef.child(key).child("labelCreated").setValue(data);

                                } else {
                                    Toast.makeText(context, "label is already defined", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        labelDBreference.orderByChild("labelKey").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    String mainKey = (String) snapshot.child("key").getValue();
                                    labelDBreference.child(mainKey).child("labelTag").setValue(data);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            } else {

                viewHolder.delete.setVisibility(View.GONE);
                viewHolder.done.setVisibility(View.GONE);
                viewHolder.tagLabel.setVisibility(View.VISIBLE);
                viewHolder.edit.setVisibility(View.VISIBLE);
                viewHolder.line1.setVisibility(View.GONE);
                viewHolder.line2.setVisibility(View.GONE);

            }
        }

    }

//    private void deleteNote(String key) {
//        fNoteDatabaseRef.child(key).removeValue();
//    }

    @Override
    public int getItemCount() {
        return editLabelModeLS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText labelCreated;
        LinearLayout labelLayout;
        View line1, line2;
        ImageView tagLabel, edit, done, delete;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            labelCreated = itemView.findViewById(R.id.label_created);
            edit = itemView.findViewById(R.id.label_edit);
            tagLabel = itemView.findViewById(R.id.tag_label);
            labelLayout = itemView.findViewById(R.id.label_layout);
            line1 = itemView.findViewById(R.id.viewline1);
            line2 = itemView.findViewById(R.id.viewline2);
            done = itemView.findViewById(R.id.doneitem);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}