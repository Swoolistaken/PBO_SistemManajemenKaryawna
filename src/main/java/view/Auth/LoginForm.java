package view.auth;

import controller.ControllerAuth;
import model.user.User;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private JCheckBox chkShowPassword;

    private final ControllerAuth controllerAuth = new ControllerAuth();

    public LoginForm() {
        initUI();
    }

    private void initUI() {
        setTitle("Login — Employee Management System");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buatForm(), BorderLayout.CENTER);

        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    prosesLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);
    }

    private JPanel buatForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblJudul = new JLabel("Employee Management System", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Dialog", Font.BOLD, 14));
        panel.add(lblJudul, gbc);

        // Username
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        // Show password
        gbc.gridx = 1;
        gbc.gridy = 3;
        chkShowPassword = new JCheckBox("Tampilkan password");
        chkShowPassword.addActionListener(e
                -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '•')
        );
        panel.add(chkShowPassword, gbc);

        // Error label
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);
        panel.add(lblError, gbc);

        // Tombol login
        gbc.gridy = 5;
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> prosesLogin());
        panel.add(btnLogin, gbc);

        return panel;
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        btnLogin.setEnabled(false);
        btnLogin.setText("Loading...");
        lblError.setText(" ");

        new Thread(()
                -> controllerAuth.login(username, password, new ControllerAuth.LoginListener() {
                    @Override
                    public void onSuccess(User user) {
                        SwingUtilities.invokeLater(() -> {
                            dispose();
                            new MainFrame(user).setVisible(true);
                        });
                    }

                    @Override
                    public void onError(String pesan) {
                        SwingUtilities.invokeLater(() -> {
                            lblError.setText(pesan);
                            btnLogin.setEnabled(true);
                            btnLogin.setText("Login");
                            txtPassword.setText("");
                            txtPassword.requestFocus();
                        });
                    }
                }),
                "Thread-Login").start();
    }
}
