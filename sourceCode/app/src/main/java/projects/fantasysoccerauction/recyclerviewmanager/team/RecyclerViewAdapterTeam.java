package projects.fantasysoccerauction.recyclerviewmanager.team;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import projects.fantasysoccerauction.R;
import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.PlayerInTeam;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Team;

import static projects.fantasysoccerauction.LoginActivity.TEAM_ID;

public class RecyclerViewAdapterTeam extends RecyclerView.Adapter<ViewHolderTeam> {
    private List<DataItemTeam> list;
    private Context context;
    private String userIdentifier;

    public RecyclerViewAdapterTeam(List<DataItemTeam> list, Context context, String userIdentifier) {
        this.list = list;
        this.context = context;
        this.userIdentifier = userIdentifier;
    }

    @NonNull
    @Override
    public ViewHolderTeam onCreateViewHolder(ViewGroup parent, int viewType) {
        // initialize the ViewHolder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cv_team_layout, parent, false);
        ViewHolderTeam holder = new ViewHolderTeam(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderTeam holder, int position) {
        String name = list.get(position).name;
        String value = list.get(position).value;
        String role = list.get(position).role;
        String logoPath = "logo_" + list.get(position).team.toLowerCase();
        Integer playerId = list.get(position).id;

        holder.tv_name.setText(name);
        holder.tv_value.setText(value);
        holder.tv_role.setText(role);
        holder.iv_logo.setImageResource(context.getResources().getIdentifier(logoPath, "drawable", context.getPackageName()));

        holder.cv.setOnLongClickListener(v -> {
            showAlert(context, DatabaseHelper.getInstance(context), position, userIdentifier, playerId, TEAM_ID);
            return true;
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

    // Remove the RecyclerView item in the specified position
    private void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    // todo: delete from the database
    private boolean removeFromTeam(SQLiteDatabase db, int position, String username, Integer playerId, Integer teamId) {
        boolean canceled = false;
        try {
            db.execSQL("DELETE FROM " + PlayerInTeam.TABLE_NAME + " WHERE " +
                    PlayerInTeam.COLUMN_NAME_PLAYER_ID + " = " + playerId + " AND " +
                    PlayerInTeam.COLUMN_NAME_TEAM_ID + " = " + teamId + " AND " +
                    teamId + " = " + "(SELECT " + Team._ID + " FROM " + Team.TABLE_NAME + " WHERE " +
                    Team.COLUMN_NAME_OWNER + " = '" + username + "')");
            remove(position);
            canceled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canceled;
    }

    private void showAlert(Context context, SQLiteDatabase db, int position, String username, Integer playerId, Integer teamId) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(context.getString(R.string.rv_adapter_team_a_title))
                .setMessage(context.getString(R.string.rv_adapter_team_a_question))
                .setPositiveButton(context.getString(R.string.rv_adapter_team_a_yes), (dialog, which) -> {
                    if (removeFromTeam(db, position, username, playerId, teamId))
                        Toast.makeText(context, R.string.rv_adapter_team_a_toast_deleted, Toast.LENGTH_SHORT).show();
                    //remove(position);
                })
                .setNegativeButton(context.getString(R.string.rv_adapter_team_a_no), null)
                .show();
    }
}
