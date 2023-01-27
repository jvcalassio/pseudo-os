package processes;

import exception.NotEnoughMemoryException;
import memory.MemoryManager;
import queues.Scheduler;
import util.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

public class ProcessManager {

    private static ProcessManager instance;
    private boolean running;
    private final ConcurrentMap<Integer, Process> processList;
    private final BlockingQueue<Process> readyProcesses;
    private final BlockingQueue<Process> blockedProcesses;
    private final Thread readyRunner;
    private final Thread blockedRunner;
    private final Thread monitorRunner;
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
        this.blockedRunner = createBlockedRunnerThread();
        this.monitorRunner = createProcessMonitor();
        this.processList = new ConcurrentHashMap<>();
        this.finishedProcesses = new Semaphore(0);
    }


    public void createProcess(final ProcessCreationRequest creationRequest) {
        try {
            int offset;
            if (creationRequest.getPriority() == 0) {
                offset = MemoryManager.getInstance().allocateRealTimeBlocks(creationRequest.getBlocks());
            } else {
                offset = MemoryManager.getInstance().allocateUserBlocks(creationRequest.getBlocks());
            }
            final Process createdProcess = new Process(creationRequest, offset);

            if (!processList.containsKey(createdProcess.getPID())) {
                processList.put(createdProcess.getPID(), createdProcess);
            }
            createdProcess.ready();
        } catch (NotEnoughMemoryException e) {
            final Process createdProcess = new Process(creationRequest, 0);
            createdProcess.finished();
            if (!processList.containsKey(createdProcess.getPID())) {
                processList.put(createdProcess.getPID(), createdProcess);
            }
            throw new NotEnoughMemoryException(createdProcess.getPID(), e);
        }
    }

    public void statusListener(Integer PID, ProcessStatus oldStatus, ProcessStatus newStatus) {
        final Process process = processList.get(PID);

        if (newStatus == ProcessStatus.READY) {
            this.readyProcesses.add(process);
        } else if (newStatus == ProcessStatus.BLOCKED) {
            this.blockedProcesses.add(process);
        } else if (newStatus == ProcessStatus.FINISHED) {
            if (process.getProcessPriority() == 0) {
                MemoryManager.getInstance().freeRealTimeBlocks(process.getOffset(), process.getBlocks());
            } else {
                MemoryManager.getInstance().freeUserBlocks(process.getOffset(), process.getBlocks());
            }
            process.refoundResources();
            Logger.info("P" + PID + " return SIGINT");
            this.finishedProcesses.release();
        }
    }


    public Semaphore getFinishedProcesses() {
        return finishedProcesses;
    }

    public ConcurrentMap<Integer, Process> getProcessList() {
        return processList;
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

    public Thread createBlockedRunnerThread() {
        return new Thread(() -> {
            while(running) {
                try {
                    final Process blockedProcess = blockedProcesses.take();
                    final Thread blockedThread = new Thread(blockedProcess::blockedRunner);
                    blockedThread.start();
                } catch (InterruptedException e) {
                    running = false;
                    Logger.debug("BlockedRunner finalizado.");
                }
            }
        }, "BlockedRunner");
    }

    public Thread createProcessMonitor() {
        return new Thread(() -> {
            BufferedWriter fileWriter;
            try {
                fileWriter = new BufferedWriter(new FileWriter("out.txt"));
            } catch (IOException e) {
                throw new RuntimeException("Erro ao criar output do ProcessMonitor");
            }

            Long counter = 0L;
            while (running) {
                try {
                    if (processList.isEmpty()) {
                        continue;
                    }
                    String output = "";
                    for (Map.Entry<Integer, Process> item : processList.entrySet()) {
                        output += item.getKey() + "-" + item.getValue().getStatus() + ", ";
                    }
                    fileWriter.append(counter.toString()).append(": ").append(output).append("\n");
                    Thread.sleep(1);
                    counter++;
                } catch (InterruptedException | IOException e) {
                    running = false;
                    Logger.debug("ProcessMonitor finalizado.");
                }
            }

            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, "ProcessMonitor");
    }

    public void start() {
        if (!running) {
            running = true;
            readyRunner.start();
            blockedRunner.start();
            monitorRunner.start();
            Logger.debug("Iniciando ProcessManager");
        }
    }

    public void stop() {
        if (running) {
            Logger.debug("Finalizando ProcessManager");
            monitorRunner.interrupt();
            blockedRunner.interrupt();
            readyRunner.interrupt();
        }
    }

}
