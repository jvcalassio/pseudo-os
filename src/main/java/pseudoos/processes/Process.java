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
        this.PC = 0;

        this.ready();
    }

    public int getPID() {
        return PID;
    }

    public Integer getPriority() {
        return priority;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    protected void block() {
        this.status = ProcessStatus.BLOCKED;
    }

    public int getOffset() {
        return offset;
    }

    public int getBlocks() {
        return blocks;
    }

    public void run() {
        this.status = ProcessStatus.RUNNING;

        while (true) {
            if (Thread.interrupted()) {
                this.status = ProcessStatus.READY;
                break;
            }

            time -= 0.001;

            if (time <= 0) {
                Logger.info("P" + PID + " return SIGINT");
                break;
            }

            double roundedTime = time.intValue();
            if (time - roundedTime < 0.001) {
                PC++;
                Logger.info("P" + PID + " instruction " + PC);
            }
        }
    }

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
