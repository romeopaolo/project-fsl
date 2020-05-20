package projects.fantasysoccerauction.recyclerviewmanager.playerslist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import projects.fantasysoccerauction.R;

public class ViewHolderPlayersList extends RecyclerView.ViewHolder {

    CardView cv;
    TextView tv_name;
    TextView tv_value;
    TextView tv_role;
    ImageView iv_photo;
    ImageView iv_logo;


    public ViewHolderPlayersList(@NonNull View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.a_players_list_cv_player);
        tv_name = itemView.findViewById(R.id.row_cv_players_list_player_name);
        tv_value = itemView.findViewById(R.id.row_cv_players_list_player_value);
        tv_role = itemView.findViewById(R.id.row_cv_players_list_player_role);
        iv_photo = itemView.findViewById(R.id.row_cv_players_list_player_photo);
        iv_logo = itemView.findViewById(R.id.row_cv_players_list_team_logo);
    }
}
