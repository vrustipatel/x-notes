package e.wolfsoft1.Xnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import adapter.AllNotesAdapter;
import model.NotePostModel;

public class TrashActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    private DatabaseReference trashDBreference;

    private RecyclerView mTrashDataList;
    private AllNotesAdapter trashAdapter;
    private ArrayList<NotePostModel> trashDataModel;
    ImageView back, emptyState;
    TextView titleToolbar, emptyStateText;
    String trashdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        /*--------------------------------Firebase tabels-------------------------------*/

        fAuth = FirebaseAuth.getInstance();
        trashDBreference = FirebaseDatabase.getInstance().getReference().child("TrashData").child(fAuth.getCurrentUser().getUid());

        trashdata = getIntent().getStringExtra("trashdata");

        titleToolbar = findViewById(R.id.title_toolbar);
        emptyStateText = findViewById(R.id.empty_state_text);
        emptyState = findViewById(R.id.empty_state);
        back = findViewById(R.id.back);
        titleToolbar.setText("Trash");


        getBack();
        loadData();
    }

    /*------------------------------Display deleted items-------------------------*/

    private void loadData() {

        trashDataModel = new ArrayList<>();
        mTrashDataList = findViewById(R.id.trash_data_recy);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TrashActivity.this, LinearLayoutManager.VERTICAL, false);
        mTrashDataList.setLayoutManager(layoutManager);

        trashDBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trashDataModel.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (!dataSnapshot.exists()) {
                        emptyStateText.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.VISIBLE);

                    } else {

                        emptyStateText.setVisibility(View.GONE);
                        emptyState.setVisibility(View.GONE);

                        NotePostModel model = snapshot.getValue(NotePostModel.class);
                        trashDataModel.add(model);
                        trashAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TrashActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        trashAdapter = new AllNotesAdapter(TrashActivity.this, trashDataModel, trashdata);
        mTrashDataList.setAdapter(trashAdapter);
    }

    private void getBack() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
