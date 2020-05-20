package projects.fantasysoccerauction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Team;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;
import projects.fantasysoccerauction.utils.DateManager;

public class RegistrationActivity extends AppCompatActivity {
    private static final Integer FIRST_TEAM_ID = 10;

    // TODO: send data to the server and wait for the response

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // flag to store if a user with the same email already exists
        AtomicBoolean alreadyExists = new AtomicBoolean(false);

        // set the behaviour of the button
        Button btn_reg = findViewById(R.id.a_registration_btn_register);
        btn_reg.setOnClickListener(v -> {

            // get references to layout elements
            EditText et_name = findViewById(R.id.a_registration_et_name);
            EditText et_email = findViewById(R.id.a_registration_et_email);
            EditText et_password = findViewById(R.id.a_registration_et_password);
            EditText et_birthdate = findViewById(R.id.a_registration_et_birthdate);
            EditText et_phone = findViewById(R.id.a_registration_et_phone);

            // get data from the view elements
            String name = et_name.getText().toString();
            String email = et_email.getText().toString();
            String password = et_password.getText().toString();
            String birthdateString = et_birthdate.getText().toString();
            long birthdate = DateManager.transformDateToMilliseconds(this, birthdateString);
            String phone = et_phone.getText().toString();

            // check if the email already exists
            try {
                Cursor c = null;
                try {
                    c = checkIfEmailIsAlreadyUsed(DatabaseHelper.getInstance(this), email);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (c != null && c.moveToFirst()) {
                    alreadyExists.set(true);
                    Toast.makeText(this, R.string.a_registration_t_user_already_exists, Toast.LENGTH_LONG).show();
                    c.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, R.string.a_registration_t_error_2, Toast.LENGTH_LONG).show();
            }

            // insert data into the database if possible
            if (!alreadyExists.get()) {
                //todo: hash password
                //String hashedPassword = PasswordStorage.getPasswordHash(et_password.getText().toString());

                long registrationResult = registerUser(DatabaseHelper.getInstance(this), name, password, birthdate, phone, email);
                long teamResult = createTeam(DatabaseHelper.getInstance(this), email);

                if (registrationResult != -1) {
                    // go to the Login page
                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.a_registration_t_error_1, Toast.LENGTH_LONG).show();
                }
            }
            // reset the boolean to allow further attempts
            alreadyExists.set(false);
        });
    }

    private long createTeam(SQLiteDatabase db, String email) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Team._ID, FIRST_TEAM_ID);
        values.put(Team.COLUMN_NAME_OWNER, email);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Team.TABLE_NAME, null, values);
        return newRowId;
    }

    private long registerUser(SQLiteDatabase db, String name, String password, Long birthdate, String phone, String email) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(UserProfile.COLUMN_NAME_NAME, name);
        values.put(UserProfile.COLUMN_NAME_PASSWORD, password);
        values.put(UserProfile.COLUMN_NAME_BIRTHDATE, birthdate);
        values.put(UserProfile.COLUMN_NAME_PHONE, phone);
        values.put(UserProfile.COLUMN_NAME_EMAIL, email);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(UserProfile.TABLE_NAME, null, values);
        return newRowId;
    }

    private Cursor checkIfEmailIsAlreadyUsed(SQLiteDatabase db, String email) {
        // useful columns
        String[] projection = {
                UserProfile._ID,
                UserProfile.COLUMN_NAME_EMAIL
        };

        // Filter results WHERE "title" = 'My Title'
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
