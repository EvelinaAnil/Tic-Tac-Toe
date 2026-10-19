package framework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    public User login(String username) {
        User user = find(username);
        if (user == null) {
            user = new User(username);
            insert(user);
        }
        return user;
    }

    public void save(User user) {
        String sql = "UPDATE users SET display_name = ?, profile_pic = ? WHERE username = ?";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, user.getDisplayName());
            statement.setString(2, user.getProfilePic());
            statement.setString(3, user.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kon gebruiker niet opslaan: " + e.getMessage());
        }
    }

    private User find(String username) {
        String sql = "SELECT username, display_name, profile_pic FROM users WHERE username = ?";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet rows = statement.executeQuery()) {
                if (!rows.next()) {
                    return null;
                }
                User user = new User(rows.getString("username"));
                String displayName = rows.getString("display_name");
                if (displayName != null) {
                    user.setDisplayName(displayName);
                }
                user.setProfilePic(rows.getString("profile_pic"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Kon gebruiker niet laden: " + e.getMessage());
            return null;
        }
    }

    private void insert(User user) {
        String sql = "INSERT INTO users (username, display_name, profile_pic) VALUES (?, ?, ?)";
        try (PreparedStatement statement = Database.get().prepareStatement(sql)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getDisplayName());
            statement.setString(3, user.getProfilePic());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Kon gebruiker niet aanmaken: " + e.getMessage());
        }
    }
}
