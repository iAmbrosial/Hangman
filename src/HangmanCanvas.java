import javax.swing.*;
import java.awt.*;

public class HangmanCanvas extends JPanel {
    private int stage;
    public void setStage(int stage) {
        this.stage = stage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(50, 350, 200, 350);
        g2d.drawLine(125, 350, 125, 50);
        g2d.drawLine(125, 50, 300, 50);
        g2d.drawLine(300, 50, 300, 100);
        if (stage > 0) {
            g2d.drawOval(275, 100, 50, 50);
        }
        if (stage > 1) {
            g2d.drawLine(300, 150, 300, 250);
        }
        if (stage > 2) {
            g2d.drawLine(300, 170, 250, 200);
        }
        if (stage > 3) {
            g2d.drawLine(300, 170, 350, 200);
        }
        if (stage > 4) {
            g2d.drawLine(300, 250, 250, 300);
        }
        if (stage > 5) {
            g2d.drawLine(300, 250, 350, 300);
        }
    }
}
