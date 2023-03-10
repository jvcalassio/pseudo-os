package resources;

import exception.InsufficientResources;
import util.Logger;

// Gerenciador de resursos do pseudoOS.
public class ResourcesManager {

    private static ResourcesManager instance;

    // Quantidade disponivel - PID do processo que esta usando
    private final Resource scanner;

    private final Resource printer;

    private final Resource modem;

    private final Resource sata;

    public static ResourcesManager getInstance() {
        if (instance == null) {
            instance = new ResourcesManager();
        }
        return instance;
    }

    // Inicializa os recursos com os valores definidos na especificacao.
    public ResourcesManager() {
        this.scanner = new Resource(1);
        this.printer = new Resource(2);
        this.modem = new Resource(1);
        this.sata = new Resource(2);
    }

    public boolean requestScanner(int PID) throws InsufficientResources {
        if (scanner.getQuantity() > 0) {
            scanner.setQuantity(scanner.getQuantity() - 1);
            scanner.addPID(PID);
            Logger.debug("Scanner alocado para o processo " + PID);
            return true;
        }
        throw new InsufficientResources("Scanner");
    }

    public boolean requestPrinter(int PID) throws InsufficientResources {
        if (printer.getQuantity() > 0) {
            printer.setQuantity(printer.getQuantity() - 1);
            printer.addPID(PID);
            Logger.debug("Impressora alocado para o processo " + PID);
            return true;
        }
        throw new InsufficientResources("Printer");
    }

    public boolean requestModem(int PID) throws InsufficientResources {
        if (modem.getQuantity() > 0) {
            modem.setQuantity(modem.getQuantity() - 1);
            modem.addPID(PID);
            Logger.debug("Modem alocado para o processo " + PID);
            return true;
        }
        throw new InsufficientResources("Modem");
    }

    public boolean requestSata(int PID) throws InsufficientResources {
        if (sata.getQuantity() > 0) {
            sata.setQuantity(sata.getQuantity() - 1);
            sata.addPID(PID);
            Logger.debug("SATA alocado para o processo " + PID);
            return true;
        }
        throw new InsufficientResources("SATA");
    }

    public boolean refundScanner(int PID) {
        if (scanner.getPIDs().contains(PID)) {
            scanner.setQuantity(scanner.getQuantity() + 1);
            scanner.removePID(PID);
            return true;
        }
        return false;
    }

    public boolean refundPrinter(int PID) {
        if (printer.getPIDs().contains(PID)) {
            printer.setQuantity(printer.getQuantity() + 1);
            printer.removePID(PID);
            return true;
        }
        return false;
    }

    public boolean refundModem(int PID) {
        if (modem.getPIDs().contains(PID)) {
            modem.setQuantity(modem.getQuantity() + 1);
            modem.removePID(PID);
            return true;
        }
        return false;
    }

    public boolean refundSata(int PID) {
        if (sata.getPIDs().contains(PID)) {
            sata.setQuantity(sata.getQuantity() + 1);
            sata.removePID(PID);
            return true;
        }
        return false;
    }

    public Resource getScanner() {
        return scanner;
    }

    public Resource getPrinter() {
        return printer;
    }

    public Resource getModem() {
        return modem;
    }

    public Resource getSata() {
        return sata;
    }
}
