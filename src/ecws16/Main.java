package ecws16;

import javax.swing.*;
import java.awt.*;

public class Main extends JPanel {

    private Simulation simulation;

    public Main() {
        simulation = new Simulation(50);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(30, 30, 30, 30);
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Simulation");
        Main main = new Main();
        frame.add(main);
        frame.setSize(800, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (!main.simulation.simulationIsOver()) {
            main.simulation.simulateTimestep();
            main.repaint();
            Thread.sleep(10);
        }
    }
}
