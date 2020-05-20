package projects.fantasysoccerauction.recyclerviewmanager.team;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import projects.fantasysoccerauction.R;

public class ViewHolderTeam extends RecyclerView.ViewHolder {

    CardView cv;
    TextView tv_name;
    TextView tv_value;
    TextView tv_role;
    ImageView iv_logo;


    public ViewHolderTeam(@NonNull View itemView) {
        super(itemView);
        cv = itemView.findViewById(R.id.a_team_cv_player);
        tv_name = itemView.findViewById(R.id.row_cv_team_name);
        tv_value = itemView.findViewById(R.id.row_cv_team_value);
        tv_role = itemView.findViewById(R.id.row_cv_team_role);
        iv_logo = itemView.findViewById(R.id.row_cv_team_logo);
    }
}
