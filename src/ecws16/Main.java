package ecws16;

import javax.swing.*;
import java.awt.*;

public class Main extends JPanel {

    private static final int SCALE = 40;
    private static final int QUARTER_SCALE = 10;
    private static final int HALF_SCALE = 20;
    private static final int FRAME_HEIGHT = 800;
    private static final int FRAME_WIDTH = 800;
    private Controller controller;
    private Simulation simulation;

    public Main() {
        controller = new Controller(10,1);
        simulation = controller.getSimulation();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        for (int x = 0; x < simulation.getMapWidth(); x++) {
            g2d.drawLine(SCALE+x*SCALE, 0, SCALE+x*SCALE, FRAME_HEIGHT);
        }
        for (int y = 0; y < simulation.getMapHeight(); y++) {
            g2d.drawLine(0,SCALE+ y*SCALE,FRAME_WIDTH,SCALE+ y*SCALE);
        }
        for (Edge edge : simulation.getEdges()) {
            g2d.setColor(Color.BLACK);
            g2d.fillOval(SCALE-QUARTER_SCALE+edge.getLocation().getX()*SCALE, SCALE-QUARTER_SCALE+ edge.getLocation().getY()*SCALE, HALF_SCALE, HALF_SCALE);
        }
    }
    //need input parameter time, modus
    public static void main(String[] args) throws InterruptedException {
        JFrame frame = new JFrame("Simulation");
        Main main = new Main();
        frame.add(main);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (!main.simulation.simulationIsOver()) {
            main.simulation.simulateTimestep();
            main.repaint();
            Thread.sleep(10);
        }
    }
}
