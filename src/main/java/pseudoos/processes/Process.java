package processes;

import resources.ResourcesManager;
import util.Logger;

public class Process extends Thread {

    public static int processCounter = 0;
    private final Integer PID;
    private Double time;
    private int priority;
    private final int blocks;
    private final int offset;
    private final boolean printers;
    private final boolean scanners;
    private final boolean modems;
    private final boolean sata;
    private ProcessStatus status;
    private int PC;

    public Process(final ProcessCreationRequest processCreationRequest,
                   final int currentMemoryOffset) {
        super("Process-" + processCounter);
        this.PID = processCounter;
        processCounter++;

        this.time = (double) processCreationRequest.getCpuTime();
        this.priority = processCreationRequest.getPriority();
        this.blocks = processCreationRequest.getBlocks();
        this.printers = processCreationRequest.hasPrinters();
        this.scanners = processCreationRequest.hasScanners();
        this.modems = processCreationRequest.hasModems();
        this.sata = processCreationRequest.hasSatas();
        this.offset = currentMemoryOffset;
        this.PC = 0;

        this.ready();
    }

    public int getPID() {
        return PID;
    }

    public Integer getProcessPriority() {
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

    public void blocked() {
        this.status = ProcessStatus.BLOCKED;
    }

    private void requestResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if(this.scanners){
            Logger.info("Alocando scanner para o processo: " + PID);
            if(resourcesManager.getScanner().getSemaphore().tryAcquire()){
                resourcesManager.requestScanner(PID);
            }else{
                blocked();
                try {
                    resourcesManager.getScanner().getSemaphore().acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(this.printers){
            Logger.info("Alocando impressora para o processo: " + PID);
            if(resourcesManager.getPrinter().getSemaphore().tryAcquire()){
                resourcesManager.requestPrinter(PID);
            }else{
                blocked();
                try {
                    resourcesManager.getPrinter().getSemaphore().acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(this.modems){
            Logger.info("Alocando modem para o processo: " + PID);
            if(resourcesManager.getModem().getSemaphore().tryAcquire()){
                resourcesManager.requestModem(PID);
            }else{
                blocked();
                try {
                    resourcesManager.getModem().getSemaphore().acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(this.sata){
            Logger.info("Alocando sata para o processo: " + PID);
            if(resourcesManager.getSata().getSemaphore().tryAcquire()){
                resourcesManager.requestSata(PID);
            }else{
                blocked();
                try {
                    resourcesManager.getSata().getSemaphore().acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
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
        if(this.sata){
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
                ", sata=" + sata +
                ", status=" + status +
                ", PC=" + PC +
                '}';
    }
}
