import memory.MemoryManager;
import processes.Process;
import processes.ProcessReader;
import processes.ProcessCreationRequest;
import queues.ProcessManager;
import util.Log;

import java.util.List;

public class Dispatcher {
    private final MemoryManager memoryManager;
    private final ProcessManager processManager;

    public Dispatcher(MemoryManager memoryManager, ProcessManager processManager) {
        this.memoryManager = memoryManager;
        this.processManager = processManager;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            System.out.println("Run with ./dispatcher processes.txt files.txt");
            System.exit(1);
        }
        final String processes = args[0];
        final String files = args[1];

        final Dispatcher dispatcher = new Dispatcher(
                new MemoryManager(),
                new ProcessManager()
        );

        final List<ProcessCreationRequest> processCreationRequestList = ProcessReader.read(processes);
        // ler o files.txt e deixar pronto as instrucoes no gerenciador de arquivos

        dispatcher.dispatch(processCreationRequestList);
    }
    private void dispatch(final List<ProcessCreationRequest> processCreationRequestList)
            throws InterruptedException {
        int time = 0;

        while (!processCreationRequestList.isEmpty()) {
            final ProcessCreationRequest nextProcess = processCreationRequestList.get(0);
            if (nextProcess.getStartTime() == time) {
                // tentar alocar o espaco na memoria
                // chamar gerenciador de filas pra criar o novo Process
                Log.debug(nextProcess.toString());
                processCreationRequestList.remove(0);
            }

            Thread.sleep(1000);
            time++;
        }
    }

}
