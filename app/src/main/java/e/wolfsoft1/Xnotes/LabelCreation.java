package e.wolfsoft1.Xnotes;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import adapter.ModifyLabelAdapter;
import model.ModifyLabelModeL;

public class LabelCreation extends AppCompatActivity {

    private DatabaseReference fNoteDBReference;
    private FirebaseStorage mStorageImage;
    FirebaseAuth fAuth;

    private RecyclerView mLabelList;
    private GridLayoutManager gridLayoutManager;
    private ModifyLabelAdapter editLabelAdapter;
    private ArrayList<ModifyLabelModeL> editLabelModeLS;

    ImageView done, close;
    EditText editLabels;
    ImageView back;
    private Query query;
    private String labelCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_labels);

        /*-------------------------------------Firebase Tabels---------------------------------*/

        fAuth = FirebaseAuth.getInstance();
        fNoteDBReference = FirebaseDatabase.getInstance().getReference().child("Labels").child(fAuth.getCurrentUser().getUid());

        done = findViewById(R.id.done);
        editLabels = findViewById(R.id.edit_labels);
        close = findViewById(R.id.close);
        back = findViewById(R.id.back);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editLabels.getText().toString().isEmpty()) {

                    createLabels();
                } else {
                    Toast.makeText(LabelCreation.this, "Create a new label", Toast.LENGTH_SHORT).show();
                }
                editLabels.setText(null);


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LoadCreatedLabelDatas();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        editLabels.setMaxLines(2);

        if (editLabels.length() < 1 ||
                editLabels.length() > 70) {

            editLabels.setError("Label should be between 1 and 100 characters in length");

            editLabels.requestFocus();
        } else {
            String text = editLabels.getText().toString();
        }

    }


    /*-----------------------------create a new label---------------------------------*/


    private void LoadCreatedLabelDatas() {

        editLabelModeLS = new ArrayList<>();
        mLabelList = findViewById(R.id.edit_labels_recyclerview);

        mStorageImage = FirebaseStorage.getInstance();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLabelList.setLayoutManager(layoutManager);


        fNoteDBReference.addValueEventListener(new ValueEventListener() {
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
                Toast.makeText(LabelCreation.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        editLabelAdapter = new ModifyLabelAdapter(LabelCreation.this, editLabelModeLS, true);
        mLabelList.setAdapter(editLabelAdapter);

    }

    private void createLabels() {

        final String key = fNoteDBReference.push().getKey();
        final String title = editLabels.getText().toString().trim();

        final ModifyLabelModeL modeL = new ModifyLabelModeL(title, key);

        fNoteDBReference.orderByChild("labelCreated").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    fNoteDBReference.child(key).setValue(modeL);

                } else {

                    Toast.makeText(LabelCreation.this, "label is already defined", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

