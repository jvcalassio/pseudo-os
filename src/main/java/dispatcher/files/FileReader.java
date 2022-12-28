package files;

import processes.ProcessInstruction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class FileReader {

    public static FileSystemInitializationRequest read(final String file) {
        int totalBlocks = 0;
        int usedBlocks = 0;

        final List<ProcessInstruction> instructionList = new ArrayList<>();
        final List<FileCreationRequest> initialFileSystem = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(file))) {
            final Iterator<String> iterator = lines.iterator();

            totalBlocks = Integer.parseInt(iterator.next());
            usedBlocks = Integer.parseInt(iterator.next());

            for (int i=0;i<usedBlocks;i++) {
                initialFileSystem.add(parseBlockLine(iterator.next()));
            }

            iterator.forEachRemaining(line -> instructionList.add(parseInstructionLine(line)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FileSystemInitializationRequest(
                totalBlocks,
                initialFileSystem,
                instructionList
        );
    }

    private static FileCreationRequest parseBlockLine(final String line) {
        final List<String> splitLine = List.of(line.replaceAll("\\s+", "").split(","));

        final String fileName = splitLine.get(0);
        final Integer fileStart = Integer.parseInt(splitLine.get(1));
        final int fileSize = Integer.parseInt(splitLine.get(2));

        return new FileCreationRequest(
                fileName,
                new FileData(
                    fileStart,
                    fileSize,
                    FileOwnedBy.SYSTEM,
                        null
                )
        );
    }

    private static ProcessInstruction parseInstructionLine(final String line) {
        final List<String> splitLine = List.of(line.replaceAll("\\s+", "").split(","));

        final int pid = Integer.parseInt(splitLine.get(0));
        final FileOperation operation = FileOperation.fromString(splitLine.get(1));
        final String fileName = splitLine.get(2);

        if (operation == FileOperation.CREATE) {
            final int fileSize = Integer.parseInt(splitLine.get(3));
            return new ProcessInstruction(
                    pid,
                    operation,
                    fileName,
                    fileSize
            );
        }

        return new ProcessInstruction(
                pid,
                operation,
                fileName
        );
    }

}
