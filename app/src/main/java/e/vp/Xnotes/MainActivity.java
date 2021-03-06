package e.vp.Xnotes;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import adapter.AllNotesAdapter;
import adapter.ModifyLabelAdapter;
import model.ModifyLabelModeL;
import model.NotePostModel;

public class MainActivity extends AppCompatActivity {

    //variable initialization
    private static final int PERM_REQ_CODE = 23;
    FirebaseAuth fAuth;
    private FirebaseStorage mStorageImage, mStorageLabels;
    private DatabaseReference fNoteDBreference, fLabelDBreference;
    private StorageReference mStorageReference;
    FirebaseUser firebaseUser;
    private ProgressDialog mProgress;

    private RecyclerView mNoteList;
    private AllNotesAdapter notesAdapter;
    private ArrayList<NotePostModel> postModel;

    private ModifyLabelAdapter editLabelAdapter;
    private RecyclerView mLabelList;
    private ArrayList<ModifyLabelModeL> editLabelModeLS;

    CardView cardAddNewLabel, cardviewDrawer, cardviewShowUpdates, cardviewSearchNotes;
    String trashdata, labelkey;
    ImageView emptyState, chooseImage, voiceRecorder, profileDrawer, expandedMenu, search, delete, createNewLabel, gridMenu, cancel;
    TextView addNotes, editlabelstxt, inputName, inputEmail, nameOfTag, labelTag, logouttxt, emptyStateText;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    Boolean selectedData = false;

    LinearLayout listOfText, listOfTrashData, listOfAudios, listOfImages, takePhotoLinear, chooseImgLinear;
    private ImageButton mButtonRecord;
    private Chronometer chronometer;
    private String mFileName = null;
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "Record_log";
    EditText voiceNote;
    private DatabaseReference recordDBreference;
    TextView selectAll, postEditedTime;
    private Dialog slideDialog;
    private String tag;
    private Query query;
    Animation animFadein;
    private AlertDialog.Builder builder;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    LinearLayout allNotesLinear;
    Button recordingDone;
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    Uri uri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    EditText searchNotesTxt;

    Boolean isRecording = false;
    Boolean isAllNotes = false;
    private NestedScrollView mainScrollView;
    private boolean isDrawerisOpen = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /*------------------------------------casting of variables--------------------------------*/


        initializeMethod();

        /*-------------------------------------load Data-------------------------------------------*/


        mProgress = new ProgressDialog(MainActivity.this);
        labelkey = getIntent().getStringExtra("labelkey");


        /*----------------------------------get labelwise data like folder---------------------------*/


        if (tag != null) {
            labeledData();
        } else {
            isAllNotes = true;
            query = fNoteDBreference;
            loadData();
        }

        mainScrollView.fullScroll(ScrollView.FOCUS_UP);

        /*----------------------------------------get gmail Id,username & user profile -----------------------------------*/


        inputEmail.setText(fAuth.getCurrentUser().getEmail());
        inputName.setText(fAuth.getCurrentUser().getDisplayName());
        Picasso.with(MainActivity.this).load(fAuth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.profile).into(profileDrawer);

        /*--------------------------------------check RunTime Permission-----------------------------------*/


        if (arePermissionsEnabled()) {
        } else {
            requestMultiplePermissions();
        }

        if (checkStoragePermission()) {
        } else {
            requestAudioPermission();
        }


        /*------------------------------------click listeners---------------------------------------*/

        clickListeneres();

        /*------------------------------------retrive all type of notes here-----------------------------------------*/

        loadData();

        /*-----------------------------------------retrive newly created label------------------------------------------------*/

        createLabelDatas();
        /*---------------------------------------logouttxt---------------------------------------------*/

