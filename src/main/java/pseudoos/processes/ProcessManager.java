package processes;

import memory.MemoryManager;
import queues.Scheduler;
import util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ProcessManager {

    private static ProcessManager instance;
    private boolean running;
    private final BlockingQueue<Process> readyProcesses;
    private final BlockingQueue<Process> blockedProcesses;
    private final Thread readyRunner;
    private final Semaphore finishedProcesses;

    public static ProcessManager getInstance() {
        if (instance == null) {
            instance = new ProcessManager();
        }
        return instance;
    }

    private ProcessManager() {
        this.readyProcesses = new LinkedBlockingQueue<>(1000);
        this.blockedProcesses = new LinkedBlockingQueue<>(1000);
        this.running = false;
        this.readyRunner = createReadyRunnerThread();
        this.finishedProcesses = new Semaphore(0);
    }

    public void readyProcess(final Process process) {
        process.ready();
        readyProcesses.add(process);
        Logger.debug("Processo #" + process.getPID() + " pronto.");
    }

    public void blockProcess(final Process process) {
        process.block();
        blockedProcesses.add(process);
        Logger.debug("Processo #" + process.getPID() + " bloqueado.");
    }

    public void finishProcess(final Process process) {
        if (process.getProcessPriority() == 0) {
            MemoryManager.getInstance().freeRealTimeBlocks(process.getOffset(), process.getBlocks());
        } else {
            MemoryManager.getInstance().freeUserBlocks(process.getOffset(), process.getBlocks());
        }
        Logger.debug("Processo #" + process.getPID() + " finalizado.");
        this.finishedProcesses.release();
    }

    public Semaphore getFinishedProcesses() {
        return finishedProcesses;
    }

    public Thread createReadyRunnerThread() {
        return new Thread(() -> {
           while (running) {
               try {
                   final Process nextProcess = readyProcesses.take();
                   Logger.debug("Adicionando processo " + nextProcess.getPID() + " na fila por prioridade.");
                   if (nextProcess.getProcessPriority() == 0) {
                       Scheduler.getInstance().addRealTimeProcess(nextProcess);
                   } else {
                       Scheduler.getInstance().addUserProcess(nextProcess);
                   }
               } catch (InterruptedException e) {
                   running = false;
                   Logger.debug("ProcessManager finalizado.");
               }
           }
        }, "ProcessManager");
    }

    public void start() {
        if (!running) {
            running = true;
            readyRunner.start();
            Logger.debug("Iniciando ProcessManager");
        }
    }

    public void stop() {
        if (running) {
            Logger.debug("Finalizando ProcessManager");
            readyRunner.interrupt();
        }
    }

}
