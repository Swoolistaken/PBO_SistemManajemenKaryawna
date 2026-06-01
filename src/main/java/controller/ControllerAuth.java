package controller;

import model.user.ModelUser;
import model.user.DAOUser;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.SQLException;

public class ControllerAuth {

    private final DAOUser daoUser = new DAOUser();
    private static ModelUser currentUser = null;

    public interface LoginListener {

        void onSuccess(ModelUser user);

        void onError(String pesan);
    }

    public void loginAsync(String username, String password, LoginListener listener) {
        new Thread(() -> {
            try {
                if (username == null || username.trim().isEmpty()) {
                    throw new IllegalArgumentException("Username tidak boleh kosong!");
                }
                if (password == null || password.trim().isEmpty()) {
                    throw new IllegalArgumentException("Password tidak boleh kosong!");
                }

                String hashedPassword = hashMD5(password);
                ModelUser user = daoUser.login(username.trim(), hashedPassword);

                if (user == null) {
                    javax.swing.SwingUtilities.invokeLater(()
                            -> listener.onError("Username atau password salah!"));
                } else {
                    currentUser = user;
                    javax.swing.SwingUtilities.invokeLater(() -> listener.onSuccess(user));
                }
            } catch (IllegalArgumentException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError(e.getMessage()));
            } catch (SQLException e) {
                javax.swing.SwingUtilities.invokeLater(() -> listener.onError("Error database: " + e.getMessage()));
            }
        }, "Thread-Login").start();
    }

    public static ModelUser getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static String hashMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hash = new StringBuilder(no.toString(16));
            while (hash.length() < 32) {
                hash.insert(0, "0");
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal hash password: " + e.getMessage());
        }
    }
}
