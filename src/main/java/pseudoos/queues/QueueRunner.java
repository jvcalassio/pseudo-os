package queues;

import processes.Process;
import util.Logger;

public class QueueRunner {

    private boolean running;
    private final Thread runnerThread;
    private final ProcessManager processManager;
    public QueueRunner(ProcessManager processManager) {
        this.processManager = processManager;
        this.running = false;
        this.runnerThread = generateRunnerThread();
    }

    private Thread generateRunnerThread() {
        return new Thread(() -> {
            while(this.running) {
                if (processManager.getRealTimeProcesses().isEmpty()) {
                    // run non priority processes
                    //Logger.debug("Real time processes queue is empty");
                } else {
                    final Process currentProcess = processManager.getRealTimeProcesses().remove();
                    Logger.debug("Running process #" + currentProcess.getPID());
                    currentProcess.run();
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean isRunning() {
        return running;
    }

    public void run() {
        if (!this.running) {
            this.running = true;
            runnerThread.start();
        }
    }

    public void stop() {
        this.running = false;
    }


}
