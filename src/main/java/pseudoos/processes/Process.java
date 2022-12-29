package processes;

import util.Logger;

public class Process {

    public static int processCounter = 0;
    private final Integer PID;
    private Double time;
    private int priority;
    private final int blocks;
    private final int offset;
    private final boolean printers;
    private final boolean scanners;
    private final boolean modems;
    private final boolean drivers;
    private ProcessStatus status;
    private int PC;

    public Process(final ProcessCreationRequest processCreationRequest,
                   final int currentMemoryOffset) {
        this.PID = processCounter;
        processCounter++;

        this.time = (double) processCreationRequest.getCpuTime();
        this.priority = processCreationRequest.getPriority();
        this.blocks = processCreationRequest.getBlocks();
        this.printers = processCreationRequest.hasPrinters();
        this.scanners = processCreationRequest.hasScanners();
        this.modems = processCreationRequest.hasModems();
        this.drivers = processCreationRequest.hasDrivers();
        this.offset = currentMemoryOffset;
        this.PC = currentMemoryOffset;

        this.ready();
    }

    public int getPID() {
        return PID;
    }

    public Integer getPriority() {
        return priority;
    }

    public void block() {
        this.status = ProcessStatus.BLOCKED;
    }

    public void run() { this.status = ProcessStatus.RUNNING; }

    public void ready() {
        this.status = ProcessStatus.READY;
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
                ", status=" + status +
                ", PC=" + PC +
                '}';
    }
}