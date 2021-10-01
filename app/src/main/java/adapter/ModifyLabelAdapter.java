package adapter;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import e.vp.Xnotes.MainActivity;
import e.vp.Xnotes.R;
import model.ModifyLabelModeL;

public class ModifyLabelAdapter extends RecyclerView.Adapter<ModifyLabelAdapter.ViewHolder> {


    Context context;
    ArrayList<ModifyLabelModeL> editLabelModeLS;
    Boolean isEnable;
    FirebaseAuth fAuth;
    String data;
    private DatabaseReference fNoteLabelsDBref, fNotesDBref;
    Intent intent;

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
        fNoteLabelsDBref = FirebaseDatabase.getInstance().getReference().child("Labels").child(fAuth.getCurrentUser().getUid());
        fNotesDBref = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        viewHolder.delete.setVisibility(View.GONE);

        //get variables on position from model class

        final ModifyLabelModeL editLabelModeL = editLabelModeLS.get(i);
        final String key = editLabelModeL.getKey();
        final String tag = editLabelModeL.getLabelCreated();

        viewHolder.labelCreated.setText(editLabelModeLS.get(i).getLabelCreated());

        /*---------------show content in navigation drawer------------------*/


        if (context instanceof MainActivity) {

            viewHolder.labelText.setVisibility(View.VISIBLE);
            viewHolder.labelCreated.setVisibility(View.GONE);
            viewHolder.edit.setVisibility(View.GONE);
            viewHolder.tagLabel.setImageResource(R.drawable.ic_tag);
            viewHolder.done.setVisibility(View.GONE);
            viewHolder.delete.setVisibility(View.GONE);
            viewHolder.line1.setVisibility(View.GONE);
            viewHolder.line2.setVisibility(View.GONE);
            viewHolder.labelText.setText(editLabelModeLS.get(i).getLabelCreated());
            viewHolder.labelText.setSingleLine(true);
            viewHolder.labelText.setMaxLines(1);

            ((MainActivity) context).releseItem();

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent = new Intent(context, MainActivity.class);

                    intent.putExtra("labelTag", tag);
                    intent.putExtra("key", key);
                    intent.putExtra("labelkey", key);

//                    set the new task and clear flags
                    context.startActivity(intent);
                    ((MainActivity) context).closeDrawer();

                }
            });

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

                        if (editLabelModeL.getLabelCreated().length() > 70) {
                            viewHolder.labelCreated.setError("Label should be between 1 and 70 characters in length");
                            viewHolder.labelCreated.requestFocus();
                        } else {
                            data = s.toString();
                        }

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

                        resetdata(viewHolder);

                        editLabelModeLS.remove(i);
                        notifyItemRemoved(i);
                        notifyItemChanged(i, editLabelModeLS.size());

                        fNotesDBref.orderByChild("labelKey").equalTo(key).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    String labelkey = (String) snapshot.child("key").getValue();

                                    String title = (String) snapshot.child("title").getValue();
                                    String content = (String) snapshot.child("content").getValue();
                                    String date = (String) snapshot.child("date").getValue();
                                    String labelKey = (String) snapshot.child("labelKey").getValue();
                                    String labelTag = (String) snapshot.child("labelTag").getValue();
                                    String selectColorImg = (String) snapshot.child("selectColorImg").getValue();
                                    String selectinput = (String) snapshot.child("selectinput").getValue();
                                    boolean selected = (boolean) snapshot.child("selected").getValue();
                                    long timeLengthMinutes = (long) snapshot.child("timeLengthMinutes").getValue();
                                    long timeLengthSeconds = (long) snapshot.child("timeLengthSeconds").getValue();

                                    DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(fAuth.getCurrentUser().getUid()).child(labelKey);

                                    trashReference.child("key").setValue(key);
                                    trashReference.child("title").setValue(title);
                                    trashReference.child("date").setValue(date);
                                    trashReference.child("labelKey").setValue(labelKey);
                                    trashReference.child("labelTag").setValue(labelTag);
                                    trashReference.child("selectColorImg").setValue(selectColorImg);
                                    trashReference.child("content").setValue(content);
                                    trashReference.child("selectinput").setValue(selectinput);

                                    trashReference.child("selected").setValue(selected);
                                    trashReference.child("timeLengthMinutes").setValue(timeLengthMinutes);
                                    trashReference.child("timeLengthSeconds").setValue(timeLengthSeconds);

                                    fNotesDBref.child(labelkey).removeValue();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        fNoteLabelsDBref.child(editLabelModeL.getKey()).removeValue();
                    }
                });

                //update a label
                viewHolder.done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resetdata(viewHolder);
                        fNoteLabelsDBref.orderByChild("labelCreated").equalTo(data).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (!dataSnapshot.exists()) {

                                    fNoteLabelsDBref.child(key).child("labelCreated").setValue(data);

                                } else {
                                    Toast.makeText(context, "label is already defined", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        fNotesDBref.orderByChild("labelKey").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    String mainKey = (String) snapshot.child("key").getValue();
                                    fNotesDBref.child(mainKey).child("labelTag").setValue(data);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

            } else {
                resetdata(viewHolder);
            }
        }

    }

    private void resetdata(ViewHolder viewHolder) {
        viewHolder.line1.setVisibility(View.GONE);
        viewHolder.line2.setVisibility(View.GONE);
        viewHolder.done.setVisibility(View.GONE);
        viewHolder.delete.setVisibility(View.GONE);
        viewHolder.tagLabel.setVisibility(View.VISIBLE);
        viewHolder.edit.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return editLabelModeLS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        EditText labelCreated;
        TextView labelText;
        LinearLayout labelLayout;
        View line1, line2;
        ImageView tagLabel, edit, done, delete;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            labelCreated = itemView.findViewById(R.id.label_created);
            labelText = itemView.findViewById(R.id.label_created_text);
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