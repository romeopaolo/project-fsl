package projects.fantasysoccerauction;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;

import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;

public class HomeActivity extends AppCompatActivity {
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // get data from LoginActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(USER_IDENTIFIER_EXTRA);
        }

        // load name and picture
        loadDynamicData();

        ImageButton btn_profile = findViewById(R.id.a_home_ib_profile);
        btn_profile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            // pass the user identifier to the next activity
            intent.putExtra(USER_IDENTIFIER_EXTRA, userIdentifier);
            startActivity(intent);
        });

        ImageButton btn_settings = findViewById(R.id.a_home_ib_settings);
        btn_settings.setOnClickListener(v -> {
            Toast.makeText(this, R.string.t_not_yet_implemented, Toast.LENGTH_LONG).show();
            /* TODO: implement settings
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            */
        });

        ImageButton btn_team = findViewById(R.id.a_home_ib_team);
        btn_team.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TeamActivity.class);
            // pass the user identifier to the next activity
            intent.putExtra(USER_IDENTIFIER_EXTRA, userIdentifier);
            startActivity(intent);
        });

        ImageButton btn_auction = findViewById(R.id.a_home_ib_auction);
        btn_auction.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AuctionActivity.class);
            // pass the user identifier to the next activity
            intent.putExtra(USER_IDENTIFIER_EXTRA, userIdentifier);
            startActivity(intent);
        });

        ImageButton btn_players = findViewById(R.id.a_home_ib_players);
        btn_players.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, PlayersListActivity.class);
            // pass the user identifier to the next activity
            intent.putExtra(USER_IDENTIFIER_EXTRA, userIdentifier);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.a_home_a_logout_title))
                .setMessage(getString(R.string.a_home_a_logout_text))
                .setPositiveButton(getString(R.string.a_home_a_logout_yes), (dialog, which) -> {
                    Toast.makeText(this, R.string.a_home_a_logout_done, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(getString(R.string.a_home_a_logout_no), null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDynamicData();
    }

    private void loadDynamicData() {
        // Get references to view elements
        TextView tv_name = findViewById(R.id.a_home_tv_name);
        ImageView iv_picture = findViewById(R.id.a_home_iv_picture);

        // query to retrieve picture Uri and name
        Cursor c = null;
        try {
            c = getDynamicDataFromDb(DatabaseHelper.getInstance(this), userIdentifier);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null && c.moveToFirst()) {
            tv_name.setText(c.getString(c.getColumnIndex("name")).toUpperCase());
            String uri = c.getString(c.getColumnIndex("photo"));
            if (uri != null) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(c.getString(c.getColumnIndex("photo"))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageBitmap != null) {
                    iv_picture.setImageBitmap(imageBitmap);
                }
            }
            c.close();
        } else {
            tv_name.setText("");
        }
    }

    private Cursor getDynamicDataFromDb(SQLiteDatabase db, String email) {
        // useful columns
        String[] projection = {
                UserProfile.COLUMN_NAME_NAME,
                UserProfile.COLUMN_NAME_PHOTO
        };

        String selection = UserProfile.COLUMN_NAME_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                UserProfile.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // don't order
        );
        return cursor;
    }
}
