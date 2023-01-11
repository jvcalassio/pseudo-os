package resources;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Resource {

    private int quantity;

    private Semaphore semaphore;

    private List<Integer> PIDs;

    public Resource(int quantity) {
        this.quantity = quantity;
        this.semaphore = new Semaphore(quantity);
        PIDs = new ArrayList<>();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    public List<Integer> getPIDs() {
        return PIDs;
    }

    public void setPIDs(List<Integer> PIDs) {
        this.PIDs = PIDs;
    }

    public void addPID(int PID){
        PIDs.add(PID);
    }

    public void removePID(int PID){
        PIDs.remove((Integer) PID);
    }
}
