package projects.fantasysoccerauction;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Player;
import projects.fantasysoccerauction.database.DatabaseNomenclature.PlayerInTeam;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Team;
import projects.fantasysoccerauction.recyclerviewmanager.team.DataItemTeam;
import projects.fantasysoccerauction.recyclerviewmanager.team.RecyclerViewAdapterTeam;

public class TeamActivity extends AppCompatActivity {

    private String userIdentifier;

    // attributes: id, role, name, team, value
    List<DataItemTeam> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        // get the username from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(LoginActivity.USER_IDENTIFIER_EXTRA);
        }

        Integer teamId = null;
        try {
            teamId = retrieveTeamId(DatabaseHelper.getInstance(this), userIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load data from the database to the cards
        Cursor c = retrievePlayersInTeam(DatabaseHelper.getInstance(this), teamId, userIdentifier);
        if (c != null) {
            while (c.moveToNext()) {
                data.add(new DataItemTeam(c.getInt(c.getColumnIndex(Player._ID)),
                        c.getString(c.getColumnIndex(Player.COLUMN_NAME_ROLE)),
                        c.getString(c.getColumnIndex(Player.COLUMN_NAME_NAME)),
                        c.getString(c.getColumnIndex(Player.COLUMN_NAME_TEAM)),
                        c.getString(c.getColumnIndex(Player.COLUMN_NAME_VALUE))));
            }
            c.close();
        }

        RecyclerView rv = findViewById(R.id.a_team_rv);
        RecyclerViewAdapterTeam adapter = new RecyclerViewAdapterTeam(data, TeamActivity.this, userIdentifier);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }

    private Cursor retrievePlayersInTeam(SQLiteDatabase db, Integer teamId, String userIdentifier) {
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT p." + Player._ID + ", " +
                    "p." + Player.COLUMN_NAME_ROLE + ", " +
                    "p." + Player.COLUMN_NAME_NAME + ", " +
                    "p." + Player.COLUMN_NAME_VALUE + ", " +
                    "p." + Player.COLUMN_NAME_TEAM + " " +
                    "FROM " + Team.TABLE_NAME + " as t, " +
                    Player.TABLE_NAME + " as p, " +
                    PlayerInTeam.TABLE_NAME + " as pit " +
                    "WHERE p." + Player._ID + " = pit." + PlayerInTeam.COLUMN_NAME_PLAYER_ID + " and " +
                    "t." + Team._ID + " = pit." + PlayerInTeam.COLUMN_NAME_TEAM_ID + " and " +
                    "t." + Team.COLUMN_NAME_OWNER + " = '" + userIdentifier + "' and " +
                    "t." + Team._ID + " = " + teamId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private Integer retrieveTeamId(SQLiteDatabase db, String userId) {
        String[] projection = {Team._ID};

        String selection = Team.COLUMN_NAME_OWNER + " = ?";
        String[] selectionArgs = {userId};

        Cursor c = db.query(
                Team.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        Integer res = null;
        if (c.moveToFirst()) {
            res = c.getInt(c.getColumnIndex(Team._ID));
        }
        c.close();
        return res;
    }
}
