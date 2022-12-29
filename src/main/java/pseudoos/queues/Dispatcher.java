package queues;

import processes.Process;
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
            final Thread processRunner = new Thread(process::run);

            if (process.getPriority() == 0) {
                // nao pode ser preemptado, espera ate o fim
                processRunner.start();
                processRunner.join();
                Logger.debug("Processo #" + process.getPID() + " finalizado.");
            } else {
                // quantum de 1ms
                processRunner.start();
                processRunner.join(1);
                if (processRunner.isAlive()) {
                    processRunner.interrupt();
                    Logger.debug("Processo #" + process.getPID() + " interrompido pelo OS.");
                    reinsert = true;
                } else {
                    Logger.debug("Processo #" + process.getPID() + " finalizado.");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Dispatcher foi interrompido inesperadamente");
        }

        isDispatcherReady.release();

        return reinsert;
    }


}
