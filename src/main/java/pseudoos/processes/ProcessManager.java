package processes;

import exception.NotEnoughMemoryException;
import queues.Scheduler;
import util.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
    private final Map<Integer, Integer> listOfUsage;

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
        this.listOfUsage = new HashMap<>();
    }

    public void createProcess(final ProcessCreationRequest creationRequest) {
        try {
            final Process createdProcess = new Process(creationRequest);

            if (!processList.containsKey(createdProcess.getPID())) {
                processList.put(createdProcess.getPID(), createdProcess);
            }
            createdProcess.ready();
        } catch (NotEnoughMemoryException e) {
            Logger.info("O processo " + e.getPID() + " nao foi criado por falta de memoria.");
            this.finishedProcesses.release();
        }
    }

    public void statusListener(Integer PID, ProcessStatus oldStatus, ProcessStatus newStatus) {
        final Process process = processList.get(PID);

        if (newStatus == ProcessStatus.READY) {
            this.readyProcesses.add(process);
        } else if (newStatus == ProcessStatus.BLOCKED) {
            this.blockedProcesses.add(process);
        } else if (newStatus == ProcessStatus.FINISHED) {
            this.finishedProcesses.release();
        }
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
                fileWriter = new BufferedWriter(new FileWriter("out/monitor.txt"));
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
        final String profile = System.getProperty("profile");

        if (!running) {
            running = true;
            readyRunner.start();
            blockedRunner.start();

            if ("debug".equals(profile)) {
                monitorRunner.start();
            }
            Logger.debug("Iniciando ProcessManager");
        }
    }

    public void stop() {
        final String profile = System.getProperty("profile");

        if (running) {
            Logger.debug("Finalizando ProcessManager");
            if ("debug".equals(profile)) {
                monitorRunner.interrupt();
            }
            blockedRunner.interrupt();
            readyRunner.interrupt();
        }
    }

    public Semaphore getFinishedProcesses() {
        return finishedProcesses;
    }

    public ConcurrentMap<Integer, Process> getProcessList() {
        return processList;
    }

    public void addUsage(Integer PID, Integer time) {
        if (listOfUsage.containsKey(PID)) {

            listOfUsage.put(PID, listOfUsage.get(PID) + time);
        } else {
            listOfUsage.put(PID, time);
        }
    }

    public Integer getUsage(Integer PID) {
        return listOfUsage.get(PID) == null ? 0 : listOfUsage.get(PID);
    }

    public Integer getAverageUsage() {
        Integer totalUsage = 0;

        if(listOfUsage.size() == 0) return 0;

        for (Integer PID : listOfUsage.keySet()) {
            totalUsage += listOfUsage.get(PID);
        }
        return totalUsage / listOfUsage.size();
    }

}
