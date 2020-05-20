package projects.fantasysoccerauction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_IDENTIFIER_EXTRA = "username";
    public static final Integer TEAM_ID = 1; // todo: give the chance to have more than a team in the future


    private final String PREFERENCE_FILE_CREDENTIALS = "permanent_credentials";
    private final String PREFERENCE_ATTR_USERNAME = "user";
    private final String PREFERENCE_ATTR_PASSWORD = "pass";
    private final String PREFERENCE_ATTR_SAVED = "saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get references of the view elements
        EditText et_email = findViewById(R.id.a_login_et_email);
        EditText et_pass = findViewById(R.id.a_login_et_password);

        // database initialization
        DatabaseHelper.openDb(this);

        // use preferences to save/load the credentials of the last user
        SharedPreferences preferences = getSharedPreferences(PREFERENCE_FILE_CREDENTIALS, Context.MODE_PRIVATE);
        boolean loadSavedCredentials = preferences.getBoolean(PREFERENCE_ATTR_SAVED, false);

        // to skip hashing if password is already saved
        //AtomicBoolean passwordIsHashed = new AtomicBoolean(false);

        if (loadSavedCredentials) {
            CheckBox cb = findViewById(R.id.a_login_cb_save_credentials);
            cb.setChecked(true);

            // check if credentials are available
            Credentials savedCredentials = readPreferences(preferences);
            if (savedCredentials.getUsername() != null && savedCredentials.getPassword() != null) {
                et_email.setText(savedCredentials.getUsername());
                et_pass.setText(savedCredentials.getPassword());
                //passwordIsHashed.set(true);
            }
        }

        // set the behaviour of the login button
        Button btn_login = findViewById(R.id.a_login_btn_login);
        btn_login.setOnClickListener(v -> {
            String username = et_email.getText().toString();
            String password = et_pass.getText().toString();
/*
            // only for the first time of each time the activity is restored, check if the password is already hashed
            if (!passwordIsHashed.get()) {
                Toast.makeText(this, "before: " + password, Toast.LENGTH_LONG).show();
                password = PasswordStorage.getPasswordHash(password);
                Toast.makeText(this, "after: " + password, Toast.LENGTH_LONG).show();
                passwordIsHashed.set(false);
            }
*/
            // if login succeeds TODO: implement the login through a server
            if (checkCredentials(DatabaseHelper.getInstance(this), username, password)) {
                // if the checkbox is checked save the credentials, if not reset them
                CheckBox cb = findViewById(R.id.a_login_cb_save_credentials);
                if (cb.isChecked()) {
                    updateCredentials(preferences, username, password, true);
                } else {
                    updateCredentials(preferences, null, null, false);
                }

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra(USER_IDENTIFIER_EXTRA, username);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.a_login_t_invalid_credentials, Toast.LENGTH_LONG).show();
            }
        });

        // go to the registration page
        Button btn_reg = findViewById(R.id.a_login_btn_registration);
        btn_reg.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        // close the database connector
        DatabaseHelper.closeDb();
        super.onDestroy();
    }

    // manage the login locally
    private boolean checkCredentials(SQLiteDatabase db, String username, String password) {
        String selection = UserProfile.COLUMN_NAME_EMAIL + " = ? AND " +
                UserProfile.COLUMN_NAME_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(
                DatabaseNomenclature.UserProfile.TABLE_NAME,   // The table to query
                null,            // return all
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,           // don't group the rows
                null,            // don't filter by row groups
                null            // don't order
        );
        return cursor.getCount() > 0;
    }

    private void updateCredentials(SharedPreferences preferences, String username, String password, boolean save) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREFERENCE_ATTR_USERNAME, username);
        editor.putString(PREFERENCE_ATTR_PASSWORD, password);
        editor.putBoolean(PREFERENCE_ATTR_SAVED, save);
        editor.apply();
    }

    private Credentials readPreferences(SharedPreferences preferences) {
        String username = preferences.getString(PREFERENCE_ATTR_USERNAME, null);
        String password = preferences.getString(PREFERENCE_ATTR_PASSWORD, null);
        return new Credentials(username, password);
    }
}

final class Credentials {
    private final String username;
    private final String password;

    Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}