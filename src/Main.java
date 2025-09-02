import ui.MenuUtama;

public class Main {
    public static void main(String[] args) {
        // Set system properties untuk tampilan yang lebih baik
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Jalankan aplikasi di Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Set Look and Feel ke Nimbus untuk tampilan modern
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                    System.err.println("Nimbus Look and Feel tidak tersedia, menggunakan default.");
                }

                // Tampilkan menu utama
                MenuUtama menuUtama = new MenuUtama();
                menuUtama.showMenu();
            }
        });
    }
}