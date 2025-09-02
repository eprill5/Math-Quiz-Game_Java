package engine;

import model.Soal;
import model.SoalMatematika;
import model.Player;
import ui.PlayerInputDialog;
import db.DatabaseHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameEngine extends JFrame {
    private JLabel soalLabel;
    private JButton[] tombolJawaban;
    private JLabel scoreLabel;
    private JLabel questionCountLabel;
    private Soal currentSoal;
    private Player player;
    private DatabaseHandler dbHandler;
    private int questionCount = 0;
    private int maxQuestions = 10;
    private int timeLeft = 30;
    private Timer gameTimer;
    private JLabel timerLabel;

    public GameEngine() {
        // Inisialisasi database handler
        dbHandler = new DatabaseHandler();

        // Setup UI terlebih dahulu
        setupUI();

        // Minta input nama pemain
        initializePlayer();

        // Mulai game jika pemain valid
        if (player != null) {
            startGame();
        } else {
            // Jika pemain membatalkan input nama, kembali ke menu
            dispose();
            return;
        }
    }

    private void initializePlayer() {
        String playerName = PlayerInputDialog.showInputDialog(this);
        if (playerName != null && !playerName.trim().isEmpty()) {
            player = new Player(playerName);
            updateUI();
        } else {
            player = null;
        }
    }

    private void setupUI() {
        setTitle("MathFun Quiz - Game Mode");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel dengan gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 250),
                        0, getHeight(), new Color(152, 251, 152));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(">> MathFun Quiz <<", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(25, 25, 112));

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        infoPanel.setOpaque(false);

        scoreLabel = new JLabel("* Skor: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(70, 130, 180));

        questionCountLabel = new JLabel("# Soal: 0/" + maxQuestions);
        questionCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        questionCountLabel.setForeground(new Color(70, 130, 180));

        timerLabel = new JLabel("@ Waktu: " + timeLeft + "s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(new Color(220, 20, 60));

        infoPanel.add(scoreLabel);
        infoPanel.add(questionCountLabel);
        infoPanel.add(timerLabel);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoPanel, BorderLayout.CENTER);

        // Soal panel
        JPanel soalPanel = new JPanel(new BorderLayout());
        soalPanel.setOpaque(false);
        soalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Custom JLabel dengan background yang solid
        soalLabel = new JLabel("? Soal akan ditampilkan di sini...", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                // Gambar background putih solid terlebih dahulu
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background putih solid dengan opacity penuh
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2d.setColor(new Color(200, 200, 200));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 15, 15);

                // Gambar background putih lagi di atas shadow
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border final
                g2d.setColor(new Color(180, 180, 180));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                // Panggil super untuk menggambar teks
                super.paintComponent(g);
            }
        };

        soalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        soalLabel.setForeground(new Color(25, 25, 112));
        soalLabel.setPreferredSize(new Dimension(800, 100));
        soalLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        soalLabel.setOpaque(false); // Set false karena kita custom paint
        soalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        soalLabel.setVerticalAlignment(SwingConstants.CENTER);

        soalPanel.add(soalLabel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        tombolJawaban = new JButton[4];
        String[] buttonLabels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            tombolJawaban[i] = createAnswerButton(">> Jawaban " + buttonLabels[i], i);
            buttonPanel.add(tombolJawaban[i]);
        }

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setOpaque(false);

        JButton backButton = createControlButton("<< Kembali ke Menu", new Color(220, 20, 60));
        backButton.addActionListener(e -> {
            if (gameTimer != null) gameTimer.stop();
            dispose();
            // Kembali ke menu utama
            SwingUtilities.invokeLater(() -> {
                try {
                    Class<?> menuClass = Class.forName("ui.MenuUtama");
                    Object menuInstance = menuClass.getDeclaredConstructor().newInstance();
                    menuClass.getMethod("showMenu").invoke(menuInstance);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        controlPanel.add(backButton);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(soalPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add control panel to bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(controlPanel, BorderLayout.CENTER);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setOpaque(false);
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(containerPanel);
    }

    private JButton createAnswerButton(String text, int index) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = new Color(70, 130, 180);
                if (getModel().isPressed()) {
                    bgColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = bgColor.brighter();
                }

                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(bgColor.darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(300, 60));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> periksaJawaban(index));

        return button;
    }

    private JButton createControlButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor = bgColor;
                if (getModel().isPressed()) {
                    currentColor = currentColor.darker();
                } else if (getModel().isRollover()) {
                    currentColor = currentColor.brighter();
                }

                g2d.setColor(currentColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    public void startGame() {
        if (player == null) {
            dispose();
            return;
        }

        // Reset game state
        questionCount = 0;
        timeLeft = 30;

        // Update UI
        updateUI();

        // Tampilkan soal pertama
        tampilkanSoalBaru();

        // Mulai timer
        startTimer();

        // Set window visible
        setVisible(true);
    }

    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("@ Waktu: " + timeLeft + "s");

                if (timeLeft <= 10) {
                    timerLabel.setForeground(Color.RED);
                } else {
                    timerLabel.setForeground(new Color(220, 20, 60));
                }

                if (timeLeft <= 0) {
                    gameTimer.stop();
                    // Waktu habis, jawaban salah
                    JOptionPane.showMessageDialog(GameEngine.this,
                            "@ Waktu habis!\nJawaban yang benar: " +
                                    currentSoal.getOptions()[currentSoal.getAnswerIndex()],
                            "Waktu Habis", JOptionPane.WARNING_MESSAGE);
                    nextQuestion();
                }
            }
        });
        gameTimer.start();
    }

    private void tampilkanSoalBaru() {
        currentSoal = new SoalMatematika();

        // Update soal label
        soalLabel.setText(currentSoal.getQuestion());

        // Update tombol jawaban
        String[] opsi = currentSoal.getOptions();
        String[] buttonLabels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            tombolJawaban[i].setText(buttonLabels[i] + ". " + opsi[i]);
            tombolJawaban[i].setEnabled(true);
        }

        // Reset timer
        timeLeft = 30;
        timerLabel.setText("@ Waktu: " + timeLeft + "s");
        timerLabel.setForeground(new Color(220, 20, 60));

        // Update question counter
        questionCount++;
        questionCountLabel.setText("# Soal: " + questionCount + "/" + maxQuestions);

        System.out.println("Soal ditampilkan: " + currentSoal.getQuestion()); // Debug
    }

    private void periksaJawaban(int index) {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Disable all buttons
        for (JButton button : tombolJawaban) {
            button.setEnabled(false);
        }

        boolean benar = currentSoal.checkAnswer(index);

        if (benar) {
            // Jawaban benar - SKOR SISTEM BARU: 10 poin per jawaban benar
            int poin = 10; // Skor tetap 10 untuk setiap jawaban benar
            player.addScore(poin);
            updateUI();

            JOptionPane.showMessageDialog(this,
                    "<3 Jawaban Benar!\n" +
                            "Poin yang didapat: " + poin + "\n" +
                            "Total Skor: " + player.getScore(),
                    "Benar!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Jawaban salah - tidak mendapat poin
            JOptionPane.showMessageDialog(this,
                    "X Jawaban Salah!\n" +
                            "Jawaban yang benar: " + currentSoal.getOptions()[currentSoal.getAnswerIndex()] + "\n" +
                            "Total Skor: " + player.getScore(),
                    "Salah!", JOptionPane.ERROR_MESSAGE);
        }

        nextQuestion();
    }

    private void nextQuestion() {
        if (questionCount >= maxQuestions) {
            // Game selesai
            endGame();
        } else {
            // Lanjut ke soal berikutnya
            tampilkanSoalBaru();
            startTimer();
        }
    }

    private void endGame() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // Simpan skor ke database
        dbHandler.saveScore(player.getName(), player.getScore());

        // Hitung persentase keberhasilan
        int correctAnswers = player.getScore() / 10; // Karena setiap benar = 10 poin
        double percentage = (correctAnswers / (double) maxQuestions) * 100;

        // Tampilkan hasil akhir dengan informasi yang lebih detail
        String performanceMessage = "";
        if (percentage == 100) {
            performanceMessage = "** SEMPURNA! Anda menjawab semua soal dengan benar!";
        } else if (percentage >= 80) {
            performanceMessage = "* EXCELLENT! Performa yang sangat baik!";
        } else if (percentage >= 60) {
            performanceMessage = "+ GOOD! Cukup baik, terus berlatih!";
        } else if (percentage >= 40) {
            performanceMessage = "# KEEP LEARNING! Perlu lebih banyak latihan!";
        } else {
            performanceMessage = ">> DON'T GIVE UP! Terus semangat belajar!";
        }

        int choice = JOptionPane.showConfirmDialog(this,
                ">> Permainan Selesai!\n\n" +
                        "@ Pemain: " + player.getName() + "\n" +
                        "* Skor Akhir: " + player.getScore() + "/" + (maxQuestions * 10) + "\n" +
                        "+ Jawaban Benar: " + correctAnswers + "/" + maxQuestions + "\n" +
                        "# Persentase: " + String.format("%.1f", percentage) + "%\n\n" +
                        performanceMessage + "\n\n" +
                        "Apakah Anda ingin bermain lagi?",
                "Permainan Selesai",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Mulai game baru
            dispose();
            SwingUtilities.invokeLater(() -> new GameEngine());
        } else {
            // Kembali ke menu
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    Class<?> menuClass = Class.forName("ui.MenuUtama");
                    Object menuInstance = menuClass.getDeclaredConstructor().newInstance();
                    menuClass.getMethod("showMenu").invoke(menuInstance);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void updateUI() {
        if (player != null) {
            scoreLabel.setText("* Skor: " + player.getScore());
            setTitle("MathFun Quiz - " + player.getName());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new GameEngine();
        });
    }
}