package ecws16;

import java.util.ArrayList;

public class PM {
    private static final double U_0 = 100;
    private static final double U_cpu = 100;
    private static final double U_mem = 100;
    private static final double U_network = 100;

    private ArrayList<VM> VMs;


    public PM() {
        this.VMs = new ArrayList<>();
    }

    public double getWorkloadCPU() {
        // TODO
        return 0;
    }

    public double getWorkloadMem() {
        // TODO
        return 0;
    }

    public double getWorkloadNetwork() {
        // TODO
        return 0;
    }

    public double getEnergyUtilization() {
        double energyUtilization = U_0;
        energyUtilization += U_cpu * getWorkloadCPU();
        energyUtilization += U_mem * getWorkloadCPU();
        energyUtilization += U_network * getWorkloadCPU();
        return energyUtilization;
    }
}
