package resources;

import exception.InsufficientResources;

// Gerenciador de resursos do pseudoOS.
public class ResourcesManager {

    private int scanner;

    private int printer;

    private int modem;

    private int sata;

    // Inicializa os recursos com os valores definidos na especificacao.
    public ResourcesManager() {
        scanner = 1;
        printer = 2;
        modem = 1;
        sata = 2;
    }

    public boolean requestScanner() {
        if (scanner > 0) {
            scanner--;
            return true;
        }
        throw new InsufficientResources();
    }

    public boolean requestPrinter() {
        if (printer > 0) {
            printer--;
            return true;
        }
        throw new InsufficientResources();
    }

    public boolean requestModem() {
        if (modem > 0) {
            modem--;
            return true;
        }
        throw new InsufficientResources();
    }

    public boolean requestSata() {
        if (sata > 0) {
            sata--;
            return true;
        }
        throw new InsufficientResources();
    }

    public void refundScanner() {
        scanner++;
    }

    public void refundPrinter() {
        printer++;
    }

    public void refundModem() {
        modem++;
    }

    public void refundSata() {
        sata++;
    }
}
