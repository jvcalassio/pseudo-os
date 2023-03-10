package queues;

import processes.Process;
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
        this.userProcessesHigh = new PriorityBlockingQueue<>(1000,
                Comparator.comparing(Process::getProcessPriority));
        this.userProcessesMedium = new PriorityBlockingQueue<>(1000,
                Comparator.comparing(Process::getProcessPriority));
        this.userProcessesLow = new PriorityBlockingQueue<>(1000,
                Comparator.comparing(Process::getProcessPriority));
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
                        dispatchRealTimeProcess();
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
                } catch (InterruptedException e) {
                    running = false;
                    Logger.debug("Scheduler finalizado.");
                }
            }
        }, "Scheduler");
    }

    private void dispatchRealTimeProcess() {
        dispatcher.dispatch(realTimeProcesses.remove());
    }

    private void dispatchUserProcess(final PriorityBlockingQueue<Process> queue) {
        dispatcher.dispatch(queue.remove());
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

}
