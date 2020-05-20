package projects.fantasysoccerauction.database;

import android.provider.BaseColumns;

public final class DatabaseNomenclature {

    public static class UserProfile implements BaseColumns {
        public static final String TABLE_NAME = "user_profile";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final String COLUMN_NAME_BIRTHDATE = "birthdate";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_PHOTO = "photo";
        public static final String COLUMN_NAME_EMAIL = "email";
    }

    public static class Player implements BaseColumns {
        public static final String TABLE_NAME = "player";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ROLE = "role";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_TEAM = "team";
    }

    public static class Team implements BaseColumns {
        public static final String TABLE_NAME = "team";
        public static final String COLUMN_NAME_OWNER = "owner";
        public static final String COLUMN_NAME_NAME = "name";
    }

    public static class PlayerInTeam implements BaseColumns {
        public static final String TABLE_NAME = "player_in_team";
        public static final String COLUMN_NAME_PLAYER_ID = "player";
        public static final String COLUMN_NAME_TEAM_ID = "team";
    }

    public static class Notification implements BaseColumns {
        public static final String TABLE_NAME = "notification";
        public static final String COLUMN_NAME_LAST_AUCTION_REMINDER = "last_auction_reminder";
        public static final String COLUMN_NAME_USER_ID = "user";
    }
}

