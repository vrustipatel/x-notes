package e.vp.Xnotes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import adapter.AllNotesAdapter;
import model.NotePostModel;

public class CategoriesNotes extends AppCompatActivity {

    private RecyclerView recylerview;
    private AllNotesAdapter notesAdapter;
    private ArrayList<NotePostModel> postModel;

    private DatabaseReference fNoteDatabaseRef;
    private FirebaseStorage mStorageImage;
    FirebaseAuth fAuth;
    Query query;

    private String trashdata, selectinput, selectinputImages, selectinputAudio;
    private CardView cardviewShowUpdates, toolbar;
    private ImageView cancel, delete, emptyState, back;
    private TextView titleToolbar, selectAll, emptyStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_fragment);

        /*------------------------------Firebase tables--------------------------------*/

        fAuth = FirebaseAuth.getInstance();
        fNoteDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());
        mStorageImage = FirebaseStorage.getInstance();

        /*-------------------------------FindViewByIDs-------------------------------*/

        titleToolbar = findViewById(R.id.title_toolbar);
        toolbar = findViewById(R.id.toolbar);
        back = findViewById(R.id.back);
        cardviewShowUpdates = findViewById(R.id.cardview_show_updates);
        delete = findViewById(R.id.delete_all);
        cancel = findViewById(R.id.cancel);
        emptyState = findViewById(R.id.empty_state);
        emptyStateText = findViewById(R.id.empty_state_text);
        selectAll = findViewById(R.id.select_all);

        /*--------------------------------get intent-------------------------------*/


        Intent intent = getIntent();
        selectinput = intent.getStringExtra("textonly");
        selectinputImages = intent.getStringExtra("imagesonly");
        selectinputAudio = intent.getStringExtra("audioonly");

        if (selectinput != null) {

            /*-------------------display descriptive data---------------------*/

            emptyState.setImageResource(R.drawable.empty_state_for_notes);
            emptyStateText.setText("There is no notes");
            query = fNoteDatabaseRef.orderByChild("selectinput")
                    .equalTo("1");
            titleToolbar.setText("Notes");

            getBack();

        } else if (selectinputImages != null) {

            /*---------------display only images----------------*/

            emptyState.setImageResource(R.drawable.empty_images);
            emptyStateText.setText("There is no Images");

            query = fNoteDatabaseRef.orderByChild("selectinput")
                    .equalTo("2");

            titleToolbar.setText("Images");
            getBack();
        } else if (selectinputAudio != null) {

            /*----------------------display only voice notes--------------*/

            emptyState.setImageResource(R.drawable.audio_bg);
            emptyStateText.setText("There is no Audio");

            query = fNoteDatabaseRef.orderByChild("selectinput")
                    .equalTo("3");
            titleToolbar.setText("Audio");
            getBack();
        }

        displayCategorywiseData();

    }
    /*-----------------------------------it shows category wise data----------------------------*/

    private void displayCategorywiseData() {
        postModel = new ArrayList<>();
        recylerview = findViewById(R.id.recyclerview_fragment);
        recylerview.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CategoriesNotes.this, LinearLayoutManager.VERTICAL, false);
        recylerview.setLayoutManager(layoutManager);

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
                Toast.makeText(CategoriesNotes.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        notesAdapter = new AllNotesAdapter(CategoriesNotes.this, postModel, trashdata);
        recylerview.setAdapter(notesAdapter);

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

        /*-------------------------------------select all items--------------------------------*/


        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < postModel.size(); i++) {
                    postModel.get(i).setSelected(true);
                }
                notesAdapter.notifyDataSetChanged();
            }
        });

    }

    private void deleteSelectedData(int i) {
        fAuth = FirebaseAuth.getInstance();
        fNoteDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(fAuth.getCurrentUser().getUid());

        final String dkey = postModel.get(i).getKey();

        fNoteDatabaseRef.child(dkey).addListenerForSingleValueEvent(new ValueEventListener() {
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

        fNoteDatabaseRef.child(dkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(CategoriesNotes.this, "Error:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getBack() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*----------------------------------------release Toolbar-------------------------------------*/

    public void releseItem() {

        toolbar.setVisibility(View.VISIBLE);
        cardviewShowUpdates.setVisibility(View.GONE);

    }

    /*-----------------------------------------display delete item toolbar--------------------------*/


    public void setView() {

        toolbar.setVisibility(View.GONE);
        cardviewShowUpdates.setVisibility(View.VISIBLE);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = postModel.size() - 1; i >= 0; i--) {
                    if (postModel.get(i).isSelected()) {

                        deleteSelectedData(i);
                        postModel.remove(i);

                        toolbar.setVisibility(View.VISIBLE);
                        cardviewShowUpdates.setVisibility(View.GONE);

                    }
                }
                notesAdapter.isLongClickEnabled = false;
                notesAdapter.notifyDataSetChanged();

                releseItem();

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
