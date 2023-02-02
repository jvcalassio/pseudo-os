package queues;

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
     * @return the action to be done after running
     */
    public void dispatch(final Process process) {
        if (process.getStatus() != ProcessStatus.READY) {
            throw new RuntimeException("Processo " + process.getPID() + " nao esta pronto, esta " + process.getStatus());
        }

        try {
            final Thread CPU = new Thread(process::run);
            Logger.debug("Iniciando Processo " + process.getPID());
            if (process.getProcessPriority() == 0) {
                CPU.start();
                CPU.join();
            } else {
                CPU.start();
                Thread.sleep(1);
                CPU.interrupt();
                CPU.join();
            }
            changeProcessPriority(process);
        } catch (InterruptedException e) {
            Logger.debug("Dispatcher interrompido");
        }

        isDispatcherReady.release();
    }

    private void changeProcessPriority(Process process) {
        ProcessManager processManager = ProcessManager.getInstance();
        int average = processManager.getAverageUsage();
        int atualPriority = process.getProcessPriority();

        // não alterar prioridade de processos de tempo real
        if(atualPriority == 0)
            return;

        // aumentar a prioridade - não pode fazer fazer um processo de usuario virar um processo de tempo real
        if (processManager.getUsage(process.getPID()) < average && atualPriority >= 2) {
            Logger.info("Processo " + process.getPID() + " aumentou a prioridade de " + atualPriority + " para " + (atualPriority - 1));
            process.setProcessPriority(atualPriority - 1);
        }

        else if (processManager.getUsage(process.getPID()) > average && atualPriority >= 1 && atualPriority <= 3) {
            Logger.info("Processo " + process.getPID() + " diminuiu a prioridade de " + atualPriority + " para " + (atualPriority + 1));
            process.setProcessPriority(atualPriority + 1);
        }
    }
}
