package projects.fantasysoccerauction;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.PlayerInTeam;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Team;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;

import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;

public class FocusViewActivity extends AppCompatActivity {

    private static final String SERVER_URL_STATISTICS = "https://fantasy-soccer-auction.herokuapp.com/statistics/";
    private String userIdentifier;
    private static int DEFAULT_ID = 1; // mandatory id for SQLite table PlayerInTeam

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_view);


        // get references
        ImageView iv_picture = findViewById(R.id.a_focus_view_iv_picture);
        ImageView iv_logo = findViewById(R.id.a_focus_view_iv_logo);
        TextView tv_name = findViewById(R.id.a_focus_view_tv_name);
        TextView tv_value = findViewById(R.id.a_focus_view_tv_value_number);
        TextView tv_role = findViewById(R.id.a_focus_view_tv_role);
        TextView tv_statistics = findViewById(R.id.a_focus_view_tv_statistics_data);
        CheckBox cb = findViewById(R.id.a_focus_view_cb);

        Integer playerId = null;

        // get the data from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(USER_IDENTIFIER_EXTRA);
            iv_picture.setImageResource(this.getResources().getIdentifier(extras.getString("picture"), "drawable", getPackageName()));
            iv_logo.setImageResource(this.getResources().getIdentifier(extras.getString("logo"), "drawable", getPackageName()));
            tv_name.setText(extras.getString("name"));
            tv_value.setText(extras.getString("value"));
            tv_role.setText(extras.getString("role"));
            playerId = extras.getInt("playerId");
            if (checkIfAlreadyInTeam(DatabaseHelper.getInstance(this), userIdentifier, playerId)) {
                cb.setChecked(true);
            }
        } else {
            Toast.makeText(this, R.string.a_focus_view_t_data, Toast.LENGTH_LONG).show();
        }

        // save/remove the player in/from the team
        Integer finalPlayerId = playerId;
        Integer teamId = null;
        try {
            teamId = retrieveTeamId(DatabaseHelper.getInstance(this), userIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Integer finalTeamId = teamId;
        cb.setOnClickListener(v -> {
            if (cb.isChecked() && finalTeamId != null) {
                boolean res = saveNewPlayer(DatabaseHelper.getInstance(this), finalPlayerId, finalTeamId);
                if (res) {
                    Toast.makeText(this, R.string.a_focus_view_t_added, Toast.LENGTH_SHORT).show();
                }
            } else if (finalTeamId != null) {
                if (finalPlayerId != null) {
                    if (removeFromTeam(DatabaseHelper.getInstance(this), finalPlayerId, finalTeamId)) {
                        Toast.makeText(this, R.string.a_focus_view_t_removed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // query for statistics
        queryForStatistics(tv_name.getText().toString());

    }

    private void queryForStatistics(String name) {
        String lang = Locale.getDefault().getLanguage();
        DownloadStatisticsTask asyncTask = new DownloadStatisticsTask();
        asyncTask.execute(SERVER_URL_STATISTICS + name + "/" + lang);
    }

    private boolean removeFromTeam(SQLiteDatabase db, Integer finalPlayerId, Integer teamId) {
        boolean canceled = false;
        try {
            db.execSQL("DELETE FROM " + PlayerInTeam.TABLE_NAME + " WHERE " +
                    PlayerInTeam.COLUMN_NAME_PLAYER_ID + " = " + finalPlayerId + " AND " +
                    PlayerInTeam.COLUMN_NAME_TEAM_ID + " = " + teamId);
            canceled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canceled;
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

    private boolean saveNewPlayer(SQLiteDatabase db, Integer finalPlayerId, Integer teamId) {
        String insertPlayer = "INSERT INTO " + PlayerInTeam.TABLE_NAME + " VALUES (?,?,?)";
        boolean res = false;
        try {
            db.execSQL(insertPlayer, new Integer[]{DEFAULT_ID, finalPlayerId, teamId});
            // increment DEFAULT_ID
            DEFAULT_ID += 1;
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    // check the checkbox of the player is already in the team
    private boolean checkIfAlreadyInTeam(SQLiteDatabase db, String userIdentifier, Integer playerId) {
        Cursor c;
        boolean isIn = false;
        try {
            c = db.rawQuery("SELECT p." + PlayerInTeam.COLUMN_NAME_PLAYER_ID + " from " +
                    PlayerInTeam.TABLE_NAME + " as p, " +
                    Team.TABLE_NAME + " as t, " +
                    UserProfile.TABLE_NAME + " as u where " +
                    "t." + Team._ID + " = p." + PlayerInTeam.COLUMN_NAME_TEAM_ID + " and " +
                    "u." + UserProfile.COLUMN_NAME_EMAIL + " = t." + Team.COLUMN_NAME_OWNER + " and " +
                    "t." + Team.COLUMN_NAME_OWNER + " = '" + userIdentifier + "' and " + "" +
                    "p." + PlayerInTeam.COLUMN_NAME_PLAYER_ID + " = " + playerId, null);
            if (c.moveToFirst()) {
                c.close();
                isIn = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isIn;
    }

    private class DownloadStatisticsTask extends AsyncTask<String, Integer, String> {

        // todo: use progresBar
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(FocusViewActivity.this);
            pd.setMessage(getString(R.string.a_focus_view_pb_message));
            pd.setIndeterminate(false);
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if (pd.isShowing()) {
                pd.dismiss();
            }

            JSONObject json = null;
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String description = null;
            try {
                if (json != null) {
                    description = json.getString("description");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (description != null) {
                TextView tv = findViewById(R.id.a_focus_view_tv_statistics_data);
                tv.setText(description);
            } else {
                Toast.makeText(FocusViewActivity.this, R.string.a_focus_view_t_server, Toast.LENGTH_LONG).show();
            }
        }
    }
}
