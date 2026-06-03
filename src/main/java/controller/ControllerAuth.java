package controller;

import model.user.User;
import model.user.DAOUser;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.SQLException;

public class ControllerAuth {

    private final DAOUser daoUser = new DAOUser();
    private static User currentUser = null;

    public interface LoginListener {

        void onSuccess(User user);

        void onError(String pesan);
    }

    public void login(String username, String password, LoginListener listener) {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username tidak boleh kosong!");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password tidak boleh kosong!");
            }

            String hashed = hashMD5(password);
            User user = daoUser.login(username.trim(), hashed);

            if (user == null) {
                listener.onError("Username atau password salah!");
            } else {
                currentUser = user;
                listener.onSuccess(user);
            }
        } catch (IllegalArgumentException e) {
            listener.onError(e.getMessage());
        } catch (SQLException e) {
            listener.onError("Error database: " + e.getMessage());
        }
    }

    public static User getCurrentUser() {
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
            byte[] digest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, digest);
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
