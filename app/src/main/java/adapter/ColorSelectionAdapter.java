package adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import e.vp.Xnotes.ImagePost;
import e.vp.Xnotes.NotesPostActivity;
import e.vp.Xnotes.R;
import model.ColorSelectionModel;

public class ColorSelectionAdapter extends RecyclerView.Adapter<ColorSelectionAdapter.ViewHolder> {

    Context context;
    ArrayList<ColorSelectionModel> colorModels;
    private int myPos = 0;

    public ColorSelectionAdapter(Context context, ArrayList<ColorSelectionModel> colorModels) {
        this.context = context;
        this.colorModels = colorModels;
    }

    @NonNull
    @Override
    public ColorSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_color_selection, viewGroup, false);

        return new ColorSelectionAdapter.ViewHolder(view, i);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.selectColorImg.setColorFilter(Color.parseColor(colorModels.get(i).getSelectColorImg()));

        //perform operation on particular item

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myPos = i;
                ColorFilter color = viewHolder.selectColorImg.getColorFilter();

                //create instance of multiple activities.
                if (context instanceof NotesPostActivity) {
                    ((NotesPostActivity) context).colorMethod(color, colorModels.get(i));
                } else if (context instanceof ImagePost) {

                    ((ImagePost) context).colorMethod(color, colorModels.get(i));
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return colorModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView selectColorImg;

        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            selectColorImg = itemView.findViewById(R.id.select_color_img);

        }
    }
}