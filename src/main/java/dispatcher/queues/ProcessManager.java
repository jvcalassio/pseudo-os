package queues;

import memory.MemoryManager;
import processes.Process;
import processes.ProcessCreationRequest;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ProcessManager {

    private final Queue<Process> realTimeProcesses;
    private final PriorityQueue<Process> userProcesses;

    public ProcessManager() {
        this.realTimeProcesses = new LinkedList<>();
        this.userProcesses = new PriorityQueue<>(1, Comparator.comparing(Process::getPriority));
    }

    public void enqueueRealTimeProcess(final ProcessCreationRequest processCreationRequest,
                                       final int offset) {
        final Process process = new Process(processCreationRequest, offset);
        realTimeProcesses.add(process);
    }

    public void enqueueUserProcess(final ProcessCreationRequest processCreationRequest,
                                   final int offset) {
        // decidir como armazenar as filas de prioridade
    }

    public Queue<Process> getRealTimeProcesses() {
        return realTimeProcesses;
    }
}
