import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class PasswordManager {
    private final HashMap<String, String[]> passwords;
    final private String salt;
    final private ArrayList<String[]> token;

    public PasswordManager(String salt) {
        this.passwords = new HashMap<>();
        this.salt = salt;
        this.token = new ArrayList<>();
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFileContent(String filePath) {
        /* 
         * Charge le contenu du fichier de mots de passe
         * Le fichier doit être au format suivant :
         * username1:key1:data1,data2,data3,data4
         * username2:key2:data1,data2,data3,data4
         * ...
         * usernameN:keyN:data1,data2,data3,data4
         * 
        */
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String username = parts[0];
                    String key = parts[1];
                    String[] data = parts[2].split(",");
                    passwords.put(username + ":" + key, data);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public String verify(String username, String password) {
        String key = hash(password + salt);
        String[] data = passwords.get(username + ":" + key);
        if (data != null) {
            String newToken = hash(username + System.currentTimeMillis());
            token.add(new String[]{String.valueOf(System.currentTimeMillis()), newToken});
            return String.join(",", data) + ":" + newToken;
        } else {
            return "";
        }
    }

    public boolean checkToken(String token) {
        long currentTime = System.currentTimeMillis();
        this.token.removeIf(t -> (currentTime - Long.parseLong(t[0])) > 3 * 60 * 60 * 1000); // 3 hours in milliseconds
        for (String[] t : this.token) {
            if (t[1].equals(token)) {
                return true;
            }
        }
        return false;
    }
}
