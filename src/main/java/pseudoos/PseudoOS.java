import files.FileSystemInitializationRequest;
import files.FileManager;
import files.FileReader;
import memory.MemoryManager;
import processes.ProcessCreationRequest;
import processes.ProcessReader;
import queues.ProcessManager;
import queues.QueueRunner;
import util.Logger;

import java.util.List;

public class PseudoOS {
    private final MemoryManager memoryManager;
    private final ProcessManager processManager;
    private final QueueRunner queueRunner;
    private final FileManager fileManager;

    public PseudoOS(final MemoryManager memoryManager,
                    final ProcessManager processManager,
                    final QueueRunner queueRunner,
                    final FileManager fileManager) {
        this.memoryManager = memoryManager;
        this.processManager = processManager;
        this.queueRunner = queueRunner;
        this.fileManager = fileManager;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Run with ./dispatcher processes.txt files.txt");
            System.exit(1);
        }
        final String processes = args[0];
        final String files = args[1];

        final MemoryManager memoryManager = new MemoryManager();
        final FileManager fileManager = new FileManager();
        final ProcessManager processManager = new ProcessManager();
        final QueueRunner queueRunner = new QueueRunner(processManager);

        final PseudoOS pseudoOS = new PseudoOS(
                memoryManager,
                processManager,
                queueRunner,
                fileManager
        );

        final List<ProcessCreationRequest> processCreationRequestList = ProcessReader.read(processes);
        final FileSystemInitializationRequest fileSystemInitializationRequest = FileReader.read(files);

        fileManager.initialize(fileSystemInitializationRequest);
        pseudoOS.initialize(processCreationRequestList);
    }
    private void initialize(final List<ProcessCreationRequest> processCreationRequestList)
            throws InterruptedException {
        int time = 0;

        while (!processCreationRequestList.isEmpty()) {
            final ProcessCreationRequest nextProcess = processCreationRequestList.get(0);
            if (nextProcess.getStartTime() == time) {
                Logger.debug(nextProcess.toString());
                // reavaliar como funciona essas filas aqui
                // enviar pra fila unica, q vai ser processada e reencaminhar pra outras filas internas
                // (ver desenho na spec)
//                if (nextProcess.getPriority() == 0) {
//                    final int offset = memoryManager.allocateRealTimeBlocks(nextProcess.getBlocks());
//                    processManager.enqueueRealTimeProcess(nextProcess, offset);
//                } else {
//                    final int offset = memoryManager.allocateUserBlocks(nextProcess.getBlocks());
//
//                }
                this.queueRunner.run();
                processCreationRequestList.remove(0);
            }

            Thread.sleep(1000);
            time++;
        }

        // esperar ate todos os processos finalizarem
        // talvez com um semaforo?

        queueRunner.stop();
    }

}
