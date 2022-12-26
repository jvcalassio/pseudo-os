package processes;

public class Process {

    public static int processCounter = 0;
    private final int PID;
    private int time;
    private int priority;
    private final int blocks;
    private final int offset;
    private final boolean printers;
    private final boolean scanners;
    private final boolean modems;
    private final boolean drivers;

    public Process(final ProcessCreationRequest processCreationRequest,
                   final int currentMemoryOffset) {
        this.PID = processCounter;
        processCounter++;

        this.time = processCreationRequest.getCpuTime();
        this.priority = processCreationRequest.getPriority();
        this.blocks = processCreationRequest.getBlocks();
        this.printers = processCreationRequest.hasPrinters();
        this.scanners = processCreationRequest.hasScanners();
        this.modems = processCreationRequest.hasModems();
        this.drivers = processCreationRequest.hasDrivers();
        this.offset = currentMemoryOffset;
    }

    @Override
    public String toString() {
        return "Process{" +
                "PID=" + PID +
                ", time=" + time +
                ", priority=" + priority +
                ", blocks=" + blocks +
                ", offset=" + offset +
                ", printers=" + printers +
                ", scanners=" + scanners +
                ", modems=" + modems +
                ", drivers=" + drivers +
                '}';
    }
}
