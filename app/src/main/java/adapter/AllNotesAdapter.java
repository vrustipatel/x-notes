package adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import e.vp.Xnotes.CategoriesNotes;
import e.vp.Xnotes.ImagePost;
import e.vp.Xnotes.MainActivity;
import e.vp.Xnotes.NotesPostActivity;
import e.vp.Xnotes.R;
import e.vp.Xnotes.TrashActivity;
import e.vp.Xnotes.fragments.PlaybackFragment;
import model.NotePostModel;

public class AllNotesAdapter extends RecyclerView.Adapter<AllNotesAdapter.ViewHolder> {

    private static final String LOG_TAG = "FileViewerAdapter";
    String key;
    String trashdata;
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
        final String key = noteModel.getKey();
        final String title = noteModel.getTitle();
        final String content = noteModel.getContent();
        final String image = noteModel.getmImageUrl();
        final String color = noteModel.getSelectColorImg();
        final String voice = String.valueOf(noteModel.getTimeLengthMinutes()) + ":" + String.valueOf(noteModel.getTimeLengthSeconds());
        final String record = noteModel.getRecord();
        final String selectinput = noteModel.getSelectinput();
        final String label = noteModel.getLabelTag();
        final String input = noteModel.getSelectinput();

        if (label != null) {

            viewHolder.labelTag.setVisibility(View.VISIBLE);
            viewHolder.labelTag.setText(label);

        } else {

            viewHolder.labelTag.setVisibility(View.GONE);

        }

        LinearLayout.LayoutParams paramscontent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramstitle = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (image != null) {

            viewHolder.imageView.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(noteModelArrayList.get(i).getmImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .fit().centerCrop()
                    .into(viewHolder.imageView);
            viewHolder.record.setVisibility(View.GONE);
            viewHolder.timeLength.setVisibility(View.GONE);
            viewHolder.content.setText(noteModel.getContent());

            paramscontent.setMargins(0, 15, 0, 30);
            paramstitle.setMargins(0, 0, 0, 10);
            viewHolder.title.setLayoutParams(paramstitle);
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
                viewHolder.linearLayout.setBackgroundResource(0);
                viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#fdcee7"));
            } else {
                if (noteModel.isSelected()) {
                    viewHolder.linearLayout.setBackgroundResource(R.drawable.gray_rect);
                } else {
                    viewHolder.linearLayout.setBackgroundResource(0);
                    viewHolder.linearLayout.setBackgroundColor(Color.parseColor("#fdcee7"));
                }
            }

            String min;

            if (noteModel.getTimeLengthMinutes() > 9) {

                min = String.valueOf(noteModel.getTimeLengthMinutes());
            } else {

                min = "0" + String.valueOf(noteModel.getTimeLengthMinutes()) + ":";
            }

            if (noteModel.getTimeLengthSeconds() > 9) {
                viewHolder.timeLength.setText(min + String.valueOf(noteModel.getTimeLengthSeconds()));
            } else {
                viewHolder.timeLength.setText(min + "0" + String.valueOf(noteModel.getTimeLengthSeconds()));
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

                        viewHolder.itemView.setEnabled(false);
                        viewHolder.itemView.setLongClickable(false);

                    } else {
                        Intent intent = null;
                        if (!isLongClickEnabled) {

                            if (input.equals("2")) {

                                intent = new Intent(context, ImagePost.class);
                                intent.putExtra("key", key);
                                intent.putExtra("title", title);
                                intent.putExtra("content", content);
                                intent.putExtra("image", image);
                                intent.putExtra("color", color);
                                intent.putExtra("selectinput", selectinput);
                                intent.putExtra("labelTag", label);

                                context.startActivity(intent);

                            } else if (input.equals("1")) {

                                intent = new Intent(context, NotesPostActivity.class);

                                intent.putExtra("key", key);
                                intent.putExtra("title", title);
                                intent.putExtra("content", content);
                                intent.putExtra("color", color);
                                Log.e("fdsf", "fsd" + key);
                                intent.putExtra("selectinput", selectinput);
                                intent.putExtra("labelTag", label);
                                context.startActivity(intent);

                            } else if (input.equals("3")) {

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
                            }

                            if (noteModelArrayList.isEmpty()) {
                                Toast.makeText(context, "There is not data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    notifyItemChanged(i);
                }
            });


        } else if (trashdata != null) {

            isLongClickEnabled = false;
            viewHolder.itemView.setEnabled(false);
            viewHolder.itemView.setLongClickable(false);

        }
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