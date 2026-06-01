package view.auth;

import controller.ControllerAuth;
import model.user.ModelUser;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.*;
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
        setSize(420, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        add(buatPanelHeader(), BorderLayout.NORTH);
        add(buatPanelForm(), BorderLayout.CENTER);
        add(buatPanelFooter(), BorderLayout.SOUTH);

        // Enter key trigger login
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

    private JPanel buatPanelHeader() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(25, 50, 120));
        p.setPreferredSize(new Dimension(0, 160));

        JPanel inner = new JPanel(new GridLayout(3, 1, 0, 6));
        inner.setOpaque(false);

        JLabel lblIcon = new JLabel("🏢", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 40));
        lblIcon.setForeground(Color.WHITE);

        JLabel lblTitle = new JLabel("Employee Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Silakan login untuk melanjutkan", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(170, 195, 240));

        inner.add(lblIcon);
        inner.add(lblTitle);
        inner.add(lblSub);
        p.add(inner);
        return p;
    }

    private JPanel buatPanelForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(6, 0, 6, 0);

        // Username
        gbc.gridy = 0;
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(50, 60, 90));
        p.add(lblUser, gbc);

        gbc.gridy = 1;
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(0, 40));
        txtUsername.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 195, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        p.add(txtUsername, gbc);

        // Password
        gbc.gridy = 2;
        gbc.insets = new Insets(14, 0, 6, 0);
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(50, 60, 90));
        p.add(lblPass, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(6, 0, 6, 0);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 40));
        txtPassword.setBorder(new CompoundBorder(
                new LineBorder(new Color(180, 195, 225), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        p.add(txtPassword, gbc);

        // Show password
        gbc.gridy = 4;
        chkShowPassword = new JCheckBox("Tampilkan password");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setBackground(Color.WHITE);
        chkShowPassword.setForeground(new Color(100, 110, 140));
        chkShowPassword.addActionListener(e
                -> txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : '●')
        );
        p.add(chkShowPassword, gbc);

        // Error label
        gbc.gridy = 5;
        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblError.setForeground(new Color(200, 40, 40));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblError, gbc);

        // Tombol login
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 6, 0);
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(25, 85, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 44));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> prosesLogin());
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(15, 65, 155));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(25, 85, 185));
            }
        });
        p.add(btnLogin, gbc);

        return p;
    }

    private JPanel buatPanelFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(new Color(245, 247, 251));
        p.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 225, 240)));
        JLabel lbl = new JLabel("Employee Management System — PBO Project");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setForeground(new Color(140, 150, 170));
        p.add(lbl);
        return p;
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");
        lblError.setText(" ");

        controllerAuth.loginAsync(username, password, new ControllerAuth.LoginListener() {
            @Override
            public void onSuccess(ModelUser user) {
                dispose();
                MainFrame frame = new MainFrame(user);
                frame.setVisible(true);
            }

            @Override
            public void onError(String pesan) {
                lblError.setText(pesan);
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        });
    }
}
