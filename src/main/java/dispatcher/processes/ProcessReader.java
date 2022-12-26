package processes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class ProcessReader {

    public static List<ProcessCreationRequest> read(final String file) {
        final List<ProcessCreationRequest> readProcesses = new ArrayList<>();

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

        return new ProcessCreationRequest(
                Integer.parseInt(splitLine.get(0)),
                Integer.parseInt(splitLine.get(1)),
                Integer.parseInt(splitLine.get(2)),
                Integer.parseInt(splitLine.get(3)),
                Integer.parseInt(splitLine.get(4)) == 1,
                Integer.parseInt(splitLine.get(5)) == 1,
                Integer.parseInt(splitLine.get(6)) == 1,
                Integer.parseInt(splitLine.get(7)) == 1
        );
    }

}
