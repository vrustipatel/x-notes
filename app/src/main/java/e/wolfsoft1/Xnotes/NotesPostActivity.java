package e.wolfsoft1.Xnotes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.ColorSelectionAdapter;
import model.NotePostModel;
import model.ColorSelectionModel;

public class NotesPostActivity extends AppCompatActivity {

    Button btnCreate;
    EditText edTitle, edContent;
    private DatabaseReference fNotesDBreference;
    private FirebaseAuth fAuth;
    RelativeLayout customView;
    private String key, title, content, label, labelkey, date, nameoftag, fontStyleType;
    private boolean isExist;
    private Dialog slideDialog;
    private String tag;
    private String color = "#00000000";
    LinearLayout deleteNote;
    LinearLayout linearLayoutBgColor;
    LinearLayout labelDirectory, shareData;
    TextView tagName, postEditedTime, titleToolbar;
    TextView addingNotesInLabel;
    ImageView back, trashIcon, selectCustomFontStyle;

    private RecyclerView selectClrRecy;
    private ColorSelectionAdapter colorAdapter;
    private ArrayList<ColorSelectionModel> colorModels;
    private String strDate;
    private LinearLayout deletePost;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note_post);

        /*---------------------------Firebase Tabels---------------------------------------*/

        fAuth = FirebaseAuth.getInstance();
        fNotesDBreference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        /*---------------------------FindViewByIds--------------------------------------*/

        initialMethod();

        titleToolbar.setText(null);
        labelkey = getIntent().getStringExtra("labelkey");

        //
        selectCustomFontStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (position == 0) {
                    Typeface font_style = ResourcesCompat.getFont(NotesPostActivity.this, R.font.roboto_bold);
                    edTitle.setTypeface(font_style);
                    fontStyleType = "roboto_bold.ttf";
                    position = 1;

                } else if (position == 1) {

                    Typeface font_style = ResourcesCompat.getFont(NotesPostActivity.this, R.font.roboto_italic);
                    edTitle.setTypeface(font_style);
                    fontStyleType = "roboto_bold.ttf";

                    position = 2;

                } else if (position == 2) {

                    Typeface font_style = ResourcesCompat.getFont(NotesPostActivity.this, R.font.sf_pro_display_light);
                    edTitle.setTypeface(font_style);
                    fontStyleType = "roboto_bold.ttf";
                    position = 3;

                } else {
                    Typeface font_style = ResourcesCompat.getFont(NotesPostActivity.this, R.font.notoserif_bold);
                    Typeface font_style_content = ResourcesCompat.getFont(NotesPostActivity.this, R.font.roboto_regular);
                    edTitle.setTypeface(font_style);
                    edContent.setTypeface(font_style_content);
                    fontStyleType = "notoserif_bold.ttf";
                    position = 0;

                }
            }
        });

        try {


            Intent intent = getIntent();
            key = intent.getStringExtra("key");
            title = intent.getStringExtra("title");
            tag = intent.getStringExtra("labelTag");
            nameoftag = intent.getStringExtra("tagname");

            if (intent.getStringExtra("tagname") != null) {

                tagName.setVisibility(View.VISIBLE);
                tagName.setText(nameoftag);

                Toast.makeText(this, nameoftag, Toast.LENGTH_SHORT).show();


            } else {

            }

            if (intent.getStringExtra("color") != null) {
                color = intent.getStringExtra("color");
            }
            content = intent.getStringExtra("content");
//            date = intent.getStringExtra("date");


            if (key.equals("no")) {
                isExist = false;
            } else {
                isExist = true;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        if (tag != null) {

            tagName.setVisibility(View.VISIBLE);
            tagName.setText(" \"" + tag + "\" ");

        } else {

            tagName.setVisibility(View.GONE);
            addingNotesInLabel.setVisibility(View.GONE);

        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy  hh:mm a");
        Date dateandtime = new Date();
        strDate = dateFormat.format(dateandtime).toString();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        putData();

        selectColorForCustomLayout();
//        edContent.setTextIsSelectable(true);

        edContent.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            private List<Integer> validResIdList = new ArrayList<>();

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                menu.clear();
                validResIdList.clear();

                mode.getMenuInflater().inflate(R.menu.main_menu, menu);

                for (int i = 0; i < menu.size(); i++) {

                    validResIdList.add(menu.getItem(i).getItemId());

                }

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                List<Integer> toRemoveResId = new ArrayList<>();

                for (int i = 0; i < menu.size(); i++) {

                    if (menu.getItem(i).getItemId() != (validResIdList.get(0)) && menu.getItem(i).getItemId() != (validResIdList.get(1)) && menu.getItem(i).getItemId() != (validResIdList.get(2))) {

                        toRemoveResId.add(menu.getItem(i).getItemId());

                    }
                }

                for (Integer resId : toRemoveResId) {
                    menu.removeItem(resId);
                }

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (validResIdList != null && validResIdList.size() > 0) {

                    if (item.getItemId() == validResIdList.get(0)) {
//                        ErrorController.showToast(NotesPostActivity.this, "View Vocab : " + edContent.getText().toString().substring(edContent.getSelectionStart(), edContent.getSelectionEnd()));

                        mode.finish();

                    } else if (item.getItemId() == validResIdList.get(1)) {//cpy
//                        ErrorController.showToast(NotesPostActivity.this, "Copy : " + edContent.getSelectionStart() + ", " + edContent.getSelectionEnd());
                        mode.finish();
                    } else if (item.getItemId() == validResIdList.get(2)) {//cpy
//                        ErrorController.showToast(NotesPostActivity.this, "Copy : " + edContent.getSelectionStart() + ", " + edContent.getSelectionEnd());
                        mode.finish();
                    } else {
                        mode.finish();
                    }
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


    }

    private void initialMethod() {
        edTitle = findViewById(R.id.new_note_title);
        edContent = findViewById(R.id.new_note_content);
        tagName = findViewById(R.id.tag_name);
        linearLayoutBgColor = findViewById(R.id.linear_layout);
        postEditedTime = findViewById(R.id.time_and_date_of_post);
        titleToolbar = findViewById(R.id.title_toolbar);
        addingNotesInLabel = findViewById(R.id.adding_notes_in_label);
        back = findViewById(R.id.back);
        trashIcon = findViewById(R.id.trash_icon);
        selectCustomFontStyle = findViewById(R.id.select_custom_font_style);
    }

    private void deleteExistPost() {

        slideDialog = new Dialog(NotesPostActivity.this, R.style.CustomDialogAnimation);
        Window window = slideDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        slideDialog.setContentView(R.layout.dialog_delete_post);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(slideDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        slideDialog.getWindow().setAttributes(layoutParams);

        slideDialog.setCancelable(true);
        slideDialog.setCanceledOnTouchOutside(true);
        slideDialog.show();
        deletePost = slideDialog.findViewById(R.id.delete_post_linear);


        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
    }

    private void selectColorForCustomLayout() {

        selectClrRecy = findViewById(R.id.select_clr_recy);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(NotesPostActivity.this, LinearLayoutManager.HORIZONTAL, false);
        selectClrRecy.setLayoutManager(layoutManager);
        selectClrRecy.setItemAnimator(new DefaultItemAnimator());

        colorModels = new ArrayList<>();

        colorModels.add(new ColorSelectionModel("#fdcee7"));
        colorModels.add(new ColorSelectionModel("#b1f7df"));
        colorModels.add(new ColorSelectionModel("#b1e4f7"));
        colorModels.add(new ColorSelectionModel("#f7f4b1"));
        colorModels.add(new ColorSelectionModel("#cff7b1"));
        colorModels.add(new ColorSelectionModel("#ffffff"));
        colorModels.add(new ColorSelectionModel("#B3F6E3"));
        colorModels.add(new ColorSelectionModel("#64C2C2"));
        colorModels.add(new ColorSelectionModel("#5F9EA0"));
        colorModels.add(new ColorSelectionModel("#F9C5E3"));
        colorModels.add(new ColorSelectionModel("#F98085"));

        colorAdapter = new ColorSelectionAdapter(NotesPostActivity.this, colorModels);
        selectClrRecy.setAdapter(colorAdapter);
    }

    private void createNote(String title, String content) {

        if (fAuth.getCurrentUser() != null) {
            if (isExist) {

                //update a existing note
                postEditedTime.setVisibility(View.VISIBLE);
                trashIcon.setVisibility(View.VISIBLE);
                Map updateMap = new HashMap();

                updateMap.put("title", edTitle.getText().toString().trim());
                updateMap.put("content", edContent.getText().toString().trim());
                updateMap.put("selectColorImg", color);
                updateMap.put("date", strDate);
                postEditedTime.setText("Last edited : " + strDate);
                linearLayoutBgColor.setBackgroundColor(Color.parseColor(color));

                fNotesDBreference.child(key).updateChildren(updateMap);
                Toast.makeText(NotesPostActivity.this, "Note updated", Toast.LENGTH_SHORT).show();

                finish();

            } else {

                //create a new note
                final String key = fNotesDBreference.push().getKey();
                final NotePostModel noteModel = new NotePostModel();

                noteModel.setTitle(title);
                noteModel.setContent(content);
                noteModel.setKey(key);
                noteModel.setSelectColorImg(color);
                noteModel.setSelectinput("1");
                noteModel.setDate(strDate);
                noteModel.setLabelKey(labelkey);
                postEditedTime.setVisibility(View.GONE);

                if (tag != null) {
                    noteModel.setLabelTag(tag);

                } else {
                    addingNotesInLabel.setVisibility(View.GONE);

                    tagName.setVisibility(View.GONE);
                }

                fNotesDBreference.child(key).setValue(noteModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(NotesPostActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(NotesPostActivity.this, "ERROR:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        } else {
            Toast.makeText(this, "USER ISNOT SIGNIN", Toast.LENGTH_SHORT).show();
        }
    }

    private void putData() {
        if (isExist) {
            fNotesDBreference.child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("content")) {
                        final String title = dataSnapshot.child("title").getValue().toString();
                        final String content = dataSnapshot.child("content").getValue().toString();
                        String date = dataSnapshot.child("date").getValue().toString();
//                        String color = dataSnapshot.child("selectColorImg").getValue().toString();
                        postEditedTime.setVisibility(View.VISIBLE);

                        edTitle.setText(title);
                        edContent.setText(content);
                        postEditedTime.setText("Last edited : " + date);

                        linearLayoutBgColor.setBackgroundColor(Color.parseColor(color));

                        trashIcon.setVisibility(View.VISIBLE);
                        trashIcon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteExistPost();
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void deleteNote() {

        fNotesDBreference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NotePostModel model = dataSnapshot.getValue(NotePostModel.class);
                DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(fAuth.getCurrentUser().getUid());
                trashReference.child(key).setValue(model);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (isExist) {

            fNotesDBreference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(NotesPostActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.e("NotesPostActivity", task.getException().toString());
                        Toast.makeText(NotesPostActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "There is no content", Toast.LENGTH_SHORT).show();
        }
    }

    public void colorMethod(ColorFilter color, ColorSelectionModel selectColorModel) {
        this.color = selectColorModel.getSelectColorImg();
        linearLayoutBgColor.setBackgroundColor(Color.parseColor(this.color));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String title = edTitle.getText().toString().trim();
        String content = edContent.getText().toString().trim();

        if ((!(title == null) && !(title.isEmpty())) || (!(content == null) && !(content.isEmpty()))) {

            createNote(title, content);

        } else {
            Toast.makeText(NotesPostActivity.this, "Fill empty fields", Toast.LENGTH_SHORT).show();
        }

    }


    public void boldAction(MenuItem item) {
        Typeface font_style1 = ResourcesCompat.getFont(NotesPostActivity.this, R.font.roboto_bold);
        edContent.setTypeface(font_style1);
    }

    public void italicAction(MenuItem item) {
        Typeface font_style2 = ResourcesCompat.getFont(NotesPostActivity.this, R.font.roboto_italic);
        edContent.setTypeface(font_style2);
    }

    public void regularAction(MenuItem item) {
        Typeface font_style3 = ResourcesCompat.getFont(NotesPostActivity.this, R.font.sf_pro_display_light);
        edContent.setTypeface(font_style3);

    }
}