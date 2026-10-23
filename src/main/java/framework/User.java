package framework;

public class User {
    private String username;
    private String displayName;
    private String profilePic;

    public User(String username) {
        this.username = username;
        this.displayName = username;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
