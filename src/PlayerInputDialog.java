package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayerInputDialog extends JDialog {
    private JTextField nameTextField;
    private JButton okButton;
    private JButton cancelButton;
    private String playerName;
    private boolean cancelled = false;

    public PlayerInputDialog(JFrame parentFrame) {
        super(parentFrame, "MathFun Quiz - Input Nama Pemain", true); // Modal dialog
        setupUI();
        setupEventHandlers();

        // Set dialog properties
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);
        pack(); // Size dialog to fit components
        setLocationRelativeTo(parentFrame); // Center relative to parent, or screen if parent is null
    }

    private void setupUI() {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Use default if Nimbus not available
        }

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background from light blue to mint green
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 250),
                        0, getHeight(), new Color(152, 251, 152));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header panel with title and icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("* Selamat Datang di MathFun Quiz! *", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 24));
        titleLabel.setForeground(new Color(25, 25, 112)); // Navy Blue
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel instructionLabel = new JLabel("Masukkan nama Anda untuk memulai permainan:", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(70, 130, 180)); // Steel Blue
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(instructionLabel, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("@ Nama Pemain:");
        nameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        nameLabel.setForeground(new Color(25, 25, 112));

        nameTextField = new JTextField(15);
        nameTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        nameTextField.setBackground(Color.WHITE);
        nameTextField.setCaretColor(new Color(25, 25, 112));

        // Set focus on text field when dialog opens
        SwingUtilities.invokeLater(() -> nameTextField.requestFocusInWindow());

        inputPanel.add(nameLabel, BorderLayout.WEST);
        inputPanel.add(nameTextField, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        okButton = createStyledButton(">> Mulai", new Color(60, 179, 113)); // Medium Sea Green
        cancelButton = createStyledButton("X Batal", new Color(220, 20, 60)); // Crimson

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add simple decorative elements
        JPanel decorPanel = new JPanel(new FlowLayout());
        decorPanel.setOpaque(false);
        JLabel decorLabel = new JLabel("+ - x / = ? ! *", SwingConstants.CENTER);
        decorLabel.setFont(new Font("Arial", Font.BOLD, 18));
        decorLabel.setForeground(new Color(25, 25, 112));
        decorPanel.add(decorLabel);
        mainPanel.add(decorPanel, BorderLayout.PAGE_END);

        setContentPane(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background with rounded corners
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Subtle border
                g2d.setColor(bgColor.darker());
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void setupEventHandlers() {
        // OK button action
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleOkAction();
            }
        });

        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelAction();
            }
        });

        // Enter key on text field should trigger OK
        nameTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleOkAction();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    handleCancelAction();
                }
            }
        });

        // Window closing should be treated as cancel
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                handleCancelAction();
            }
        });
    }

    private void handleOkAction() {
        String inputName = nameTextField.getText().trim();

        if (inputName.isEmpty()) {
            // Show warning for empty name
            JOptionPane.showMessageDialog(this,
                    "⚠️ Nama pemain tidak boleh kosong!\nSilakan masukkan nama Anda.",
                    "Nama Diperlukan",
                    JOptionPane.WARNING_MESSAGE);
            nameTextField.requestFocusInWindow();
            return;
        }

        if (inputName.length() > 20) {
            // Show warning for name too long
            JOptionPane.showMessageDialog(this,
                    "⚠️ Nama terlalu panjang!\nMaksimal 20 karakter.",
                    "Nama Terlalu Panjang",
                    JOptionPane.WARNING_MESSAGE);
            nameTextField.requestFocusInWindow();
            return;
        }

        // Valid name entered
        playerName = inputName;
        cancelled = false;
        dispose();
    }

    private void handleCancelAction() {
        playerName = null;
        cancelled = true;
        dispose();
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    // Method to show dialog and return result
    public static String showInputDialog(JFrame parentFrame) {
        PlayerInputDialog dialog = new PlayerInputDialog(parentFrame);
        dialog.setVisible(true); // This will block until dialog is closed

        if (dialog.isCancelled()) {
            return null;
        }

        return dialog.getPlayerName();
    }
}