package projects.fantasysoccerauction.recyclerviewmanager.auction;

public class DataItemAwardedPlayer {
    int id;
    String name;
    String value;
    String role;
    String team;

    // TODO: make package-private when data will be taken from a database
    //Define the structure of the data for the card view of the player
    public DataItemAwardedPlayer(int id, String role, String name, String team, String value) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.value = value;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getRole() {
        return role;
    }

    public String getTeam() {
        return team;
    }
}
