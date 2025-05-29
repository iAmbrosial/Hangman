import java.io.*;
import java.util.ArrayList;

public class DataStore {
    private static final String FILE_NAME = "players.txt";

    public static void savePlayers(ArrayList<Player> players) {
        try {
            FileOutputStream fos = new FileOutputStream(FILE_NAME);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(players);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Player> loadPlayers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new java.util.ArrayList<>();
        }

        try {
            FileInputStream fis = new FileInputStream(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Player> players = (ArrayList<Player>) ois.readObject();
            ois.close();
            fis.close();
            return players;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

}
