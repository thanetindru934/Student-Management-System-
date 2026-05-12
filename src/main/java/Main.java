public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            edu.university.sams.ui.DarkTheme.apply();
            new edu.university.sams.gui.LoginWindow().setVisible(true);
        });
    }
}