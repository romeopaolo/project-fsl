package projects.fantasysoccerauction.recyclerviewmanager.auction;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import projects.fantasysoccerauction.FocusViewActivity;
import projects.fantasysoccerauction.R;

import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;

public class RecyclerViewAdapterAwardedPlayer extends RecyclerView.Adapter<ViewHolderAwardedPlayer> {
    private List<DataItemAwardedPlayer> list;
    private Context context;
    private String userIdentifier;

    public RecyclerViewAdapterAwardedPlayer(List<DataItemAwardedPlayer> list, Context context, String userIdentifier) {
        this.list = list;
        this.context = context;
        this.userIdentifier = userIdentifier;
    }

    @NonNull
    @Override
    public ViewHolderAwardedPlayer onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the ViewHolderPlayersList
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cv_players_list_layout, parent, false);
        ViewHolderAwardedPlayer holder = new ViewHolderAwardedPlayer(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderAwardedPlayer holder, int position) {
        String name = list.get(position).name;
        String value = list.get(position).value;
        String role = list.get(position).role;
        String picturePath = "player_photo_" + list.get(position).name.replace("'", "_").replace("-", "_").replace(" ", "_").toLowerCase();
        String logoPath = "logo_" + list.get(position).team.toLowerCase();

        holder.tv_name.setText(name);
        holder.tv_value.setText(value);
        holder.tv_role.setText(role);
        holder.iv_photo.setImageResource(context.getResources().getIdentifier(picturePath, "drawable", context.getPackageName()));
        holder.iv_logo.setImageResource(context.getResources().getIdentifier(logoPath, "drawable", context.getPackageName()));

        holder.cv.setOnClickListener(v -> {
            Intent intent = new Intent(context, FocusViewActivity.class);
            intent.putExtra(USER_IDENTIFIER_EXTRA, userIdentifier);
            intent.putExtra("playerId", list.get(position).id);
            intent.putExtra("picture", picturePath);
            intent.putExtra("logo", logoPath);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("value", value);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position TODO: remove if not useful
    public void insert(int position, DataItemAwardedPlayer data) {
        list.add(position, data);
        notifyItemInserted(position);
    }
}
