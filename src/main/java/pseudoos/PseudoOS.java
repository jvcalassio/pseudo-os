import inputreader.file.FileSystemInitializationRequest;
import files.FileManager;
import inputreader.file.FileReader;
import memory.MemoryManager;
import processes.Process;
import processes.ProcessCreationRequest;
import inputreader.process.ProcessReader;
import processes.ProcessManager;
import queues.Dispatcher;
import queues.Scheduler;
import resources.ResourcesManager;
import util.Logger;

import java.util.List;
import java.util.concurrent.Semaphore;

public class PseudoOS {
    private final MemoryManager memoryManager;
    private final FileManager fileManager;
    private final ResourcesManager resourcesManager;
    private final ProcessManager processManager;
    private final Dispatcher dispatcher;
    private final Scheduler scheduler;

    public PseudoOS(final MemoryManager memoryManager,
                    final FileManager fileManager,
                    final ResourcesManager resourcesManager,
                    final ProcessManager processManager,
                    final Dispatcher dispatcher,
                    final Scheduler scheduler) {
        this.memoryManager = memoryManager;
        this.fileManager = fileManager;
        this.resourcesManager = resourcesManager;
        this.processManager = processManager;
        this.dispatcher = dispatcher;
        this.scheduler = scheduler;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Run with ./dispatcher processes.txt files.txt");
            System.exit(1);
        }
        final String processes = args[0];
        final String files = args[1];

        final MemoryManager memoryManager = MemoryManager.getInstance();
        final FileManager fileManager = new FileManager();
        final ResourcesManager resourcesManager = new ResourcesManager();

        final Semaphore isDispatcherReady = new Semaphore(1);
        final Dispatcher dispatcher = new Dispatcher(isDispatcherReady);
        final Scheduler scheduler = new Scheduler(isDispatcherReady, dispatcher);
        final ProcessManager processManager = new ProcessManager(scheduler);

        final PseudoOS pseudoOS = new PseudoOS(
                memoryManager,
                fileManager,
                resourcesManager,
                processManager,
                dispatcher,
                scheduler
        );

        final List<ProcessCreationRequest> processInfo = ProcessReader.read(processes);
        final FileSystemInitializationRequest fileInfo = FileReader.read(files);

        fileManager.initialize(fileInfo.getTotalBlocks(), fileInfo.getInitialFileSystem(), fileInfo.getInstructions());
        pseudoOS.initialize(processInfo);
    }
    private void initialize(final List<ProcessCreationRequest> processCreationRequestList)
            throws InterruptedException {
        int time = 0;
        boolean running = true;

        processManager.start();
        scheduler.start();

        while (running) {
            while(!processCreationRequestList.isEmpty() && processCreationRequestList.get(0).getStartTime() == time) {
                final ProcessCreationRequest nextProcess = processCreationRequestList.get(0);
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
//                this.queueRunner.run();
                final int offset = memoryManager.allocateRealTimeBlocks(nextProcess.getBlocks());
                processManager.readyProcess(new Process(nextProcess, offset));

                processCreationRequestList.remove(0);
            }

            if (processCreationRequestList.isEmpty()) {
                running = false;
            }

            Thread.sleep(1000);
            time++;
        }

        Logger.debug("Todos os processos foram criados.");

        // esperar ate todos os processos finalizarem
        // talvez com um semaforo?

//        queueRunner.stop();
    }

}
