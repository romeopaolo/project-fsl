package projects.fantasysoccerauction.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import projects.fantasysoccerauction.database.DatabaseNomenclature.Player;
import projects.fantasysoccerauction.database.DatabaseNomenclature.PlayerInTeam;
import projects.fantasysoccerauction.database.DatabaseNomenclature.Team;
import projects.fantasysoccerauction.database.DatabaseNomenclature.UserProfile;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static SQLiteDatabase mInstance = null;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FantasySoccer.db";

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USER_PROFILE);
        db.execSQL(SQL_CREATE_PLAYER);
        db.execSQL(SQL_CREATE_TEAM);
        db.execSQL(SQL_CREATE_PLAYER_IN_TEAM);
        db.execSQL(SQL_CREATE_NOTIFICATION);
        // temporary insertion of data TODO: put them in the server
        db.execSQL(DATABASE_TEST);
        db.execSQL(SQL_INSERT_FAKE_TEAM);
        db.execSQL(SQL_INSERT_FAKE_USER);
        db.execSQL(SQL_INSERT_FAKE_NOTIFICATION);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database should only be a cache for online data, so its upgrade policy is simply
        // to discard the data and start over
        db.execSQL(SQL_DELETE_PLAYER_IN_TEAM);
        db.execSQL(SQL_DELETE_TEAM);
        db.execSQL(SQL_DELETE_PLAYER);
        db.execSQL(SQL_DELETE_USER_PROFILE);
        db.execSQL(SQL_DELETE_NOTIFICATION);
        onCreate(db);
    }

    public static void openDb(Context ctx){
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext()).getReadableDatabase(); // todo: do in asyncTask
        }
    }
    public static SQLiteDatabase getInstance(Context ctx) {
        return mInstance;
    }

    public static void closeDb() {
        mInstance.close();
    }

    private static final String SQL_INSERT_FAKE_USER = "INSERT INTO user_profile (_id, name, email, password, birthdate, phone, photo) " +
            "VALUES(1,'paolo', 'paolo@mail.it', 'password', '07/03/1996', '3483795766', null)";

    private static final String SQL_INSERT_FAKE_TEAM = "INSERT INTO team (_id, owner, name) " +
            "VALUES(1,'paolo@mail.it', 'dream team')";

    private static final String SQL_INSERT_FAKE_NOTIFICATION = "INSERT INTO notification (_id, last_auction_reminder, user) " +
            "VALUES(1, -1, 'paolo@mail.it')";

    private static final String SQL_CREATE_USER_PROFILE =
            "CREATE TABLE " + UserProfile.TABLE_NAME + " (" +
                    UserProfile._ID + " INTEGER PRIMARY KEY," +
                    UserProfile.COLUMN_NAME_NAME + " TEXT," +
                    UserProfile.COLUMN_NAME_EMAIL + " TEXT NOT NULL," +
                    UserProfile.COLUMN_NAME_PASSWORD + " TEXT NOT NULL," +
                    UserProfile.COLUMN_NAME_BIRTHDATE + " INTEGER," +
                    UserProfile.COLUMN_NAME_PHONE + " TEXT," +
                    UserProfile.COLUMN_NAME_PHOTO + " TEXT)";

    private static final String SQL_CREATE_PLAYER =
            "CREATE TABLE " + Player.TABLE_NAME + " (" +
                    Player._ID + " INTEGER PRIMARY KEY," +
                    Player.COLUMN_NAME_NAME + " TEXT," +
                    Player.COLUMN_NAME_ROLE + " TEXT," +
                    Player.COLUMN_NAME_TEAM + " TEXT," +
                    Player.COLUMN_NAME_VALUE + " TEXT)";

    private static final String SQL_CREATE_TEAM =
            "CREATE TABLE " + Team.TABLE_NAME + " (" +
                    Team._ID + " INTEGER PRIMARY KEY," +
                    Team.COLUMN_NAME_OWNER + " TEXT," +
                    Team.COLUMN_NAME_NAME + " TEXT," +
                    "FOREIGN KEY(" + Team.COLUMN_NAME_OWNER + ") REFERENCES " + UserProfile.TABLE_NAME + "(" + UserProfile.COLUMN_NAME_EMAIL + "))"; //TODO: use ID in the future

    private static final String SQL_CREATE_PLAYER_IN_TEAM =
            "CREATE TABLE " + PlayerInTeam.TABLE_NAME + " (" +
                    PlayerInTeam._ID + " INTEGER PRIMARY KEY," +
                    PlayerInTeam.COLUMN_NAME_PLAYER_ID + " INTEGER," +
                    PlayerInTeam.COLUMN_NAME_TEAM_ID + " INTEGER," +
                    "FOREIGN KEY(" + PlayerInTeam.COLUMN_NAME_PLAYER_ID + ") REFERENCES " + Player.TABLE_NAME + "(" + Player._ID + ")," +
                    "FOREIGN KEY(" + PlayerInTeam.COLUMN_NAME_TEAM_ID + ") REFERENCES " + Team.TABLE_NAME + "(" + Team._ID + "))";

    private static final String SQL_CREATE_NOTIFICATION =
            "CREATE TABLE " + DatabaseNomenclature.Notification.TABLE_NAME + " (" +
                    DatabaseNomenclature.Notification._ID + " INTEGER PRIMARY KEY," +
                    DatabaseNomenclature.Notification.COLUMN_NAME_USER_ID + " TEXT," +
                    DatabaseNomenclature.Notification.COLUMN_NAME_LAST_AUCTION_REMINDER + " INTEGER," +
                    "FOREIGN KEY(" + DatabaseNomenclature.Notification.COLUMN_NAME_USER_ID + ") REFERENCES " + UserProfile.TABLE_NAME + "(" + UserProfile.COLUMN_NAME_EMAIL + "))"; //TODO: use ID in the future

    private static final String SQL_DELETE_USER_PROFILE = "DROP TABLE IF EXISTS " + UserProfile.TABLE_NAME;
    private static final String SQL_DELETE_PLAYER = "DROP TABLE IF EXISTS " + Player.TABLE_NAME;
    private static final String SQL_DELETE_TEAM = "DROP TABLE IF EXISTS " + Team.TABLE_NAME;
    private static final String SQL_DELETE_PLAYER_IN_TEAM = "DROP TABLE IF EXISTS " + PlayerInTeam.TABLE_NAME;
    private static final String SQL_DELETE_NOTIFICATION = "DROP TABLE IF EXISTS " + DatabaseNomenclature.Notification.TABLE_NAME;

    private static final String DATABASE_TEST = "INSERT INTO " +
            Player.TABLE_NAME + "(" + Player._ID + "," +
            Player.COLUMN_NAME_ROLE + "," +
            Player.COLUMN_NAME_NAME + "," +
            Player.COLUMN_NAME_TEAM + "," +
            Player.COLUMN_NAME_VALUE + ") VALUES " +
            "  (513,'D','ACERBI','Lazio',14)," +
            "  (4515,'C','AGUDELO','Genoa',4)," +
            "  (2865,'D','AINA','Torino',8)," +
            "  (662,'D','ALEX SANDRO','Juventus',14)," +
            "  (397,'C','ALLAN','Napoli',14)," +
            "  (4522,'C','AMRABAT','Verona',4)," +
            "  (706,'C','ANSALDI','Torino',13)," +
            "  (295,'D','ASAMOAH','Inter',10)," +
            "  (761,'P','AUDERO','Sampdoria',11)," +
            "  (4523,'P','AVOGADRI','Sampdoria',1);";
}
