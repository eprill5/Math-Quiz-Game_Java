package db;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHandler {
    private final String DB_URL = "jdbc:sqlite:mathfun.db";

    public DatabaseHandler() {
        createTable();
        updateTableSchema(); // Add this line to ensure schema is up to date
    }

    private void createTable() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Updated SQL to include 'id' and 'date_played' columns
            String sql = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "score INTEGER NOT NULL, " +
                    "date_played DATETIME DEFAULT CURRENT_TIMESTAMP)";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add this method to handle existing databases that don't have the date_played column
    private void updateTableSchema() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Check if date_played column exists
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "leaderboard", "date_played");

            if (!columns.next()) {
                // Column doesn't exist, add it
                String sql = "ALTER TABLE leaderboard ADD COLUMN date_played DATETIME DEFAULT CURRENT_TIMESTAMP";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("Added date_played column to existing table");

                // Update existing records with current timestamp
                String updateSql = "UPDATE leaderboard SET date_played = datetime('now', 'localtime') WHERE date_played IS NULL";
                stmt.execute(updateSql);
                System.out.println("Updated existing records with current timestamp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveScore(String name, int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Modified SQL to explicitly set the date with local timezone
            String sql = "INSERT INTO leaderboard (name, score, date_played) VALUES (?, ?, datetime('now', 'localtime'))";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("Score saved for " + name + ": " + score + " at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showLeaderboard() {
        showEnhancedLeaderboard();
    }

    private void showEnhancedLeaderboard() {
        JFrame leaderboardFrame = new JFrame("# Papan Skor Tertinggi");
        leaderboardFrame.setSize(700, 500); // Increase width slightly for date column
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 215, 0),
                        0, getHeight(), new Color(255, 255, 224));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("* PAPAN SKOR TERTINGGI *", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(new Color(184, 134, 11)); // Dark golden
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Top 10 Pemain Terbaik MathFun Quiz", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(101, 67, 33)); // Brown
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Create table data - Fixed ASCII characters in column headers
        String[] columnNames = {"# Peringkat", "@ Nama Pemain", "* Skor", "[] Tanggal"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        // Fetch data from database with improved date handling
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // First check if date_played column exists
            String sql;
            boolean hasDateColumn = false;

            try {
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet columns = metaData.getColumns(null, null, "leaderboard", "date_played");
                hasDateColumn = columns.next();
            } catch (SQLException e) {
                // If we can't check metadata, assume no date column
                hasDateColumn = false;
            }

            if (hasDateColumn) {
                // Use SQLite's datetime function to ensure proper formatting
                sql = "SELECT name, score, " +
                        "CASE " +
                        "WHEN date_played IS NULL THEN '-' " +
                        "ELSE date(date_played) " +
                        "END as formatted_date " +
                        "FROM leaderboard ORDER BY score DESC LIMIT 10";
            } else {
                sql = "SELECT name, score, '-' as formatted_date FROM leaderboard ORDER BY score DESC LIMIT 10";
            }

            System.out.println("Executing SQL: " + sql); // Debug output

            ResultSet rs = conn.createStatement().executeQuery(sql);
            int rank = 1;

            while (rs.next()) {
                String rankStr = getRankString(rank);
                String name = rs.getString("name");
                int score = rs.getInt("score");
                String date = rs.getString("formatted_date");

                // Additional date formatting if needed
                if (date != null && !date.equals("-") && date.length() > 10) {
                    date = date.substring(0, 10); // Take only YYYY-MM-DD part
                } else if (date == null) {
                    date = "-";
                }

                System.out.println("Row " + rank + ": " + name + ", " + score + ", " + date); // Debug output

                tableModel.addRow(new Object[]{rankStr, name, score, date});
                rank++;
            }

            // Add empty rows if less than 10 players
            while (rank <= 10) {
                tableModel.addRow(new Object[]{getRankString(rank), "-", "-", "-"});
                rank++;
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(leaderboardFrame,
                    "Gagal memuat data leaderboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create and style table
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(255, 248, 220));
        table.setSelectionForeground(new Color(139, 69, 19));
        table.setGridColor(new Color(218, 165, 32));
        table.setShowGrid(true);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Peringkat
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Nama
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Skor
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Tanggal

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        header.setBackground(new Color(255, 215, 0));
        header.setForeground(new Color(139, 69, 19));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));

        // Custom cell renderer for ranking colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // Color coding for top 3
                    if (row == 0) { // 1st place
                        c.setBackground(new Color(255, 215, 0, 100)); // Gold
                    } else if (row == 1) { // 2nd place
                        c.setBackground(new Color(192, 192, 192, 100)); // Silver
                    } else if (row == 2) { // 3rd place
                        c.setBackground(new Color(205, 127, 50, 100)); // Bronze
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Close button - Fixed ASCII character
        JButton closeButton = new JButton("X Tutup");
        closeButton.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        closeButton.setBackground(new Color(220, 20, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setPreferredSize(new Dimension(120, 40));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> leaderboardFrame.dispose());

        // Stats panel
        JPanel statsPanel = createStatsPanel();

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(closeButton, BorderLayout.EAST);

        // Add components to main panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        leaderboardFrame.add(mainPanel);
        leaderboardFrame.setVisible(true);
    }

    private String getRankString(int rank) {
        switch (rank) {
            case 1: return ">> #1";  // Winner symbol
            case 2: return "<< #2";  // Second place
            case 3: return "^^ #3";  // Third place
            default: return "#" + rank;
        }
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            // Get total players
            String sql1 = "SELECT COUNT(DISTINCT name) as total_players FROM leaderboard";
            ResultSet rs1 = conn.createStatement().executeQuery(sql1);
            int totalPlayers = rs1.next() ? rs1.getInt("total_players") : 0;

            // Get highest score
            String sql2 = "SELECT MAX(score) as highest_score FROM leaderboard";
            ResultSet rs2 = conn.createStatement().executeQuery(sql2);
            int highestScore = rs2.next() ? rs2.getInt("highest_score") : 0;

            // Fixed ASCII characters in stats label
            JLabel statsLabel = new JLabel(String.format(">> Total Pemain: %d | * Skor Tertinggi: %d",
                    totalPlayers, highestScore));
            statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            statsLabel.setForeground(new Color(101, 67, 33));
            statsPanel.add(statsLabel);

        } catch (SQLException e) {
            JLabel errorLabel = new JLabel(">> Data statistik tidak tersedia");
            errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            errorLabel.setForeground(Color.RED);
            statsPanel.add(errorLabel);
        }

        return statsPanel;
    }

    // Method untuk mendapatkan top scores (untuk penggunaan lain)
    public java.util.List<String> getTopScores(int limit) {
        java.util.List<String> topScores = new java.util.ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT name, score FROM leaderboard ORDER BY score DESC LIMIT ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                topScores.add(rs.getString("name") + " - " + rs.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topScores;
    }

    // Debug method to check database content
    public void debugDatabaseContent() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT name, score, date_played FROM leaderboard ORDER BY score DESC LIMIT 5";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            System.out.println("=== Database Content Debug ===");
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("name") +
                        ", Score: " + rs.getInt("score") +
                        ", Raw Date: '" + rs.getString("date_played") + "'");
            }
            System.out.println("=== End Debug ===");
        } catch (SQLException e) {
            System.err.println("Debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}