import files.FileManager;
import inputreader.file.FileReader;
import inputreader.file.FileSystemInitializationRequest;
import inputreader.process.ProcessReader;
import memory.MemoryManager;
import processes.Process;
import processes.ProcessCreationRequest;
import processes.ProcessManager;
import queues.Scheduler;
import util.Logger;

import java.util.List;
import java.util.Set;

public class PseudoOS {

    public PseudoOS() {}

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            System.out.println("Run with ./dispatcher processes.txt files.txt");
            System.exit(1);
        }
        final String processes = args[0];
        final String files = args[1];

        final PseudoOS pseudoOS = new PseudoOS();

        final List<ProcessCreationRequest> processInfo = ProcessReader.read(processes);
        final FileSystemInitializationRequest fileInfo = FileReader.read(files);

        FileManager.getInstance().initialize(
                fileInfo.getTotalBlocks(), fileInfo.getInitialFileSystem(), fileInfo.getInstructions()
        );
        pseudoOS.initialize(processInfo);
    }
    private void initialize(final List<ProcessCreationRequest> processCreationRequestList)
            throws InterruptedException {
        int time = 0;
        int processCount = processCreationRequestList.size();
        boolean running = true;

        ProcessManager.getInstance().start();
        Scheduler.getInstance().start();

        while (running) {
            while(!processCreationRequestList.isEmpty() && processCreationRequestList.get(0).getStartTime() == time) {
                final ProcessCreationRequest nextProcess = processCreationRequestList.get(0);
                Logger.debug(nextProcess.toString());

                int offset;
                if (nextProcess.getPriority() == 0) {
                    offset = MemoryManager.getInstance().allocateRealTimeBlocks(nextProcess.getBlocks());
                } else {
                    offset = MemoryManager.getInstance().allocateUserBlocks(nextProcess.getBlocks());
                }
                ProcessManager.getInstance().readyProcess(new Process(nextProcess, offset));

                processCreationRequestList.remove(0);
            }

            if (processCreationRequestList.isEmpty()) {
                running = false;
            }

            Thread.sleep(1000);
            time++;
        }

        Logger.debug("Todos os processos foram criados.");

        ProcessManager.getInstance().getFinishedProcesses().acquire(processCount);

        Logger.debug("Cabo tudo");

        ProcessManager.getInstance().stop();
        Scheduler.getInstance().stop();
    }

}
