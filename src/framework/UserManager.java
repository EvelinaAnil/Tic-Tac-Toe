package framework;

public class UserManager {
    public User login(String username){
        return new User(username);
    }
}
