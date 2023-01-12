package inputreader.process;

import processes.ProcessCreationRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ProcessReader {

    public static List<ProcessCreationRequest> read(final String file) {
        final List<ProcessCreationRequest> readProcesses = new LinkedList<>();

        try (Stream<String> lines = Files.lines(Paths.get(file))) {
            lines.forEach(line -> readProcesses.add(parseLine(line)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        readProcesses.sort(Comparator.comparing(ProcessCreationRequest::getStartTime));

        return readProcesses;
    }

    private static ProcessCreationRequest parseLine(final String line) {
        final List<String> splitLine = List.of(line.replaceAll("\\s+", "").split(","));

        final int startTime = Integer.parseInt(splitLine.get(0));
        final int priority = Integer.parseInt(splitLine.get(1));
        final int cpuTime = Integer.parseInt(splitLine.get(2));
        final int blocks = Integer.parseInt(splitLine.get(3));
        final boolean printers = Integer.parseInt(splitLine.get(4)) == 1;
        final boolean scanners = Integer.parseInt(splitLine.get(5)) == 1;
        final boolean modems = Integer.parseInt(splitLine.get(6)) == 1;
        final boolean satas = Integer.parseInt(splitLine.get(7)) == 1;

        return new ProcessCreationRequest(
                startTime,
                priority,
                cpuTime,
                blocks,
                printers,
                scanners,
                modems,
                satas
        );
    }

}
