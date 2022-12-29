package processes;

public class ProcessCreationRequest {

    private final int startTime;
    private final int priority;
    private final int cpuTime;
    private final int blocks;
    private final boolean printers;
    private final boolean scanners;
    private final boolean modems;
    private final boolean drivers;

    public ProcessCreationRequest(int startTime,
                                  int priority,
                                  int cpuTime,
                                  int blocks,
                                  boolean printers,
                                  boolean scanners,
                                  boolean modems,
                                  boolean drivers) {
        this.startTime = startTime;
        this.priority = priority;
        this.cpuTime = cpuTime;
        this.blocks = blocks;
        this.printers = printers;
        this.scanners = scanners;
        this.modems = modems;
        this.drivers = drivers;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public int getPriority() {
        return priority;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public int getBlocks() {
        return blocks;
    }

    public boolean hasPrinters() {
        return printers;
    }

    public boolean hasScanners() {
        return scanners;
    }

    public boolean hasModems() {
        return modems;
    }

    public boolean hasDrivers() {
        return drivers;
    }

    @Override
    public String toString() {
        return "ProcessRequest{" +
                "startTime=" + startTime +
                ", priority=" + priority +
                ", cpuTime=" + cpuTime +
                ", blocks=" + blocks +
                ", printers=" + printers +
                ", scanners=" + scanners +
                ", modems=" + modems +
                ", drivers=" + drivers +
                '}';
    }
}
