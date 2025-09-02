package ui;

import engine.GameEngine;
import db.DatabaseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuUtama extends JFrame {
    private JPanel mainPanel;
    private JButton startButton, leaderboardButton, exitButton, fullscreenButton;
    private JLabel titleLabel, subtitleLabel, descriptionLabel;
    private DatabaseHandler dbHandler;
    private boolean isFullscreen = false;
    private Rectangle normalBounds;

    public MenuUtama() {
        dbHandler = new DatabaseHandler();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setFrameProperties();
    }

    private void initializeComponents() {
        // Set Nimbus Look and Feel untuk tampilan modern
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Main Panel dengan gradient background yang responsif
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background yang menyesuaikan ukuran
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 250),
                        0, getHeight(), new Color(152, 251, 152));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Tambahkan pattern dekoratif
                drawDecorationPattern(g2d);
            }
        };
        mainPanel.setLayout(new BorderLayout(15, 15));

        // Title dengan styling yang responsif
        titleLabel = new JLabel("* MathFun Quiz *", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(new Color(25, 25, 112));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Subtitle
        subtitleLabel = new JLabel("Game Edukasi Matematika Interaktif", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(70, 130, 180));

        // Description dengan HTML untuk formatting yang lebih baik
        descriptionLabel = new JLabel("<html><div style='text-align: center; padding: 5px;'>" +
                "<b>Latih kemampuan matematika dengan cara yang menyenangkan!</b><br><br>" +
                "• Soal matematika dasar yang menarik<br>" +
                "• Sistem poin dan leaderboard<br>" +
                "• Interface yang user-friendly<br>" +
                "• Raih skor tertinggi dan jadilah yang terbaik!" +
                "</div></html>", SwingConstants.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(47, 79, 79));
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 30));

        // Buttons dengan styling yang diperbaiki
        startButton = createStyledButton(">> Mulai Bermain", new Color(60, 179, 113), Color.WHITE, new Dimension(200, 50));
        leaderboardButton = createStyledButton("# Papan Skor", new Color(255, 165, 0), Color.WHITE, new Dimension(200, 50));
        fullscreenButton = createStyledButton("[] Toggle Fullscreen", new Color(138, 43, 226), Color.WHITE, new Dimension(200, 40));
        exitButton = createStyledButton("X Keluar", new Color(220, 20, 60), Color.WHITE, new Dimension(200, 50));
    }

    private void drawDecorationPattern(Graphics2D g2d) {
        // Gambar lingkaran-lingkaran dekoratif dengan transparansi
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
        g2d.setColor(Color.WHITE);

        int width = getWidth();
        int height = getHeight();

        // Lingkaran besar di pojok
        g2d.fillOval(-50, -50, 200, 200);
        g2d.fillOval(width - 150, height - 150, 200, 200);

        // Lingkaran kecil tersebar
        for (int i = 0; i < 15; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = (int) (Math.random() * 30 + 10);
            g2d.fillOval(x, y, size, size);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Dimension size) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth(), getHeight(), 25, 25);

                // Button background dengan rounded corners
                Color currentColor;
                if (getModel().isPressed()) {
                    currentColor = bgColor.darker().darker();
                } else if (getModel().isRollover()) {
                    currentColor = bgColor.brighter();
                } else {
                    currentColor = bgColor;
                }

                // Gradient untuk button
                GradientPaint buttonGradient = new GradientPaint(0, 0, currentColor.brighter(),
                        0, getHeight(), currentColor.darker());
                g2d.setPaint(buttonGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Border dengan efek glossy
                g2d.setColor(currentColor.darker().darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);

                // Highlight di bagian atas
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 100));
                    g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()/2, 25, 25);
                }

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setForeground(fgColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(size);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Enhanced hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.repaint();
                // Tambahkan efek scaling halus
                Timer scaleTimer = new Timer(10, null);
                scaleTimer.addActionListener(evt -> {
                    button.repaint();
                });
                scaleTimer.setRepeats(false);
                scaleTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });

        return button;
    }

    private void setupLayout() {
        // Header Panel dengan spacing yang lebih baik
        JPanel headerPanel = new JPanel(new BorderLayout(0, 10));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel subtitlePanel = new JPanel(new BorderLayout(0, 5));
        subtitlePanel.setOpaque(false);
        subtitlePanel.add(subtitleLabel, BorderLayout.NORTH);
        subtitlePanel.add(descriptionLabel, BorderLayout.CENTER);

        headerPanel.add(subtitlePanel, BorderLayout.CENTER);

        // Button Panel dengan layout yang lebih rapi dan kompak
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Main buttons dengan spacing yang lebih kecil
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(startButton, gbc);

        gbc.gridy = 1;
        buttonPanel.add(leaderboardButton, gbc);

        // Fullscreen button dengan spacing yang lebih kecil
        gbc.gridy = 2;
        gbc.insets = new Insets(15, 0, 10, 0);
        buttonPanel.add(fullscreenButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 0, 10, 0);
        buttonPanel.add(exitButton, gbc);

        // Control Panel untuk shortcut keys info
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setOpaque(false);
        JLabel shortcutLabel = new JLabel("<html><center>@ Shortcut Keys:<br>" +
                "F11 - Toggle Fullscreen | ESC - Exit Fullscreen</center></html>");
        shortcutLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        shortcutLabel.setForeground(new Color(105, 105, 105));
        controlPanel.add(shortcutLabel);

        // Footer dengan informasi tambahan
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JLabel footerLabel = new JLabel("Dibuat dengan <3 untuk pembelajaran matematika yang menyenangkan",
                SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        footerLabel.setForeground(new Color(105, 105, 105));

        footerPanel.add(controlPanel, BorderLayout.NORTH);
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        // Add to main panel dengan padding yang lebih kecil
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        // Start button dengan animasi
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            Timer timer = new Timer(150, evt -> {
                startButton.setEnabled(true);
                dispose();
                GameEngine game = new GameEngine();
                game.startGame();
            });
            timer.setRepeats(false);
            timer.start();
        });

        // Leaderboard button
        leaderboardButton.addActionListener(e -> {
            leaderboardButton.setEnabled(false);
            Timer timer = new Timer(150, evt -> {
                leaderboardButton.setEnabled(true);
                dbHandler.showLeaderboard();
            });
            timer.setRepeats(false);
            timer.start();
        });

        // Fullscreen toggle button
        fullscreenButton.addActionListener(e -> toggleFullscreen());

        // Exit button dengan konfirmasi
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin keluar dari MathFun Quiz?",
                    "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                // Animasi fade out
                Timer fadeTimer = new Timer(50, null);
                final float[] alpha = {1.0f};
                fadeTimer.addActionListener(evt -> {
                    alpha[0] -= 0.1f;
                    if (alpha[0] <= 0) {
                        fadeTimer.stop();
                        System.exit(0);
                    }
                    repaint();
                });
                fadeTimer.start();
            }
        });

        // Keyboard shortcuts
        setupKeyboardShortcuts();

        // Window state listener untuk fullscreen
        addWindowStateListener(e -> {
            if (e.getNewState() == Frame.NORMAL && isFullscreen) {
                isFullscreen = false;
                updateFullscreenButtonText();
            }
        });

        // Component listener untuk responsive design
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustFontSizes();
                repaint();
            }
        });
    }

    private void setupKeyboardShortcuts() {
        // F11 untuk toggle fullscreen
        KeyStroke f11 = KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(f11, "toggleFullscreen");
        getRootPane().getActionMap().put("toggleFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullscreen();
            }
        });

        // ESC untuk exit fullscreen
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "exitFullscreen");
        getRootPane().getActionMap().put("exitFullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFullscreen) {
                    toggleFullscreen();
                }
            }
        });

        // Enter untuk start game
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enter, "startGame");
        getRootPane().getActionMap().put("startGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.doClick();
            }
        });
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            // Exit fullscreen
            dispose();
            setUndecorated(false);
            setBounds(normalBounds);
            setVisible(true);
            isFullscreen = false;
        } else {
            // Enter fullscreen
            normalBounds = getBounds();
            dispose();
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);
            isFullscreen = true;
        }
        updateFullscreenButtonText();
        adjustFontSizes();
    }

    private void updateFullscreenButtonText() {
        if (isFullscreen) {
            fullscreenButton.setText("O Exit Fullscreen");
        } else {
            fullscreenButton.setText("[] Toggle Fullscreen");
        }
    }

    private void adjustFontSizes() {
        // Responsive font sizing berdasarkan ukuran window
        int width = getWidth();
        int height = getHeight();
        int baseSize = Math.max(Math.min(width / 35, height / 35), 20); // Minimum 20, scale with both dimensions

        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, Math.min(baseSize + 16, 50)));
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, Math.min(baseSize - 2, 22)));
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, Math.min(baseSize - 6, 16)));

        // Adjust button fonts
        Font buttonFont = new Font("Comic Sans MS", Font.BOLD, Math.min(baseSize - 4, 18));
        startButton.setFont(buttonFont);
        leaderboardButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);
        fullscreenButton.setFont(new Font("Comic Sans MS", Font.BOLD, Math.min(baseSize - 6, 14)));
    }

    private void setFrameProperties() {
        setTitle("MathFun Quiz - Menu Utama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 750); // Ukuran yang lebih proporsional
        setMinimumSize(new Dimension(550, 650)); // Minimum size yang lebih kecil
        setLocationRelativeTo(null);
        add(mainPanel);

        // Set icon jika tersedia
        try {
            // Uncomment jika memiliki icon file
            // setIconImage(Toolkit.getDefaultToolkit().getImage("resources/icon.png"));
        } catch (Exception e) {
            // Icon tidak tersedia, gunakan default
        }

        // Initial font adjustment
        SwingUtilities.invokeLater(this::adjustFontSizes);
    }

    public void showMenu() {
        setVisible(true);
        // Focus pada start button secara default
        SwingUtilities.invokeLater(() -> startButton.requestFocusInWindow());
    }

    // Method untuk mendapatkan status fullscreen (untuk digunakan oleh kelas lain)
    public boolean isFullscreenMode() {
        return isFullscreen;
    }

    public static void main(String[] args) {
        // Set system properties untuk rendering yang lebih baik
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.translaccel", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                System.err.println("Nimbus Look and Feel tidak tersedia, menggunakan default.");
            }

            new MenuUtama().showMenu();
        });
    }
}