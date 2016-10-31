package ecws16;

import java.util.ArrayList;

public class PM {
    /*
    Size, consumed memory, consumed CPU, consumed network bandwidth.
– Energy signature:
• linear combination of the CPU, memory and network workload, but not
equal to zero in the idle state. It depends on the consumed resources, e.g.
the same workload of CPU and network will end up into different energy
consumption rates on different machines.Energy utilization function of
a machine is given as follows:
U = U 0 + W cpu U cpu + W mem U mem + W network U network
(1)
where U is the total energy utilization, U 0 is the energy utilization in the idle
state when no resources are consumed, U i is the energy utilizations considering
resources at the highest possible workload, W i are workload rates.
     */
    private static final double U_0 = 100;
    private static final double U_cpu = 100;
    private static final double U_mem = 100;
    private static final double U_network = 100;
    private static final double FAILURE_PROBABILITY = 0.001;
    private static final long restartDuration = 5;

    private ArrayList<VM> VMs;
    private long timeLeftUntilRestarted;


    public PM() {
        this.VMs = new ArrayList<>();
        timeLeftUntilRestarted = 0;
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

    public void restart() {
        timeLeftUntilRestarted = restartDuration;
    }

    public void timeStep() {
        if (timeLeftUntilRestarted > 0) {
            timeLeftUntilRestarted--;
        } else {
            if (Math.random() < FAILURE_PROBABILITY) {
                restart();
            }
            for (int i = 0; i < VMs.size(); i++) {
                VM vm = VMs.get(i);
                vm.timeStep();
                if (vm.isFinished()) {
                    VMs.remove(i);
                    i--;
                }
            }
        }
    }
}
