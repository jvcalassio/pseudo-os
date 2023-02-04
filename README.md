# Pseudo Operating System

Code for the "Operating Systems" course, UnB 2022.2

The goal of this project is to implement a pseudo operating system in Java, in order to familiarize with the concepts used by modern days operating systems.

This project is separated by five major modules: process management, queue management, memory management, resources management and file management. Each module is responsible for a separate area of the OS, and may communicate with other modules in order to execute processes.

## Input

The inputs are two text files. The first one specifies which processes will run at the given times, and the second one specifies the contents of the file system initially and the instructions that will be run by the processes.

Process input file require the following pattern:

```
<start time (seconds)>, <priority>, <required time>, <required memory>, <printer?>, <scanner?>, <modem?>, <sata?>
```

File input file require the following pattern:

```
<amount of blocks in the disk>
<amount of used blocks in the disk (N)>
N entries of <file name>, <starting block number>, <number of used blocks>
For every other line after that: <PID>, <operation>, <file name>, <number of blocks to allocate, if op == 0>

```

Where operation might be 0 (create file) or 1 (delete file).

Sample file inputs are present at `src/main/resources/processes.txt` and `src/main/resources/files.txt`.

## How to run it

The project is build with Java 11, and any JDK 11 should be compatible with it. You'll first need to compile the project with `javac`:

```
javac -d out/production -cp .:./out/production src/main/java/pseudoos/**/*.java
```

After the compiled code is generated, you might execute it by typing the following command:

```
java -classpath .:./out/production PseudoOS {processes} {files}
```

Where `{processes}` is the text file input with the the processes information, and `{files}` is the file input with the initial file system and instructions.

### Debug mode

If you want to see the full logs and the process monitoring (it outputs the state of every process, every 1ms), you'll need to pass the flag `-Dprofile=debug` to the program. So, for example, if you run the following command, you'll have full logs of the application:

```
java -classpath .:./out/production -Dprofile=debug PseudoOS src/main/resources/processes.txt src/main/resources/files.txt
```

Sample logs:

```
2023-02-04T16:03:16.495 [DEBUG] Iniciando ProcessManager
2023-02-04T16:03:16.554 [DEBUG] Iniciando Scheduler
2023-02-04T16:03:18.575 [DEBUG] ProcessRequest{startTime=2, priority=0, cpuTime=3, blocks=64, printers=false, scanners=false, modems=false, satas=false}
2023-02-04T16:03:18.605 [DEBUG] Alocando memória real-time nos blocos [0:63]
2023-02-04T16:03:18.606 [DEBUG] ProcessRequest{startTime=2, priority=0, cpuTime=2, blocks=64, printers=false, scanners=false, modems=false, satas=false}
2023-02-04T16:03:18.611 [DEBUG] Adicionando processo 0 na fila por prioridade.
2023-02-04T16:03:18.612 [INFO] O processo 1 nao foi criado por falta de memoria.
2023-02-04T16:03:18.618 [DEBUG] Iniciando Processo 0
2023-02-04T16:03:19.613 [DEBUG] Todos os processos foram criados.
2023-02-04T16:03:20.117 [INFO] O processo 0 não pode criar o arquivo A (falta de espaço)
...
```

The output of the process monitor will be saved at `out/monitor.txt`.