/**
 * Created by Evan on 8/22/2016.
 */
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Authenticate {

    public static FileReader inputStream = null;

    public static Rank login (String user, String pass) {
        Rank r = Rank.Guest;
        System.out.println(user + "," + pass);
        System.out.println(new File(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath()));
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\Evan\\Google Drive\\dev\\JChat Server\\out\\production\\JChat Server\\Users"));

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] split = line.split(",");
                String suser = split[0];
                String spass = split[1];
                String srank = split[2];
                System.out.println(suser + "," + spass + "," + srank);
                if (suser.equals(user) && BCrypt.checkpw(pass, spass)) {
                    r = Rank.valueOf(srank);
                }
            }

            br.close();
        }
        catch (Exception e) {
            System.out.println("Could not access passwords");
        }

        return r;
    }
}
