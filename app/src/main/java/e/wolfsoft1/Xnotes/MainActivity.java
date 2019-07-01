package e.wolfsoft1.Xnotes;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.ModifyLabelAdapter;
import adapter.AllNotesAdapter;
import model.ModifyLabelModeL;
import model.NotePostModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //variable initialization
    private static final int PERM_REQ_CODE = 23;
    FirebaseAuth fAuth;
    private FirebaseStorage mStorageImage, mStorageLabels;
    private DatabaseReference fNoteDBreference, fLabelDBreference;
    private StorageReference mStorageReference, filepath;
    FirebaseUser firebaseUser;
    ItemTouchHelper touchHelper;
    private ProgressDialog mProgress;

    private RecyclerView mNoteList;
    private GridLayoutManager gridLayoutManager;
    private AllNotesAdapter notesAdapter;
    private ArrayList<NotePostModel> postModel;

    private ModifyLabelAdapter editLabelAdapter;
    private RecyclerView mLabelList;
    private ArrayList<ModifyLabelModeL> editLabelModeLS;
    int inc;
    String labelkey;

    ImageView profileImg, profileDrawer;
    CardView cardAddNewLabel;
    Button done;
    String trashdata;
    ImageView chooseImage, voiceRecorder;
    TextView addNotes, editlabelstxt;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ImageView expandedMenu;
    private ImageView delete, createNewLabel, gridMenu, cancel;
    private CardView cardviewDrawer, cardviewShowUpdates, cardviewSearchNotes;
    Boolean selectedData = false;
    TextView inputName, inputEmail;
    LinearLayout listOfText, listOfTrashData, listOfAudios, listOfImages;
    TextView nameOfTag, labelTag;
    private ImageButton mButtonRecord;
    private Chronometer chronometer;
    private String mFileName = null;
    private MediaRecorder mRecorder;
    private static final String LOG_TAG = "Record_log";
    private String key, title, record, voice;
    EditText voiceNote;
    private DatabaseReference recordDBreference;
    TextView selectAll;
    TextView postEditedTime;
    private Dialog slideDialog;
    private String tag;
    private Query query, queryPosition;
    Animation animFadein;
    private AlertDialog.Builder builder;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    LinearLayout allNotesLinear;
    boolean doubleBackToExitPressedOnce = false;
    Button recordingDone;
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    //    private String[] permission = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW", "android.permission.CAMERA"};
    int i;
    Uri uri;
    LinearLayout takePhotoLinear, chooseImgLinear;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Snackbar snackbar;
    private boolean isOnClicked = false;
    private Uri mImageUri;
    TextView logouttxt, emptyStateText;
    private String keyimg;
    ImageView emptyState;
    private String itemPosition;
    ImageView search;
    EditText searchNotesTxt;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*------------------------------------casting of variables--------------------------------*/


        initializeMethod();

        /*-------------------------------------load Data-------------------------------------------*/

        mProgress = new ProgressDialog(MainActivity.this);
        String progress = getIntent().getStringExtra("progress");

        labelkey = getIntent().getStringExtra("labelkey");

        if (progress != null) {
            mProgress.show();
            mProgress.setMessage("uploading image");
        } else {
            mProgress.dismiss();
        }

        /*----------------------------------get labelwise data like folder---------------------------*/

        if (tag != null) {
            nameOfTag.setText(tag);
            nameOfTag.setSingleLine(true);
            nameOfTag.setMaxLines(1);
            nameOfTag.setEllipsize(TextUtils.TruncateAt.END);

            query = fNoteDBreference.orderByChild("labelTag")
                    .equalTo(tag);
            loadData();
        } else {
            query = fNoteDBreference;
            loadData();
        }

        /*----------------------------------------get gmail Id,username & user profile -----------------------------------*/

        inputEmail.setText(fAuth.getCurrentUser().getEmail());
        inputName.setText(fAuth.getCurrentUser().getDisplayName());
        Picasso.with(MainActivity.this).load(fAuth.getCurrentUser().getPhotoUrl()).placeholder(R.drawable.profile).into(profileDrawer);

        /*--------------------------------------check RunTime Permission-----------------------------------*/

        if (arePermissionsEnabled()) {
        } else {
//            permissions = new String[]{Manifest.permission.CAMERA};
            requestMultiplePermissions();
        }

        if (checkStoragePermission()) {
//            audioRecord();
        } else {
            requestAudioPermission();
//            audioRecord();
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

    private void clickListeneres() {

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

                notesAdapter.isLongClickEnabled = false;
                releseItem();
                for (int i = 0; i < postModel.size(); i++) {
                    postModel.get(i).setSelected(false);
                }
                notesAdapter.notifyDataSetChanged();
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


            }
        });
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (arePermissionsEnabled()) {
                        imageSelectionDialog();
                    } else {
                        //            permissions = new String[]{Manifest.permission.CAMERA};
                        requestMultiplePermissions();
                    }
                } else {
                    imageSelectionDialog();
                }
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
            }

        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardviewSearchNotes.setVisibility(View.VISIBLE);
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

    /*----------------------------Open Gallery-------------------------------------*/


    private void openFileChoose() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }

    /*---------------------------------Search notes--------------------------------*/


    private void filter(String s) {

//        Pattern pattern = Pattern.compile(s.toLowerCase());
//        Matcher matcher = pattern.matcher(s.toLowerCase());
//        SpannableString spannableString = new SpannableString(title);

        ArrayList<NotePostModel> filteredlist = new ArrayList<>();
        for (NotePostModel model : postModel) {
            if (model.getTitle() != null && model.getTitle().toLowerCase().contains(s.toLowerCase())) {

                filteredlist.add(model);
            }

        }
        notesAdapter.updateData(filteredlist);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 101) {
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            mImageUri = data.getData();

            Picasso.with(MainActivity.this)
                    .load(mImageUri);

            Intent intentIMG = new Intent(MainActivity.this, ImagePost.class);
            intentIMG.putExtra("labelTag", tag);
            intentIMG.putExtra("imageUri", mImageUri);
            intentIMG.putExtra("labelkey", labelkey);

            startActivity(intentIMG);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
//        profileImg = findViewById(R.id.profile_img);
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
        searchNotesTxt = findViewById(R.id.search_notes_txt);

        expandedMenu.setOnClickListener(this);

        /*---------------------------------- load the animation---------------------------*/

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        /*---------------------------------start the animation------------------------------*/


        nameOfTag.startAnimation(animFadein);

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
                        drawerLayout.closeDrawer(navigationView);

                    }
                });

        /*------------------------Creating dialog box---------------------------------*/


        AlertDialog alert = builder.create();

        /*---------------------------------Setting the title manually------------------*/


        alert.setTitle("Log Out");
        alert.show();

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

        Intent intent = getIntent();

        key = intent.getStringExtra("key");
        title = intent.getStringExtra("title");
        tag = intent.getStringExtra("labelTag");
        voice = intent.getStringExtra("voice");
        record = intent.getStringExtra("record");

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
        mFileName += "/recorded_audio.mp3";

        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOnClicked = true;
            }
        });


        mButtonRecord.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (isOnClicked == false) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    Toast.makeText(MainActivity.this, "Recording Started.....", Toast.LENGTH_SHORT).show();
