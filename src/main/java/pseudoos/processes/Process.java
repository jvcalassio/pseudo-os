package processes;

import resources.ResourcesManager;
import util.Logger;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

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
    private ProcessStatus status;
    private Semaphore runningBlockedSemaphore;
    private Semaphore readySemaphore;
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
        this.sata = processCreationRequest.hasSatas();
        this.offset = currentMemoryOffset;
        this.PC = 0;

        this.runningBlockedSemaphore = new Semaphore(0);
        this.readySemaphore = new Semaphore(0);
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

    public int getOffset() {
        return offset;
    }

    public int getBlocks() {
        return blocks;
    }

    public void run() {
        requestResources();
        this.running();

        while (true) {
            if (Thread.interrupted()) {
                this.ready();
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
        this.readySemaphore.release();
    }

    public void blocked() {
        this.status = ProcessStatus.BLOCKED;
        this.runningBlockedSemaphore.release();
    }

    public void running() {
        this.status = ProcessStatus.RUNNING;
        this.runningBlockedSemaphore.release();
    }

    public void finished() {
        this.status = ProcessStatus.FINISHED;
    }

    public void waitUntilRunningOrBlocked() throws InterruptedException {
        runningBlockedSemaphore.acquire();
        runningBlockedSemaphore.drainPermits();
    }

    public void waitUntilReady() throws InterruptedException {
        readySemaphore.acquire();
        readySemaphore.drainPermits();
    }

    public void requestResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if (this.scanners) {
            if (resourcesManager.getScanner().getPIDs().contains(this.getPID())) {
                return;
            }

            Logger.info("Alocando scanner para o processo: " + PID);
            if (resourcesManager.getScanner().getSemaphore().tryAcquire()) {
                resourcesManager.requestScanner(PID);
            } else {
                blocked();
                try {
                    resourcesManager.getScanner().getSemaphore().acquire();
                    resourcesManager.requestScanner(PID);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (this.printers) {
            if (resourcesManager.getPrinter().getPIDs().contains(this.getPID())) {
                return;
            }

            Logger.info("Alocando impressora para o processo: " + PID);
            if (resourcesManager.getPrinter().getSemaphore().tryAcquire()) {
                resourcesManager.requestPrinter(PID);
            } else {
                blocked();
                try {
                    resourcesManager.getPrinter().getSemaphore().acquire();
                    resourcesManager.requestPrinter(PID);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (this.modems) {
            if (resourcesManager.getModem().getPIDs().contains(this.getPID())) {
                return;
            }

            Logger.info("Alocando modem para o processo: " + PID);
            if (resourcesManager.getModem().getSemaphore().tryAcquire()) {
                resourcesManager.requestModem(PID);
            } else {
                blocked();
                try {
                    resourcesManager.getModem().getSemaphore().acquire();
                    resourcesManager.requestModem(PID);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (this.sata) {
            if (resourcesManager.getSata().getPIDs().contains(this.getPID())) {
                return;
            }

            Logger.info("Alocando sata para o processo: " + PID);
            if (resourcesManager.getSata().getSemaphore().tryAcquire()) {
                resourcesManager.requestSata(PID);
            } else {
                blocked();
                try {
                    resourcesManager.getSata().getSemaphore().acquire();
                    resourcesManager.requestSata(PID);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void refoundResources() {
        ResourcesManager resourcesManager = ResourcesManager.getInstance();
        if (this.scanners) {
            Logger.info("Liberando scanner do processo: " + PID);
            resourcesManager.refoundScanner(PID);
            resourcesManager.getScanner().getSemaphore().release();
        }
        if (this.printers) {
            Logger.info("Liberando impressora do processo: " + PID);
            resourcesManager.refoundPrinter(PID);
            resourcesManager.getPrinter().getSemaphore().release();
        }
        if (this.modems) {
            Logger.info("Liberando modem do processo: " + PID);
            resourcesManager.refoundModem(PID);
            resourcesManager.getModem().getSemaphore().release();
        }
        if (this.sata) {
            Logger.info("Liberando sata do processo: " + PID);
            resourcesManager.refoundSata(PID);
            resourcesManager.getSata().getSemaphore().release();
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
