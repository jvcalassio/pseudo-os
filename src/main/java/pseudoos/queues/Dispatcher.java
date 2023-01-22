package queues;

import memory.MemoryManager;
import processes.Process;
import processes.ProcessManager;
import processes.ProcessStatus;
import util.Logger;

import java.util.concurrent.Semaphore;

public class Dispatcher {

    private final Semaphore isDispatcherReady;

    public Dispatcher(Semaphore isDispatcherReady) {
        this.isDispatcherReady = isDispatcherReady;
    }

    /**
     * Dispatches process to CPU
     * @param process the process
     * @return true if the process should be reinserted into it's queue
     */
    public boolean dispatch(final Process process) {
        boolean reinsert = false;
        try {
            if (process.getStatus() != ProcessStatus.READY) {
                throw new RuntimeException("Processo #" + process.getPID() + " nao estava pronto para execucao");
            }

            Logger.info("P" + process.getPID() + " started");

            // processo de tempo real
            if (process.getProcessPriority() == 0) {
                // nao pode ser preemptado, espera ate o fim
                process.start();
                process.join();
                ProcessManager.getInstance().finishProcess(process);
            } else {
                // quantum de 1ms
                process.start();
                process.join(1);
                if (process.isAlive()) {
                    process.interrupt();
                    Logger.debug("Processo #" + process.getPID() + " interrompido pelo OS.");
                    reinsert = true;
                } else {
                    ProcessManager.getInstance().finishProcess(process);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Dispatcher foi interrompido inesperadamente");
        }

        isDispatcherReady.release();

        return reinsert;
    }
}
