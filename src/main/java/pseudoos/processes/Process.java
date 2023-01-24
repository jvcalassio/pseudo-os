package processes;

import resources.ResourcesManager;
import util.Logger;

import java.util.concurrent.Semaphore;

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
    private final boolean sata;
    private ProcessStatus status = ProcessStatus.CREATED;
    private int PC;
    private final Semaphore statusSemaphore;

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
        this.sata = processCreationRequest.hasSatas();
        this.offset = currentMemoryOffset;
        this.PC = 0;

        this.statusSemaphore = new Semaphore(1);
    }

    public void run() {
        requestResources();
        if (status == ProcessStatus.BLOCKED) {
            return;
        }

        this.running();

        while (status == ProcessStatus.RUNNING && !Thread.currentThread().isInterrupted()) {
            time -= 0.001;

            if (time <= 0) {
                finished();
                break;
            }

            double roundedTime = time.intValue();
            if (time - roundedTime < 0.001) {
                PC++;
                Logger.info("P" + PID + " instruction " + PC);
            }
        }

        if (status == ProcessStatus.RUNNING) {
            this.ready();
        }
    }

    public void blockedRunner() {
        try {
            waitResources();
            this.ready();
        } catch (InterruptedException e) {
            Logger.debug("O processo " + PID + " foi interrompido enquanto esperava por E/S.");
        }
    }

    public void ready() {
        this.setStatus(ProcessStatus.READY);
    }

    public void blocked() {
        this.setStatus(ProcessStatus.BLOCKED);
    }

    public void running() {
        this.setStatus(ProcessStatus.RUNNING);
    }

    public void finished() { this.setStatus(ProcessStatus.FINISHED); }

    private void setStatus(ProcessStatus status) {
        statusSemaphore.acquireUninterruptibly();
        final ProcessStatus oldStatus = this.status;
        this.status = status;
        statusSemaphore.release();

        ProcessManager.getInstance().statusListener(PID, oldStatus, status);
    }

    private void requestResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if (this.scanners && !resourcesManager.getScanner().getPIDs().contains(PID)) {
            Logger.debug("Alocando scanner para o processo: " + PID);
            if (resourcesManager.getScanner().getSemaphore().tryAcquire()) {
                resourcesManager.requestScanner(PID);
            } else {
                this.blocked();
            }
        }
        if (this.printers && !resourcesManager.getPrinter().getPIDs().contains(PID)) {
            Logger.debug("Alocando impressora para o processo: " + PID);
            if (resourcesManager.getPrinter().getSemaphore().tryAcquire()) {
                resourcesManager.requestPrinter(PID);
            } else {
                this.blocked();
            }
        }
        if (this.modems && !resourcesManager.getModem().getPIDs().contains(PID)) {
            Logger.debug("Alocando modem para o processo: " + PID);
            if (resourcesManager.getModem().getSemaphore().tryAcquire()) {
                resourcesManager.requestModem(PID);
            } else {
                Logger.debug("Nao foi possivel alocar modem para o processo " + PID + ", bloqueando...");
                this.blocked();
            }
        }
        if (this.sata && !resourcesManager.getSata().getPIDs().contains(PID)) {
            Logger.debug("Alocando sata para o processo: " + PID);
            if (resourcesManager.getSata().getSemaphore().tryAcquire()) {
                resourcesManager.requestSata(PID);
            } else {
                this.blocked();
            }
        }
    }

    public void waitResources() throws InterruptedException {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if (this.scanners && !resourcesManager.getScanner().getPIDs().contains(PID)) {
            resourcesManager.getScanner().getSemaphore().acquire();
            resourcesManager.requestScanner(PID);
        }
        if (this.printers && !resourcesManager.getPrinter().getPIDs().contains(PID)) {
            resourcesManager.getPrinter().getSemaphore().acquire();
            resourcesManager.requestPrinter(PID);
        }
        if (this.modems && !resourcesManager.getModem().getPIDs().contains(PID)) {
            resourcesManager.getModem().getSemaphore().acquire();
            resourcesManager.requestModem(PID);
        }
        if (this.sata && !resourcesManager.getSata().getPIDs().contains(PID)) {
            resourcesManager.getSata().getSemaphore().acquire();
            resourcesManager.requestSata(PID);
        }
    }

    public void refoundResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if (this.scanners) {
            Logger.debug("Liberando scanner do processo: " + PID);
            resourcesManager.refoundScanner(PID);
            resourcesManager.getScanner().getSemaphore().release();
        }
        if (this.printers) {
            Logger.debug("Liberando impressora do processo: " + PID);
            resourcesManager.refoundPrinter(PID);
            resourcesManager.getPrinter().getSemaphore().release();
        }
        if (this.modems) {
            Logger.debug("Liberando modem do processo: " + PID);
            if (resourcesManager.refoundModem(PID)) {
                resourcesManager.getModem().getSemaphore().release();
            }
        }
        if (this.sata) {
            Logger.debug("Liberando sata do processo: " + PID);
            resourcesManager.refoundSata(PID);
            resourcesManager.getSata().getSemaphore().release();
        }
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

    public int getOffset() {
        return offset;
    }

    public int getBlocks() {
        return blocks;
    }

    public Semaphore getStatusSemaphore() {
        return statusSemaphore;
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
