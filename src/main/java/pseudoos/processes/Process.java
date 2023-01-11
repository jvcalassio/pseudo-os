package processes;

import resources.ResourcesManager;
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
        requestResources();

        while (true) {
            if (Thread.interrupted()) {
                this.status = ProcessStatus.READY;
                break;
            }

            time -= 0.001;

            if (time <= 0) {
                Logger.info("P" + PID + " return SIGINT");
                refoundResources();
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

    private void requestResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if(this.scanners){
            try {
                Logger.info("Alocando scanner para o processo: " + PID);
                resourcesManager.getScanner().getSemaphore().acquire();
                resourcesManager.requestScanner(PID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(this.printers){
            try {
                Logger.info("Alocando impressora para o processo: " + PID);
                resourcesManager.getPrinter().getSemaphore().acquire();
                resourcesManager.requestPrinter(PID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(this.modems){
            try {
                Logger.info("Alocando modem para o processo: " + PID);
                resourcesManager.getModem().getSemaphore().acquire();
                resourcesManager.requestModem(PID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(this.drivers){
            try {
                Logger.info("Alocando sata para o processo: " + PID);
                resourcesManager.getSata().getSemaphore().acquire();
                resourcesManager.requestSata(PID);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void refoundResources(){
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if(this.scanners){
            Logger.info("Liberando scanner do processo: " + PID);
            resourcesManager.getScanner().getSemaphore().release();
            resourcesManager.refoundScanner(PID);
        }
        if(this.printers){
            Logger.info("Liberando impressora do processo: " + PID);
            resourcesManager.getPrinter().getSemaphore().release();
            resourcesManager.refoundPrinter(PID);
        }
        if(this.modems){
            Logger.info("Liberando modem do processo: " + PID);
            resourcesManager.getModem().getSemaphore().release();
            resourcesManager.refoundModem(PID);
        }
        if(this.drivers){
            Logger.info("Liberando sata do processo: " + PID);
            resourcesManager.getSata().getSemaphore().release();
            resourcesManager.refoundSata(PID);
        }
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
