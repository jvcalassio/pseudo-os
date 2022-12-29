package processes;

import queues.Scheduler;
import util.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessManager {

    private boolean running;
    private final BlockingQueue<Process> readyProcesses;
    private final BlockingQueue<Process> blockedProcesses;
    private final Thread readyRunner;
    private final Scheduler scheduler;

    public ProcessManager(final Scheduler scheduler) {
        this.readyProcesses = new LinkedBlockingQueue<>(1000);
        this.blockedProcesses = new LinkedBlockingQueue<>(1000);
        this.running = false;
        this.scheduler = scheduler;
        this.readyRunner = createReadyRunnerThread();
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

    public Thread createReadyRunnerThread() {
        return new Thread(() -> {
           while (running) {
               try {
                   final Process nextProcess = readyProcesses.take();
                   Logger.debug("Adicionando processo " + nextProcess.getPID() + " na fila por prioridade.");
                   if (nextProcess.getPriority() == 0) {
                       scheduler.addRealTimeProcess(nextProcess);
                   } else {
                       scheduler.addUserProcess(nextProcess);
                   }
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        });
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
            running = false;
            Logger.debug("Finalizando ProcessManager");
        }
    }

}