        logouttxt = findViewById(R.id.logout);
        logouttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

    }

    private void initializeMethod() {

        /*-----------------------------------firebase Tables----------------------------------------*/

        fAuth = FirebaseAuth.getInstance();
        fNoteDBreference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        recordDBreference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        mStorageReference = FirebaseStorage.getInstance().getReference().child("Audio");


        Intent intent = getIntent();
        tag = intent.getStringExtra("labelTag");


        /*------------------------------- findViewById--------------------------------------*/


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        expandedMenu = findViewById(R.id.expanded_menu);
        addNotes = findViewById(R.id.add_notes);
        chooseImage = findViewById(R.id.choose_image);
        cardviewDrawer = findViewById(R.id.cardview_drawer);
        cardviewShowUpdates = findViewById(R.id.cardview_show_updates);
        cardviewSearchNotes = findViewById(R.id.cardview_search_notes);
        voiceRecorder = findViewById(R.id.voice_recorder);
        profileDrawer = findViewById(R.id.profile_drawer);
        cardAddNewLabel = findViewById(R.id.card_add_new_label);
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        gridMenu = findViewById(R.id.grid_menu);
        cancel = findViewById(R.id.cancel);
        delete = findViewById(R.id.delete_all);
        selectAll = findViewById(R.id.select_all);
        createNewLabel = findViewById(R.id.create_new_label);
        listOfImages = findViewById(R.id.list_of_images);
        listOfText = findViewById(R.id.list_of_text);
        listOfAudios = findViewById(R.id.list_of_Audios);
        listOfTrashData = findViewById(R.id.list_of_trash_data);
        editlabelstxt = findViewById(R.id.edit_labels_txt);
        nameOfTag = findViewById(R.id.name_of_tag);
        emptyState = findViewById(R.id.empty_state);
        allNotesLinear = findViewById(R.id.all_notes_linear);
        emptyStateText = findViewById(R.id.empty_state_text);
        search = findViewById(R.id.search);
        mainScrollView = findViewById(R.id.scrollview);
        searchNotesTxt = findViewById(R.id.search_notes_txt);

        /*---------------------------------- load the animation---------------------------*/

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        /*---------------------------------start the animation------------------------------*/


        nameOfTag.startAnimation(animFadein);

    }

    private void clickListeneres() {

        expandedMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        allNotesLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        createNewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LabelCreation.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseItemData();
            }
        });

        cardAddNewLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LabelCreation.class);
                startActivity(intent);

            }
        });

        editlabelstxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LabelCreation.class);
                startActivity(intent);


            }
        });
        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newintent = new Intent(MainActivity.this, NotesPostActivity.class);

                newintent.putExtra("labelTag", tag);
                newintent.putExtra("labelkey", labelkey);

                startActivity(newintent);

                releaseItemData();

            }
        });
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (arePermissionsEnabled()) {
                        imageSelectionDialog();
                    } else {
                        requestMultiplePermissions();
                    }
                } else {
                    imageSelectionDialog();
                }

                releaseItemData();
            }
        });

        listOfText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenttxt = new Intent(MainActivity.this, CategoriesNotes.class);
                intenttxt.putExtra("textonly", "1");
                startActivity(intenttxt);
            }
        });
        listOfImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentIMGonly = new Intent(MainActivity.this, CategoriesNotes.class);
                intentIMGonly.putExtra("imagesonly", "2");
                startActivity(intentIMGonly);
            }
        });

        listOfAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentaudio = new Intent(MainActivity.this, CategoriesNotes.class);

                intentaudio.putExtra("audioonly", "3");

                startActivity(intentaudio);
            }
        });


        listOfTrashData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intenttrash = new Intent(MainActivity.this, TrashActivity.class);
                intenttrash.putExtra("trashdata", "4");
                startActivity(intenttrash);
            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < postModel.size(); i++) {
                    postModel.get(i).setSelected(true);
                }
                notesAdapter.notifyDataSetChanged();
            }
        });

        voiceRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    audioRecord();
                } else {
                    requestAudioPermission();
                }

                releaseItemData();

            }

        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardviewSearchNotes.setVisibility(View.VISIBLE);
                cardviewDrawer.setVisibility(View.GONE);
            }
        });

        searchNotesTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


        filter(searchNotesTxt.getText().toString());
    }

    private void labeledData() {
        nameOfTag.setText(tag);
        nameOfTag.setSingleLine(true);
        nameOfTag.setMaxLines(1);
        nameOfTag.setEllipsize(TextUtils.TruncateAt.END);

        query = fNoteDBreference.orderByChild("labelTag")
                .equalTo(tag);
        loadData();
    }


    private void releaseItemData() {

        cardviewDrawer.setVisibility(View.VISIBLE);
        cardviewSearchNotes.setVisibility(View.GONE);
        mainScrollView.fullScroll(ScrollView.FOCUS_UP);
        notesAdapter.isLongClickEnabled = false;
        releseItem();
        for (int i = 0; i < postModel.size(); i++) {
            postModel.get(i).setSelected(false);
        }
        notesAdapter.notifyDataSetChanged();

    }


    /*---------------------------------Search notes--------------------------------*/

    private void filter(String s) {

        ArrayList<NotePostModel> filteredlist = new ArrayList<>();
        for (NotePostModel model : postModel) {
            if ((model.getTitle() != null && model.getTitle().toLowerCase().contains(s.toLowerCase())) || (model.getContent() != null && model.getContent().toLowerCase().contains(s.toLowerCase()))) {
                filteredlist.add(model);
            }
        }
        notesAdapter.updateData(filteredlist);
    }


    /*----------------------------Open Gallery-------------------------------------*/


    private void openFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permissions[i])) {
                        new AlertDialog.Builder(this)
                                .setMessage("Needs Permissions")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestMultiplePermissions();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                    }
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bmp, "Title", null);
            Uri uri = Uri.parse(path);
            byte[] byteArray = stream.toByteArray();

            /*-------------------------stored captured image in external directory------------------------*/

            if (byteArray != null) {
                FileOutputStream outStream = null;
                String cameraPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();

                try {
                    outStream = new FileOutputStream(String.format(cameraPath + "/Camera/%d.jpg", System.currentTimeMillis()));
                    outStream.write(byteArray);
                    outStream.close();
                    //                    String pathimg = getRealPathFromURI(uri);

                    Intent intentIMG = new Intent(MainActivity.this, ImagePost.class);
                    intentIMG.putExtra("labelTag", tag);
                    intentIMG.putExtra("imageUri", uri);
                    intentIMG.putExtra("labelkey", labelkey);
                    startActivity(intentIMG);
                    Log.d("Log", "onPictureTaken - wrote bytes: " + byteArray.length);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(MainActivity.this);


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();

                Intent intentIMG = new Intent(MainActivity.this, ImagePost.class);
                intentIMG.putExtra("labelTag", tag);
                intentIMG.putExtra("imageUri", mImageUri);
                intentIMG.putExtra("labelkey", labelkey);
                startActivity(intentIMG);
            }
        }

    }

    /*------------------------------------record audio notes--------------------------*/

    private void audioRecord() {

        slideDialog = new Dialog(MainActivity.this, R.style.CustomDialogAnimation);
        slideDialog.setContentView(R.layout.dialog_record_speech);

        slideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        slideDialog.setCancelable(true);
        slideDialog.show();
        slideDialog.setCanceledOnTouchOutside(true);

        labelTag = slideDialog.findViewById(R.id.label_tag);
        recordingDone = slideDialog.findViewById(R.id.recording_completed);
        postEditedTime = slideDialog.findViewById(R.id.time_and_date_of_post);
        chronometer = slideDialog.findViewById(R.id.chronometerTimer);
        voiceNote = slideDialog.findViewById(R.id.voice_note);
        mButtonRecord = slideDialog.findViewById(R.id.record_btn);

        labelTag.setText(tag);

        if (tag != null) {
            labelTag.setVisibility(View.VISIBLE);
        } else {
            labelTag.setVisibility(View.GONE);
        }

        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy  hh:mm a");
        Date dateandtime = new Date();
        String strDate = dateFormat.format(dateandtime).toString();
        postEditedTime.setText("Edited " + strDate);

        fAuth = FirebaseAuth.getInstance();
        firebaseUser = fAuth.getCurrentUser();

        chronometer.setBase(SystemClock.elapsedRealtime());

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/" + UUID.randomUUID().toString() + ".mp3";

        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    isRecording = false;
                    stopRecording();
                    Toast.makeText(MainActivity.this, "Recording Stop.....", Toast.LENGTH_SHORT).show();

                } else {
                    isRecording = true;
                    startRecording();
                    Toast.makeText(MainActivity.this, "Recording Started.....", Toast.LENGTH_SHORT).show();

                }
            }
        });

        recordingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                uploadAudio();
                if (uri != null) {
                    stopRecording();
                    uploadAudio();
                } else {
                    Toast.makeText(MainActivity.this, "you have not make voice note", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    /*----------------------------------permissions method----------------------------*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestMultiplePermissions() {

        List<String> remainingPermissions = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                remainingPermissions.add(permission);
            }
        }
        requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean arePermissionsEnabled() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    private void requestAudioPermission() {
        ActivityCompat.requestPermissions((Activity) MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERM_REQ_CODE);
    }

    private boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    /*-----------------------------------captured image and pick image from gallery-----------------------*/

    private void imageSelectionDialog() {

        slideDialog = new Dialog(MainActivity.this, R.style.CustomDialogAnimation);
        slideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = slideDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        slideDialog.setContentView(R.layout.dialog_image_selection);
        slideDialog.getContext();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        slideDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
        layoutParams.copyFrom(slideDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        slideDialog.getWindow().setAttributes(layoutParams);
        slideDialog.setCancelable(true);
        slideDialog.setCanceledOnTouchOutside(true);
        slideDialog.show();

        takePhotoLinear = slideDialog.findViewById(R.id.take_photo_linear);
        chooseImgLinear = slideDialog.findViewById(R.id.choose_img_linear);

        takePhotoLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                slideDialog.dismiss();
                dispatchTakePictureIntent();
            }
        });

        chooseImgLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChoose();
                slideDialog.dismiss();

            }
        });

    }

    private void loadData() {

        /*----------------------Retrive all_notes---------------------*/


        postModel = new ArrayList<>();
        mNoteList = findViewById(R.id.main_notes_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        mNoteList.setLayoutManager(layoutManager);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        mNoteList.setItemAnimator(new DefaultItemAnimator());
        mStorageImage = FirebaseStorage.getInstance();

        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postModel.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (!dataSnapshot.exists()) {

                        emptyState.setVisibility(View.VISIBLE);
                        emptyStateText.setVisibility(View.VISIBLE);
                    } else {

                        emptyState.setVisibility(View.GONE);
                        emptyStateText.setVisibility(View.GONE);

                        NotePostModel model = snapshot.getValue(NotePostModel.class);
                        postModel.add(model);
                        notesAdapter.notifyDataSetChanged();

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        notesAdapter = new AllNotesAdapter(MainActivity.this, postModel, trashdata);
        mNoteList.setAdapter(notesAdapter);
        gridMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedData == false) {
                    gridMenu.setImageResource(R.drawable.ic_menu_grid);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                    mNoteList.setLayoutManager(layoutManager);
                    layoutManager.setReverseLayout(true);
                    selectedData = true;
                } else {
                    gridMenu.setImageResource(R.drawable.linear_view);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    mNoteList.setLayoutManager(staggeredGridLayoutManager);
                    selectedData = false;
                }
            }
        });
    }

    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    /*-----------------------------------open and close navigation drawer on onclick----------------*/


    private void createLabelDatas() {

        fLabelDBreference = FirebaseDatabase.getInstance().getReference().child("Labels").child(fAuth.getCurrentUser().getUid());
        editLabelModeLS = new ArrayList<>();
        mLabelList = findViewById(R.id.recyclerview_label);
        mLabelList.setNestedScrollingEnabled(false);

        mStorageLabels = FirebaseStorage.getInstance();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLabelList.setLayoutManager(layoutManager);

        fLabelDBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editLabelModeLS.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModifyLabelModeL model = snapshot.getValue(ModifyLabelModeL.class);

                    editLabelModeLS.add(model);
                    editLabelAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editLabelAdapter = new ModifyLabelAdapter(MainActivity.this, editLabelModeLS, false);
        mLabelList.setAdapter(editLabelAdapter);

    }

    public void setView() {

        cardviewDrawer.setVisibility(View.GONE);
        cardviewShowUpdates.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = postModel.size() - 1; i >= 0; i--) {
                    if (postModel.get(i).isSelected()) {

                        deleteSelectedData(i);
                        postModel.remove(i);


                        cardviewDrawer.setVisibility(View.VISIBLE);
                        cardviewShowUpdates.setVisibility(View.GONE);

                    }
                }

                notesAdapter.isLongClickEnabled = false;
                notesAdapter.notifyDataSetChanged();

                releseItem();

            }
        });
    }

    public void releseItem() {

        cardviewDrawer.setVisibility(View.VISIBLE);
        cardviewShowUpdates.setVisibility(View.GONE);

    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {

            Log.e(LOG_TAG, "prepare() failed");
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        uri = Uri.fromFile(new File(mFileName));

        mRecorder.start();
    }

    private void stopRecording() {

        try {
            uri = Uri.fromFile(new File(mFileName));
            mRecorder.stop();
            chronometer.stop();

        } catch (RuntimeException stopException) {
            //handle cleanup here
        }
    }

    private void uploadAudio() {

        /*----------------audio upload-------------------*/

        mProgress.setMessage("Uploading audio......");
        mProgress.show();
        final String audio = UUID.randomUUID().toString();
        final StorageReference filepath = mStorageReference.child("Audio");
        final Uri uri = Uri.fromFile(new File(mFileName));

        filepath.child(audio).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.child(audio).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);

                        String download = uri.toString();
                        String title = voiceNote.getText().toString();
                        String voicerecord = chronometer.getText().toString();
                        Long minutes = Long.valueOf(voicerecord.substring(0, 2));
                        Long seconds = Long.valueOf(voicerecord.substring(3, 5));
                        String key = recordDBreference.push().getKey();

                        DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy  hh:mm a");
                        Date dateandtime = new Date();
                        String strDate = dateFormat.format(dateandtime).toString();
                        postEditedTime.setText("Edited " + strDate);

                        NotePostModel model = new NotePostModel(title, null, key, null, download, minutes, seconds, null, "3", tag, strDate, labelkey, mFileName);

                        if (tag != null) {
                            labelTag.setVisibility(View.VISIBLE);
                        } else {
                            labelTag.setVisibility(View.GONE);
                        }

                        recordDBreference.child(key).setValue(model);

                        mProgress.dismiss();
//                        MainActivity.super.onBackPressed();

                        Toast.makeText(MainActivity.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void deleteSelectedData(int i) {
        fAuth = FirebaseAuth.getInstance();
        fNoteDBreference = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        final String dkey = postModel.get(i).getKey();

        fNoteDBreference.child(dkey).addListenerForSingleValueEvent(new ValueEventListener() {
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

        fNoteDBreference.child(dkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(MainActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);

    }

    private void logOut() {

        builder = new AlertDialog.Builder(MainActivity.this);

        /*-------------------------------Setting message manually and performing action on button click-------------------------------------------*/

        builder.setMessage("Are you sure you want to Log out")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fAuth.signOut();

                        finish();
                        Toast.makeText(getApplicationContext(), "you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, GoogleSignin.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        /*------------------------------- Action for 'NO' Button--------------------*/

                        dialog.cancel();
                        //                        drawerLayout.closeDrawer(navigationView);

                    }
                });

        /*------------------------Creating dialog box---------------------------------*/


        AlertDialog alert = builder.create();

        /*---------------------------------Setting the title manually------------------*/


        alert.setTitle("Log Out");
        alert.show();
    }


    /*--------------------mobile button backpress confirmation dialog--------------------*/

    @Override
    public void onBackPressed() {

        cardviewDrawer.setVisibility(View.VISIBLE);
        releaseItemData();

        if (!isAllNotes) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);

        } else {

            mainScrollView.fullScroll(ScrollView.FOCUS_UP);
            drawerLayout.closeDrawer(navigationView);

            /*--------Setting message manually and performing action on button click----------*/


            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton("No", null)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                            finish();
                            moveTaskToBack(true);

                            Intent start = new Intent(Intent.ACTION_MAIN);
                            start.addCategory(Intent.CATEGORY_HOME);
                            start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(start);
                        }
                    }).create().show();


            cardviewSearchNotes.setVisibility(View.GONE);
            cardviewDrawer.setVisibility(View.VISIBLE);
            searchNotesTxt.setText("");

        }

//        if (!isDrawerisOpen) {
//
//            Toast.makeText(this, "><><", Toast.LENGTH_SHORT).show();
//            drawerLayout.closeDrawer(navigationView);
//        } else {
//
//        }
    }

}
