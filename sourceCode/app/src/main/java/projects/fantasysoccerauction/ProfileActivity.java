package projects.fantasysoccerauction;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import projects.fantasysoccerauction.database.DatabaseHelper;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;
import projects.fantasysoccerauction.utils.DateManager;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static projects.fantasysoccerauction.LoginActivity.USER_IDENTIFIER_EXTRA;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 81;
    private static final int REQUEST_PERMISSION_CODE = 200;
    private ImageView iv;
    private Uri photoURI;
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // todo: date picker for the birthdate
        // get the username from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userIdentifier = extras.getString(USER_IDENTIFIER_EXTRA);
        }

        // manage text input
        EditText et_name = findViewById(R.id.a_registration_et_name);
        EditText et_birthdate = findViewById(R.id.a_registration_et_birthdate);
        EditText et_email = findViewById(R.id.a_registration_et_email);
        EditText et_phone = findViewById(R.id.a_registration_et_phone);

        disableEditTexts(et_name, et_birthdate, et_email, et_phone);

        // pick a photo as profile picture
        iv = findViewById(R.id.a_profile_iv_photo);

        loadData(userIdentifier, iv, et_name, et_birthdate, et_email, et_phone);

        iv.setOnClickListener(v -> {
            // check if the phone has a camera
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (checkPermission()) {
                    dispatchTakePictureIntent();
                } else {
                    requestPermission();
                }
            } else {
                Toast.makeText(ProfileActivity.this, R.string.a_profile_t_camera, Toast.LENGTH_LONG).show();
            }
        });

        Button btn_update = findViewById(R.id.a_profile_btn_update);
        btn_update.setOnClickListener(v -> {
            enableEditTexts(et_name, et_birthdate, et_email, et_phone);
            setHintAsText(et_name, et_birthdate, et_email, et_phone);
            Toast.makeText(ProfileActivity.this, R.string.a_profile_t_data_update, Toast.LENGTH_LONG).show();
        });

        Button btn_save = findViewById(R.id.a_profile_btn_save);
        btn_save.setOnClickListener(v -> {
            // check if data modification has been enabled
            if (et_name.isEnabled()) {
                boolean updateOk = updateProfile(DatabaseHelper.getInstance(this), et_name.getText().toString(), et_phone.getText().toString(), et_birthdate.getText().toString(), et_email.getText().toString());
                if (updateOk) {
                    Toast.makeText(ProfileActivity.this, R.string.a_profile_t_data_saved, Toast.LENGTH_LONG).show();
                }
                disableEditTexts(et_name, et_birthdate, et_email, et_phone);
            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "projects.fantasysoccerauction.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, R.string.a_profile_t_error_take_image, Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    try {
                        // get the image from the internal storage of the app
                        Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                        iv.setImageBitmap(imageBitmap);
                        // save the Uri in the database
                        boolean isUpdated = updatePhotoUri(DatabaseHelper.getInstance(this), userIdentifier, photoURI.toString());
                        if (!isUpdated) {
                            Toast.makeText(ProfileActivity.this, R.string.a_profile_t_error_set_image, Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!checkPermission()) {
                        showMessageOKCancel(getString(R.string.a_profile_a_permissions),
                                (dialog, which) -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermission();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                        dispatchTakePictureIntent();
                    }
                }
            }
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ProfileActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.a_profile_a_ok), okListener)
                .setNegativeButton(getString(R.string.a_profile_a_cancel), null)
                .create()
                .show();
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void disableEditTexts(EditText et_name, EditText et_birthdate, EditText et_email, EditText et_phone) {
        et_name.setEnabled(false);
        et_birthdate.setEnabled(false);
        et_email.setEnabled(false);
        et_phone.setEnabled(false);
    }

    private void enableEditTexts(EditText et_name, EditText et_birthdate, EditText et_email, EditText et_phone) {
        et_name.setEnabled(true);
        et_birthdate.setEnabled(true);
        //et_email.setEnabled(true); //todo: this cannot be the id in the future
        et_phone.setEnabled(true);
    }

    private void setHintAsText(EditText et_name, EditText et_birthdate, EditText et_email, EditText et_phone) {
        et_name.setText(et_name.getHint());
        et_birthdate.setText(et_birthdate.getHint());
        et_email.setText(et_email.getHint());
        et_phone.setText(et_phone.getHint());
    }

    private void loadData(String username, ImageView iv, EditText et_name, EditText et_birthdate, EditText et_email, EditText et_phone) {

        Cursor c = getProfileData(DatabaseHelper.getInstance(this), username);

        if (c != null && c.moveToFirst()) {
            // database structure id, name, password, birthdate, phone, photo, email
            // set the email
            et_email.setHint(username);
            // set the picture
            if (c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_PHOTO)) != null) {
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_PHOTO))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageBitmap != null) {
                    iv.setImageBitmap(imageBitmap);
                }
            }
            // set the name
            if (c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_NAME)) != null) {
                et_name.setHint(c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_NAME)));
            }
            // set the birthdate
            if (c.getLong(c.getColumnIndex(UserProfile.COLUMN_NAME_BIRTHDATE)) > 0) {
                et_birthdate.setHint(DateManager.transformMillisecondsToDate(c.getLong(c.getColumnIndex(UserProfile.COLUMN_NAME_BIRTHDATE))));
            }
            // set the phone
            if (c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_PHONE)) != null) {
                et_phone.setHint(c.getString(c.getColumnIndex(UserProfile.COLUMN_NAME_PHONE)));
            }
            c.close();
        }
    }

    private Cursor getProfileData(SQLiteDatabase db, String username) {
        String selection = UserProfile.COLUMN_NAME_EMAIL + " LIKE ?";
        String[] selectionArgs = {username};

        Cursor c = db.query(
                UserProfile.TABLE_NAME,   // The table to query
                null,             // get all columns
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return c;
    }

    private boolean updatePhotoUri(SQLiteDatabase db, String user, String uri) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(UserProfile.COLUMN_NAME_PHOTO, uri);

        // Which row to update, based on the title
        String selection = UserProfile.COLUMN_NAME_EMAIL + " LIKE ?";
        String[] selectionArgs = {user};

        int count = db.update(
                UserProfile.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count > 0;
    }

    // TODO: when id won't be the email, allow to update the email
    private boolean updateProfile(SQLiteDatabase db, String name, String phone, String birthdate, String email) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(UserProfile.COLUMN_NAME_NAME, name);
        values.put(UserProfile.COLUMN_NAME_PHONE, phone);
        //values.put(UserProfile.COLUMN_NAME_EMAIL, email);
        values.put(UserProfile.COLUMN_NAME_BIRTHDATE, DateManager.transformDateToMilliseconds(this, birthdate));

        // Which row to update, based on the title
        String selection = UserProfile.COLUMN_NAME_EMAIL + " LIKE ?";
        String[] selectionArgs = {email};

        int count = db.update(
                UserProfile.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        return count > 0;
    }
}

