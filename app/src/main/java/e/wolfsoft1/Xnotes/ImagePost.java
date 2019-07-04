package e.wolfsoft1.Xnotes;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import adapter.ColorSelectionAdapter;
import model.NotePostModel;
import model.ColorSelectionModel;


public class ImagePost extends AppCompatActivity {

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private Uri mImageUri;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private String color = "#00000000";
    private boolean isExist;
    private static final int PICK_IMAGE_REQUEST = 1;
    String newkey;
    private ProgressDialog mProgress;

    EditText mEditTextFileName, edNoteContent;
    ImageView mImageView;

    private String key, title, content, image, labelKey;
    private Dialog slideDialog;
    LinearLayout linearLayoutBgColor;
    TextView addingNotesInLabel;
    TextView titleToolbar;
    private String tag;
    private TextView labelName, postEditedTime;
    private String strDate;
    ImageView back;

    private RecyclerView selectClrRecy;
    private ColorSelectionAdapter colorAdapter;
    private ArrayList<ColorSelectionModel> colorModels;
    private ImageView trashIcon;
    LinearLayout deletePost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);

        /*----------------------------------Firebase tables-------------------------------*/

        mStorageReference = FirebaseStorage.getInstance().getReference("Notes");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Notes");

        /*---------------------------------FindViewById----------------------------------*/

        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        key = intent.getStringExtra("key");
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        if (intent.getStringExtra("color") != null) {
            color = intent.getStringExtra("color");
        }
        labelKey = intent.getStringExtra("labelkey");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        mImageView = findViewById(R.id.upload_image);
        edNoteContent = findViewById(R.id.new_note_content);
        linearLayoutBgColor = findViewById(R.id.linear_layout);
        labelName = findViewById(R.id.label_name);
        mEditTextFileName = findViewById(R.id.edittext);
        edNoteContent = findViewById(R.id.note_content);
        postEditedTime = findViewById(R.id.time_and_date_of_post);
        addingNotesInLabel = findViewById(R.id.adding_notes_in_label);
        trashIcon = findViewById(R.id.trash_icon);
        back = findViewById(R.id.back);
        titleToolbar = findViewById(R.id.title_toolbar);

        titleToolbar.setText(null);

        tag = intent.getStringExtra("labelTag");
        mProgress = new ProgressDialog(this);

        //direct gallery
        mImageUri = getIntent().getParcelableExtra("imageUri");
        Picasso.with(ImagePost.this).load(mImageUri).into(mImageView);

        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy  hh:mm a");
        Date dateandtime = new Date();
        strDate = dateFormat.format(dateandtime).toString();
        postEditedTime.setText(strDate);


        /*------------------------------------update image post----------------------------------------*/

        if (tag != null) {


            Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
            addingNotesInLabel.setVisibility(View.VISIBLE);
            labelName.setVisibility(View.VISIBLE);

            labelName.setText(" \"" + tag + "\" ");

        }

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChoose();
            }
        });
        selectColorForCustomLayout();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateImagePost();

    }

    /*----------------------------Open Gallery-------------------------------------*/

    private void openFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    private void uploadImage() {

        mProgress.show();
        mProgress.setMessage("Uploading image......");

        final String imageName = UUID.randomUUID().toString();
        final String date = postEditedTime.getText().toString().trim();

        mStorageReference.child(imageName).putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mStorageReference.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {

                                String imageurl = downloadUrl.toString();
                                newkey = mDatabaseReference.push().getKey();

                                title = mEditTextFileName.getText().toString();
                                content = edNoteContent.getText().toString();

                                final NotePostModel imagePostModel;

                                if (key != null) {

                                    imagePostModel = new NotePostModel(title, content, key, imageurl, null, 0, 0, color, "2", tag, date, labelKey, null);
                                    mDatabaseReference.child(firebaseUser.getUid()).child(key).setValue(imagePostModel);


                                } else {
                                    imagePostModel = new NotePostModel(title, content, newkey, imageurl, null, 0, 0, color, "2", tag, date, labelKey, null);
                                    mDatabaseReference.child(firebaseUser.getUid()).child(newkey).setValue(imagePostModel);
                                }
                                if (tag != null) {
                                    imagePostModel.setLabelTag(tag);
                                } else {
                                    labelName.setVisibility(View.GONE);
                                }

                                Toast.makeText(ImagePost.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ImagePost.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        });

                    }
                });
    }

    private void selectColorForCustomLayout() {

        selectClrRecy = findViewById(R.id.select_clr_recy);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ImagePost.this, LinearLayoutManager.HORIZONTAL, false);
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

        colorAdapter = new ColorSelectionAdapter(ImagePost.this, colorModels);
        selectClrRecy.setAdapter(colorAdapter);

    }

    private void deleteExistPost() {

        slideDialog = new Dialog(ImagePost.this, R.style.CustomDialogAnimation);
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
                deleteImageNote();
            }
        });

    }

    private void deleteImageNote() {

        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NotePostModel model = dataSnapshot.getValue(NotePostModel.class);
                DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(mAuth.getCurrentUser().getUid());
                trashReference.child(key).setValue(model);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mDatabaseReference.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                NotePostModel model = dataSnapshot.getValue(NotePostModel.class);
//                DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(mAuth.getCurrentUser().getUid());
//                trashReference.child(key).setValue(model);
//
//                Toast.makeText(ImagePost.this, key, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        if (!isExist) {

            mDatabaseReference.child(mAuth.getCurrentUser().getUid()).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(ImagePost.this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.e("ImagePost", task.getException().toString());
                        Toast.makeText(ImagePost.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "There is no content", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(ImagePost.this);


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                Picasso.with(ImagePost.this)
                        .load(mImageUri)
                        .into(mImageView);

            }
        }
    }

    private void updateImagePost() {

        if (image != null) {

            trashIcon.setVisibility(View.VISIBLE);
            Picasso.with(ImagePost.this).load(image).into(mImageView);
            mEditTextFileName.setText(title);
            edNoteContent.setText(content);
            labelName.setText(" \"" + tag + "\" ");
            linearLayoutBgColor.setBackgroundColor(Color.parseColor(color));

            postEditedTime.setVisibility(View.VISIBLE);
            postEditedTime.setText("Last edited : " + strDate);

            if (tag != null) {

            } else {

                addingNotesInLabel.setVisibility(View.GONE);
                labelName.setVisibility(View.GONE);

            }

            mDatabaseReference.child(firebaseUser.getUid()).child(key).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            image = null;
                            openFileChoose();

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            trashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteExistPost();
                }
            });

        }
    }

    public void colorMethod(ColorFilter color, ColorSelectionModel selectColorModel) {
        this.color = selectColorModel.getSelectColorImg();
        linearLayoutBgColor.setBackgroundColor(Color.parseColor(this.color));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (image != null) {

            mProgress.show();
            mProgress.setMessage("Uploading image......");

            DatabaseReference reference = mDatabaseReference.child(firebaseUser.getUid()).child(key);
            title = mEditTextFileName.getText().toString();
            content = edNoteContent.getText().toString();
            labelName.setText(" \"" + tag + "\" ");
            postEditedTime.setText("Last edited : " + strDate);
            linearLayoutBgColor.setBackgroundColor(Color.parseColor(color));

            reference.child("mImageUrl").setValue(image);
            reference.child("title").setValue(title);
            reference.child("content").setValue(content);
            reference.child("date").setValue(strDate);
            reference.child("labelKey").setValue(labelKey);
            reference.child("selectColorImg").setValue(color);

            Intent intent = new Intent(ImagePost.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else if (mImageUri != null) {

            uploadImage();

            Intent intent = new Intent(ImagePost.this, MainActivity.class);

            if (mProgress != null) {
                intent.putExtra("progress", "10");
                intent.putExtra("labelTag", tag);

            }
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }
}