//                    mRecordLable.setText("Recording Started.....");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    Toast.makeText(MainActivity.this, "Recording Stop.....", Toast.LENGTH_SHORT).show();

                }

                return false;
            }

        });

        recordingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadAudio();
                if (uri != null) {
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
        gridLayoutManager = new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.VERTICAL, false);
        mNoteList.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setReverseLayout(true);
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
                    gridLayoutManager = new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.VERTICAL, true);
                    mNoteList.setLayoutManager(gridLayoutManager);
                    gridLayoutManager.setReverseLayout(true);
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

    @Override
    public void onClick(View v) {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

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
                for (int i = 0; i < postModel.size(); i++) {
                    if (postModel.get(i).isSelected()) {

                    }
                }

                notesAdapter.notifyDataSetChanged();
                notesAdapter.deleteSelectedData();
                cardviewDrawer.setVisibility(View.VISIBLE);
                cardviewShowUpdates.setVisibility(View.GONE);
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
                        mProgress.dismiss();
                        Toast.makeText(MainActivity.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();

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

                        NotePostModel model = new NotePostModel(title, null, key, null, download, minutes, seconds, null, "3", tag, strDate, labelkey, null, null);

                        if (tag != null) {
                            labelTag.setVisibility(View.VISIBLE);
                        } else {
                            labelTag.setVisibility(View.GONE);
                        }


                        recordDBreference.child(key).setValue(model);

                        Toast.makeText(MainActivity.this, "Uploading Finished...", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });
    }

    public void deleteAllData() {

        fNoteDBreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotePostModel model = snapshot.getValue(NotePostModel.class);

                    DatabaseReference trashReference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(fAuth.getCurrentUser().getUid());
                    String Dkey = trashReference.push().getKey();
                    trashReference.child(Dkey).setValue(model);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fNoteDBreference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(MainActivity.this, "Notes deleted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(MainActivity.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    /*--------------------mobile button backpress confirmation dialog--------------------*/

    @Override
    public void onBackPressed() {

        drawerLayout.closeDrawer(navigationView);
//            builder = new AlertDialog.Builder(this);


        /*--------Setting message manually and performing action on button click----------*/

        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        System.exit(0);
                    }
                }).create().show();

        cardviewSearchNotes.setVisibility(View.GONE);
        cardviewDrawer.setVisibility(View.VISIBLE);
        searchNotesTxt.setText("");


    }

    public void updateSomething(String tag, String key) {
        labelkey = key;

        if (tag != null) {
            nameOfTag.setText(tag);
            nameOfTag.setSingleLine(true);
            nameOfTag.setMaxLines(1);
            nameOfTag.setEllipsize(TextUtils.TruncateAt.END);

            query = fNoteDBreference.orderByChild("labelTag")
                    .equalTo(tag);
            loadData();
        } else {
            query = fNoteDBreference;
            loadData();
        }
    }
}
