package queues;

import processes.Process;
import processes.ProcessManager;
import processes.ProcessStatus;
import util.Logger;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

public class Scheduler {

    private static Scheduler instance;
    private boolean running;
    private final Queue<Process> realTimeProcesses;
    private final PriorityBlockingQueue<Process> userProcessesHigh;
    private final PriorityBlockingQueue<Process> userProcessesMedium;
    private final PriorityBlockingQueue<Process> userProcessesLow;

    private final Thread schedulerThread;
    private final Semaphore isDispatcherReady;
    private final Dispatcher dispatcher;

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }


    private Scheduler() {
        this.realTimeProcesses = new ConcurrentLinkedQueue<>();
        this.userProcessesHigh = new PriorityBlockingQueue<>(1000, Comparator.comparing(Process::getProcessPriority));
        this.userProcessesMedium = new PriorityBlockingQueue<>(1000, Comparator.comparing(Process::getProcessPriority));
        this.userProcessesLow = new PriorityBlockingQueue<>(1000, Comparator.comparing(Process::getProcessPriority));
        this.running = false;
        this.schedulerThread = createSchedulerThread();

        this.isDispatcherReady = new Semaphore(1);
        this.dispatcher = new Dispatcher(this.isDispatcherReady);
    }

    private Thread createSchedulerThread() {
        return new Thread(() -> {
            while (running) {
                try {
                    isDispatcherReady.acquire();
                    if (!realTimeProcesses.isEmpty()) {
                        dispatcher.dispatch(realTimeProcesses.remove());
                    } else if (!userProcessesHigh.isEmpty()) {
                        dispatchUserProcess(userProcessesHigh);
                    } else if (!userProcessesMedium.isEmpty()) {
                        dispatchUserProcess(userProcessesMedium);
                    } else if (!userProcessesLow.isEmpty()) {
                        dispatchUserProcess(userProcessesLow);
                    } else {
                        isDispatcherReady.release();
                        Thread.sleep(1);
                    }
                    /*
                     * Verificar RT, se tiver algum ele vai ser escolhido pra ser o prox
                     * Se nao tiver, vai ser o q tiver em userProcessesHigh,
                     * Se nao tiver, vai ser o q tiver em userProcessesMedium
                     * Se nao tiver, vai ser o q tiver em userProcessesLow
                     * Se nao tiver, tenta dnv
                     *
                     * enviar o processo removido pro dispatcher
                     */
                } catch (InterruptedException e) {
                    running = false;
                    Logger.debug("Scheduler finalizado.");
                }
            }
        }, "Scheduler");
    }

    private void dispatchUserProcess(final PriorityBlockingQueue<Process> queue) {
        final Process currentProcess = queue.remove();
        if (currentProcess.getStatus() != ProcessStatus.BLOCKED) {
            final boolean reinsert = dispatcher.dispatch(currentProcess);
            if (reinsert) {
                addUserProcess(currentProcess);
                Logger.debug("Reinserindo processo #" + currentProcess.getPID());
            }
        } else {
            ProcessManager.getInstance().addBlockedProcess(currentProcess);
        }
    }

    public void start() {
        if (!running) {
            running = true;
            schedulerThread.start();
            Logger.debug("Iniciando Scheduler");
        }
    }

    public void stop() {
        if (running) {
            Logger.debug("Finalizado Scheduler");
            schedulerThread.interrupt();
        }
    }

    public void addRealTimeProcess(final Process process) {
        realTimeProcesses.add(process);
    }

    public void addUserProcess(final Process process) {
        if (process.getProcessPriority() == 1) {
            userProcessesHigh.add(process);
        } else if (process.getProcessPriority() == 2) {
            userProcessesMedium.add(process);
        } else {
            userProcessesLow.add(process);
        }
    }

}
