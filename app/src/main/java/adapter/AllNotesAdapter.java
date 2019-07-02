package adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import e.wolfsoft1.Xnotes.CategoriesNotes;
import e.wolfsoft1.Xnotes.ImagePost;
import e.wolfsoft1.Xnotes.MainActivity;
import e.wolfsoft1.Xnotes.NotesPostActivity;
import e.wolfsoft1.Xnotes.R;
import e.wolfsoft1.Xnotes.TrashActivity;
import e.wolfsoft1.Xnotes.fragments.PlaybackFragment;
import model.NotePostModel;

public class AllNotesAdapter extends RecyclerView.Adapter<AllNotesAdapter.ViewHolder> {

    private static final String LOG_TAG = "FileViewerAdapter";
    String key;
    String trashdata;
    FirebaseAuth fAuth;
    private DatabaseReference fNotesDatabase;
    public Boolean isLongClickEnabled = false;

    private Context context;
    private ArrayList<NotePostModel> noteModelArrayList;

    private ProgressDialog mProgress;
    private int selecteditem;


    public AllNotesAdapter(Context context, ArrayList<NotePostModel> noteModelArrayList, String trashdata) {
        this.context = context;
        this.noteModelArrayList = noteModelArrayList;
    }


    @NonNull
    @Override
    public AllNotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notes_layout, viewGroup, false);
        return new AllNotesAdapter.ViewHolder(view, i);

    }

    @Override
    public void onBindViewHolder(@NonNull final AllNotesAdapter.ViewHolder viewHolder, final int i) {

        final NotePostModel noteModel = noteModelArrayList.get(i);

        if (!(noteModel.getContent() == null) && !(noteModel.getContent().isEmpty())) {

            viewHolder.content.setText(noteModel.getContent());
            viewHolder.content.setVisibility(View.VISIBLE);

        } else {
            viewHolder.content.setVisibility(View.GONE);
        }

        if (selecteditem == 0) {

            if (context instanceof MainActivity) {
                ((MainActivity) context).releseItem();
            } else if (context instanceof CategoriesNotes) {
                ((CategoriesNotes) context).releseItem();
            }

            isLongClickEnabled = false;
        }

        key = noteModel.getKey();
        Log.e("aaa", "onBindViewHolder: " + noteModel.getSelectColorImg());
        final String key = noteModel.getKey();
        final String title = noteModel.getTitle();
        final String content = noteModel.getContent();
        final String image = noteModel.getmImageUrl();
        final String color = noteModel.getSelectColorImg();
        final String voice = String.valueOf(noteModel.getTimeLengthMinutes()) + ":" + String.valueOf(noteModel.getTimeLengthSeconds());
        final String record = noteModel.getRecord();
        final String selectinput = noteModel.getSelectinput();
        final String label = noteModel.getLabelTag();

        if (label != null) {

            viewHolder.labelTag.setVisibility(View.VISIBLE);
            viewHolder.labelTag.setText(label);

        } else {

            viewHolder.labelTag.setVisibility(View.GONE);

        }

        LinearLayout.LayoutParams paramscontent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (image != null) {

            viewHolder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(noteModelArrayList.get(i).getmImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .fit().centerCrop()
                    .into(viewHolder.imageView);
            viewHolder.record.setVisibility(View.GONE);
            viewHolder.timeLength.setVisibility(View.GONE);
            viewHolder.content.setText(noteModel.getContent());

            paramscontent.setMargins(0, 15, 0, 30);
            viewHolder.content.setLayoutParams(paramscontent);
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));

            if (!isLongClickEnabled) {
                viewHolder.linearLayout.setBackgroundResource(0);
                viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));
            } else {
                if (noteModel.isSelected()) {
                    viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                } else {
                    viewHolder.linearLayout.setBackgroundResource(0);
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));
                }
            }

            invisibleContent(viewHolder, noteModel);

        } else if (record != null) {

            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.content.setVisibility(View.GONE);
            viewHolder.record.setVisibility(View.VISIBLE);
            viewHolder.timeLength.setVisibility(View.VISIBLE);

            if (!isLongClickEnabled) {

            } else {
                if (noteModel.isSelected()) {
                    viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                } else {
                    viewHolder.linearLayout.setBackgroundResource(0);
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));
                }
            }

            if (noteModel.getTimeLengthMinutes() > 9) {

                viewHolder.timeLength.setText(String.valueOf(voice));
            } else {

                viewHolder.timeLength.setText(String.valueOf("0" + voice));
            }

            if (noteModel.getTimeLengthSeconds() > 9) {
                viewHolder.timeLength.setText(String.valueOf(voice));
            } else {
                viewHolder.timeLength.setText(String.valueOf("0" + voice));
            }

            invisibleContent(viewHolder, noteModel);

        } else if (content != null) {

            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.linearLayout.setVisibility(View.VISIBLE);
            viewHolder.record.setVisibility(View.GONE);
            viewHolder.timeLength.setVisibility(View.GONE);
            viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));

            if (!content.isEmpty() && title.isEmpty()) {

                paramscontent.setMargins(20, 10, 10, 10);
                viewHolder.content.setLayoutParams(paramscontent);

            }
            viewHolder.content.setText(noteModel.getContent());

            if (!isLongClickEnabled) {

            } else {
                if (noteModel.isSelected()) {
                    viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                } else {
                    viewHolder.linearLayout.setBackgroundResource(0);
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor(color));
                }
            }

            invisibleContent(viewHolder, noteModel);
        }
        if (trashdata == null) {
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!isLongClickEnabled) {
                        if (!noteModelArrayList.get(i).isSelected()) {
                            noteModel.setSelected(true);
                            viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).setView();
                            } else if (context instanceof CategoriesNotes) {
                                ((CategoriesNotes) context).setView();
                            }
                            selecteditem++;

                        } else if (noteModelArrayList.get(i).isSelected()) {
                            noteModel.setSelected(false);
                            Toast.makeText(context, "you have not select data", Toast.LENGTH_SHORT).show();
                        }
                        if (noteModelArrayList.isEmpty()) {
                            Toast.makeText(context, "There is not data", Toast.LENGTH_SHORT).show();
                        }
                        isLongClickEnabled = true;
                    }
                    return true;

                }

            });
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (context instanceof TrashActivity) {

                        if (isLongClickEnabled != true) {

                        }

                    } else {

                        Intent intent = null;
                        if (!isLongClickEnabled) {

                            if (!(image == null) && !(image.isEmpty())) {

                                intent = new Intent(context, ImagePost.class);
                                intent.putExtra("key", key);
                                intent.putExtra("title", title);
                                intent.putExtra("content", content);
                                intent.putExtra("image", image);
                                intent.putExtra("color", color);
                                intent.putExtra("selectinput", selectinput);
                                context.startActivity(intent);

                            } else if ((!(content == null) && !(content.isEmpty())) || (!(title == null) && !(title.isEmpty()))) {

                                intent = new Intent(context, NotesPostActivity.class);

                                intent.putExtra("key", key);
                                intent.putExtra("title", title);
                                intent.putExtra("content", content);
                                intent.putExtra("color", color);
                                Log.e("fdsf", "fsd" + key);
                                intent.putExtra("selectinput", selectinput);
                                intent.putExtra("labelTag", label);
                                context.startActivity(intent);

                            } else {

                                intent = new Intent(context, MainActivity.class);

                                intent.putExtra("key", key);
                                intent.putExtra("title", title);
                                intent.putExtra("record", record);
                                intent.putExtra("voice", voice);
                                intent.putExtra("selectinput", selectinput);
                                context.startActivity(intent);

                            }

                        } else {
                            if (noteModelArrayList.get(i).isSelected() == false) {
                                noteModel.setSelected(true);
                                selecteditem++;
                                viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                                if (context instanceof MainActivity) {
                                    ((MainActivity) context).setView();
                                } else if (context instanceof CategoriesNotes) {
                                    ((CategoriesNotes) context).setView();
                                }

                            } else if (noteModelArrayList.get(i).isSelected() == true) {
                                noteModel.setSelected(false);
                                selecteditem--;
                                Toast.makeText(context, "you have not select data", Toast.LENGTH_SHORT).show();
                            }

                            if (noteModelArrayList.isEmpty()) {
                                Toast.makeText(context, "There is not data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

//                    notifyItemRemoved(i);
                    notifyItemChanged(i);
                }
            });


            viewHolder.record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mProgress = new ProgressDialog(context);

                    try {
                        PlaybackFragment playbackFragment =
                                new PlaybackFragment().newInstance(noteModelArrayList.get(i));

                        FragmentTransaction transaction = ((FragmentActivity) context)
                                .getSupportFragmentManager()
                                .beginTransaction();

                        playbackFragment.show(transaction, "dialog_playback");

                    } catch (Exception e) {

                        Log.e(LOG_TAG, "exception", e);

                    }
                }
            });

        } else if (trashdata != null) {

        }

    }

    private void deleteImageNote() {
        fNotesDatabase.removeValue();

    }

    public void updateData(ArrayList<NotePostModel> datas) {
        noteModelArrayList = new ArrayList<>();
        noteModelArrayList.addAll(datas);
        notifyDataSetChanged();
    }


    private void invisibleContent(ViewHolder viewHolder, NotePostModel noteModel) {

        if (!(noteModel.getTitle() == null) && !(noteModel.getTitle().isEmpty())) {
            viewHolder.title.setText(noteModel.getTitle());
            viewHolder.title.setVisibility(View.VISIBLE);
        } else {

            viewHolder.title.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {

        return (noteModelArrayList == null) ? 0 : noteModelArrayList.size();

    }

    public void deleteSelectedData() {

        for (int i = 0; i < noteModelArrayList.size(); i++) {

            fAuth = FirebaseAuth.getInstance();
            fNotesDatabase = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

            if (noteModelArrayList.get(i).isSelected()) {
                final String dkey = noteModelArrayList.get(i).getKey();

                fNotesDatabase.child(dkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NotePostModel model = dataSnapshot.getValue(NotePostModel.class);
                        DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(fAuth.getCurrentUser().getUid());
                        trashReference.child(dkey).setValue(model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                fNotesDatabase.child(dkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Note deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content, labelTag;
        LinearLayout linearLayout, linearRecord;
        ImageView imageView;
        Chronometer timeLength;
        ImageButton record;
        View rowView;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            rowView = itemView;

            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_description);
            linearLayout = itemView.findViewById(R.id.linear_notes);
            record = itemView.findViewById(R.id.record_btn);
            timeLength = itemView.findViewById(R.id.chronometerTimer);
            linearRecord = itemView.findViewById(R.id.linear_record);
            labelTag = itemView.findViewById(R.id.label_tag);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}