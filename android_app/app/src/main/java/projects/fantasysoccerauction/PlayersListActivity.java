package projects.fantasysoccerauction;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Player;
import projects.fantasysoccerauction.recyclerviewmanager.playerslist.DataItemPlayersList;
import projects.fantasysoccerauction.recyclerviewmanager.playerslist.RecyclerViewAdapterPlayersList;

import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;


public class PlayersListActivity extends AppCompatActivity {

    private String userIdentifier;
    List<DataItemPlayersList> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        // get the username from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(USER_IDENTIFIER_EXTRA);
        }

        // load data from the database to the cards
        loadPlayers(DatabaseHelper.getInstance(this), data);

        RecyclerView rv = findViewById(R.id.a_players_list_rv);
        RecyclerViewAdapterPlayersList adapter = new RecyclerViewAdapterPlayersList(data, PlayersListActivity.this, userIdentifier);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        CheckBox cb_gk = findViewById(R.id.a_players_list_cb_filter_goalkeeper);
        CheckBox cb_df = findViewById(R.id.a_players_list_cb_filter_defender);
        CheckBox cb_mf = findViewById(R.id.a_players_list_cb_filter_midfielder);
        CheckBox cb_fw = findViewById(R.id.a_players_list_cb_filter_forwarder);
        cb_gk.setOnClickListener(v -> filterPlayers(data, cb_gk, cb_df, cb_mf, cb_fw, rv));
        cb_df.setOnClickListener(v -> filterPlayers(data, cb_gk, cb_df, cb_mf, cb_fw, rv));
        cb_mf.setOnClickListener(v -> filterPlayers(data, cb_gk, cb_df, cb_mf, cb_fw, rv));
        cb_fw.setOnClickListener(v -> filterPlayers(data, cb_gk, cb_df, cb_mf, cb_fw, rv));
    }

    private void filterPlayers(List<DataItemPlayersList> data, CheckBox cb_gk, CheckBox cb_df, CheckBox cb_mf, CheckBox cb_fw, RecyclerView rv) {
        RecyclerViewAdapterPlayersList adapter;
        if (cb_gk.isChecked() || cb_df.isChecked() || cb_mf.isChecked() || cb_fw.isChecked()) {
            adapter = new RecyclerViewAdapterPlayersList(filterData(data, cb_gk, cb_df, cb_mf, cb_fw), PlayersListActivity.this, userIdentifier);

        } else {
            adapter = new RecyclerViewAdapterPlayersList(data, PlayersListActivity.this, userIdentifier);
        }
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(PlayersListActivity.this));
    }

    private List<DataItemPlayersList> filterData(List<DataItemPlayersList> data, CheckBox cb_gk, CheckBox cb_df, CheckBox cb_mf, CheckBox cb_fw) {
        List<DataItemPlayersList> tempData = new ArrayList<>();

        // todo: modify to allow filter in different languages
        if (cb_gk.isChecked()) {
            tempData.addAll(data.stream().filter(p -> p.getRole().equals("P")).collect(Collectors.toList()));
        }
        if (cb_df.isChecked()) {
            tempData.addAll(data.stream().filter(p -> p.getRole().equals("D")).collect(Collectors.toList()));
        }
        if (cb_mf.isChecked()) {
            tempData.addAll(data.stream().filter(p -> p.getRole().equals("C")).collect(Collectors.toList()));
        }
        if (cb_fw.isChecked()) {
            tempData.addAll(data.stream().filter(p -> p.getRole().equals("A")).collect(Collectors.toList()));
        }

        return tempData;
    }

    private void loadPlayers(SQLiteDatabase db, List<DataItemPlayersList> data) {
        String sortOrder = Player.COLUMN_NAME_NAME + " ASC";

        Cursor c = db.query(
                Player.TABLE_NAME,   // The table to query
                null,             // get all columns
                null,
                null,
                null,
                null,
                sortOrder              // order by name
        );

        // add data to the list
        while (c.moveToNext()) {
            data.add(new DataItemPlayersList(
                    c.getInt(c.getColumnIndex(Player._ID)),
                    c.getString(c.getColumnIndex(Player.COLUMN_NAME_ROLE)),
                    c.getString(c.getColumnIndex(Player.COLUMN_NAME_NAME)),
                    c.getString(c.getColumnIndex(Player.COLUMN_NAME_TEAM)),
                    c.getString(c.getColumnIndex(Player.COLUMN_NAME_VALUE))));
        }
        c.close();
    }

}
