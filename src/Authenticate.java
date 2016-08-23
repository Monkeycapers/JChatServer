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
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\Evan\\AppData\\Roaming\\Users"));

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println("Now parsing: " + line);
                String[] split = line.split(",");
                String suser = split[0];
                String spass = split[1];
                String srank = split[2];
                System.out.println(suser + "," + spass + "," + srank);
                if (suser.equals(user) && (BCrypt.checkpw(pass, spass))) {
                    r = Rank.valueOf(srank);
                }
                else {
                    System.out.println("Fail, " + user + "-->" + suser + "\n" + pass + "-->" + spass + BCrypt.checkpw(pass, spass) );
                }
            }

            br.close();
        }
        catch (Exception e) {
            System.out.println("Could not access passwords");
        }

        return r;
    }
    public static boolean verify (String user) {
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\Evan\\AppData\\Roaming\\Users"));

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println("Now parsing: " + line);
                String[] split = line.split(",");
                String suser = split[0];
                String spass = split[1];
                String srank = split[2];
                System.out.println(suser + "," + spass + "," + srank);
                if (suser.equals(user)) {
                    return true;
                }
                else {
                    //System.out.println("Fail, " + user + "-->" + suser + "\n" + pass + "-->" + spass + BCrypt.checkpw(pass, spass) );
                }
            }

            br.close();
        }
        catch (Exception e) {
            System.out.println("Could not access passwords");
        }
        return false;
    }
    public static Rank signup (String user, String pass) throws IOException {
        try {
            //No users exist with that username
            //FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\Evan\\Google Drive\\dev\\JChat Server\\out\\production\\JChat Server\\Users"));
            //BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(fos)));
            if (verify(user)) {
                return Rank.Guest;
            }
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\Evan\\AppData\\Roaming\\Users"));

            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String total = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                total += line + "\n";
            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\Evan\\AppData\\Roaming\\Users"));
            pw.print(total);
            pw.print(user + "," + BCrypt.hashpw(pass, BCrypt.gensalt()) + "," + Rank.User.name());
           // bw.append("\n" + user + "," + BCrypt.hashpw(user, BCrypt.gensalt()) + "," + Rank.User.name());
            //bw.close();
            //fos.close();
            pw.close();

        }
        catch (Exception e) {
            System.out.println("Error in signing up..." + e.toString());
        }

        return login(user, pass);
    }

    public static boolean update (User u) {
        try {
            FileInputStream fis = new FileInputStream(new File("C:\\Users\\Evan\\AppData\\Roaming\\Users"));
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String total = "";
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(",");
                //String suser = split[0];
                if (split[0].equals(u.username)) {
                    total += u.username + "," + split[1] + "," + u.rank.name() + "\n";
                }
                else {
                    total += line + "\n";
                }

            }
            br.close();
            PrintWriter pw = new PrintWriter(new FileWriter("C:\\Users\\Evan\\AppData\\Roaming\\Users"));
            pw.print(total);
            //pw.print(user + "," + BCrypt.hashpw(pass, BCrypt.gensalt()) + "," + Rank.User.name());
            // bw.append("\n" + user + "," + BCrypt.hashpw(user, BCrypt.gensalt()) + "," + Rank.User.name());
            //bw.close();
            //fos.close();
            pw.close();

        }
        catch (Exception e) {
            System.out.println("Error in signing up..." + e.toString());
        }
        return false;
    }
}
