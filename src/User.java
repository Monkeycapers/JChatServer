/**
 * Created by Evan on 8/22/2016.
 */
public class User {
    Rank rank;
    String username;

    public User(Rank r, String un) {
        rank = r;
        username = un;
        if (!Verify(this)) {
            System.out.println("Could not create user.");
            rank = Rank.Guest;
            username = "Anon";
        }
    }
    public User() {
        rank = Rank.Guest;
        username = "Anon";
    }

    public static boolean Verify(User u) {
        return Authenticate.verify(u.username);
    }
}
