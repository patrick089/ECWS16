package ecws16;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

public class Main extends JPanel {

    private static final int FRAME_HEIGHT = 768;
    private static final int FRAME_WIDTH = 1024;
    private static final int SCALE = 8*7;
    private static final int HALF_SCALE = SCALE / 2;
    private static final int QUARTER_SCALE = HALF_SCALE / 2;
    private static final int EIGHTH_SCALE = QUARTER_SCALE / 2;
    private static ButtonGroup modusGroup;
    private static JSlider durationSlider;
    private Controller controller;
    private Simulation simulation;

    public Main() {
        initiateSimulation();
    }

    //need input parameter time, modus
    public static void main(String[] args) throws Exception {
        modusGroup = new ButtonGroup();
        JLabel durationLabel = new JLabel("Duration (100 time steps):");
        durationSlider = new JSlider(JSlider.HORIZONTAL,
                0, 2000, 100);
        durationSlider.setMajorTickSpacing(500);
        durationSlider.setMinorTickSpacing(100);
        durationSlider.setPaintTicks(true);
        durationSlider.setPaintLabels(true);

        JFrame frame = new JFrame("Simulation");
        Main main = new Main();
        JButton restartButton = new JButton("Restart Simulation");
        restartButton.addActionListener(e -> {
            main.initiateSimulation();
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(
                buttonsPanel, BoxLayout.PAGE_AXIS));

        buttonsPanel.add(Box.createVerticalStrut(30));

        buttonsPanel.add(durationLabel);
        buttonsPanel.add(durationSlider);

        buttonsPanel.add(Box.createVerticalStrut(30));

        JLabel timestepLabel = new JLabel("Simulation Speed (1000 ms interval)");
        buttonsPanel.add(timestepLabel);
        JSlider timestepSlider = new JSlider(JSlider.HORIZONTAL,
                0, 2000, 1000);
        timestepSlider.setMajorTickSpacing(500);
        timestepSlider.setMinorTickSpacing(100);
        timestepSlider.setPaintTicks(true);
        timestepSlider.setPaintLabels(true);
        buttonsPanel.add(timestepSlider);

        buttonsPanel.add(Box.createVerticalStrut(30));

        JRadioButton modus1 = new JRadioButton("1: no retry - migrate random");
        modusGroup.add(modus1);
        buttonsPanel.add(modus1);
        JRadioButton modus2 = new JRadioButton("2: no retry - migrate random + SLA");
        modusGroup.add(modus2);
        buttonsPanel.add(modus2);
        JRadioButton modus3 = new JRadioButton("3: retry - migrate random");
        modusGroup.add(modus3);
        buttonsPanel.add(modus3);
        JRadioButton modus4 = new JRadioButton("4: retry - migrate random + SLA");
        modusGroup.add(modus4);
        buttonsPanel.add(modus4);
        modus1.setSelected(true);

        buttonsPanel.add(Box.createVerticalStrut(30));

        buttonsPanel.add(restartButton);

        buttonsPanel.add(Box.createVerticalStrut(30));

        frame.setLayout(new BorderLayout());
        frame.add(main, BorderLayout.CENTER);
        frame.add(buttonsPanel, BorderLayout.EAST);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (true) {
            if (!main.simulation.simulationIsOver()) {
                main.simulation.simulateTimestep();
                main.repaint();
            }
            int ts = new Integer(timestepSlider.getValue());
            timestepLabel.setText("Simulation Speed ("+ts+" ms interval)");
            durationLabel.setText("Duration ("+durationSlider.getValue()+" time steps):");
            Thread.sleep(ts);
        }
    }

    private void initiateSimulation() {
        // Detect modus
        int modus = 1;
        for (Enumeration<AbstractButton> buttons = modusGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                modus = new Integer(button.getText().substring(0,1));
            }
        }

        // Initialize simulation
        int duration = new Integer(durationSlider.getValue());
        //please change!
        double failureProbability = 0.2;
        controller = new Controller(duration, 10, modus, failureProbability);
        simulation = controller.getSimulation();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(HALF_SCALE, HALF_SCALE, SCALE+SCALE * simulation.getMapWidth(), SCALE+SCALE * simulation.getMapHeight());

        // Draw grid
        g2d.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= simulation.getMapWidth(); x++) {
            g2d.drawLine(SCALE + x * SCALE, SCALE, SCALE + x * SCALE, SCALE+SCALE * simulation.getMapWidth());
        }
        for (int y = 0; y <= simulation.getMapHeight(); y++) {
            g2d.drawLine(SCALE, SCALE + y * SCALE, SCALE+SCALE * simulation.getMapHeight(), SCALE + y * SCALE);
        }

        // Draw connections
        for (Edge edge : simulation.getEdges()) {
            g2d.setColor(Color.BLACK);
            for (PM pm : edge.getPms()) {
                for (VM vm : pm.getVms()) {
                    for (Request request : vm.getRequests()) {
                        g2d.drawLine(SCALE + (int)(request.getLocation().getX() * SCALE), SCALE + (int)(request.getLocation().getY() * SCALE), SCALE + (int)(edge.getLocation().getX() * SCALE), SCALE + (int)(edge.getLocation().getY() * SCALE));
                    }
                }
            }
        }

        // Draw users
        for (User user : simulation.getUsers()) {
            if (user.getRequest().isFinished(simulation.getCurrentTime())) {
                g2d.setColor(Color.GRAY);
            } else {
                g2d.setColor(Color.BLUE);
            }
            g2d.fillOval(SCALE - EIGHTH_SCALE + (int)(user.getRequest().getLocation().getX() * SCALE), SCALE - EIGHTH_SCALE + (int)(user.getRequest().getLocation().getY() * SCALE), QUARTER_SCALE, QUARTER_SCALE);
        }

        // Draw edges
        for (Edge edge : simulation.getEdges()) {
            g2d.setColor(Color.RED);
            g2d.fillRect(SCALE - QUARTER_SCALE +(int)( edge.getLocation().getX() * SCALE), SCALE - QUARTER_SCALE + (int)(edge.getLocation().getY() * SCALE), HALF_SCALE, HALF_SCALE);
        }

        // Draw information
        g2d.setColor(Color.BLACK);
        int y = SCALE * simulation.getMapHeight() + 2*SCALE;
        g2d.drawString("RED=edges, BLUE=active users, GRAY=inactive users, BLACK=connections", 10, y);
        y+= 20;
        g2d.drawString("Step " + simulation.getCurrentTime() + "/" + simulation.getDuration(), 10, y);
        g2d.drawString("modus=" + simulation.getModus(), 110, y);
    }
}
