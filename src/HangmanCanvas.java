import javax.swing.*;
import java.awt.*;

// creates a panel to paint on and will override the paintComponent function in that panel to create our custom hangman
public class HangmanCanvas extends JPanel {
    private int stickmanStage;

    public void setStage(int stickmanStage) {
        this.stickmanStage = stickmanStage;
    }

    @Override
    // javax.swing.JComponent factors the paint method into three methods, one of which is the paintComponent method
    // this method is involved to paint the hangman using the abstract graphics class to draw the components
    protected void paintComponent(Graphics g) {
        // by calling super the UI Delegate paints first. This includes background filling since this component is opaque.
        super.paintComponent(g);
        // class to render 2D shapes, such as my stickman
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(50, 350, 200, 350);
        g2d.drawLine(125, 350, 125, 50);
        g2d.drawLine(125, 50, 300, 50);
        g2d.drawLine(300, 50, 300, 100);
        // everytime the panel is updated these conditional statements will run and draw up to a certain amount of the stickman
        if (stickmanStage > 0) {
            g2d.drawOval(275, 100, 50, 50);
        }
        if (stickmanStage > 1) {
            g2d.drawLine(300, 150, 300, 250);
        }
        if (stickmanStage > 2) {
            g2d.drawLine(300, 170, 250, 200);
        }
        if (stickmanStage > 3) {
            g2d.drawLine(300, 170, 350, 200);
        }
        if (stickmanStage > 4) {
            g2d.drawLine(300, 250, 250, 300);
        }
        if (stickmanStage > 5) {
            g2d.drawLine(300, 250, 350, 300);
        }
    }
}
