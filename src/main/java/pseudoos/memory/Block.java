package memory;

public class Block {
    private Integer content;
    private boolean used;

    public Block() {
        this.content = null;
        this.used = false;
    }

    public void alloc(int content) {
        this.content = content;
        this.used = true;
    }

    public void free() {
        this.content = null;
        this.used = false;
    }

    public boolean isUsed() {
        return used;
    }

    public int getContent() {
        return content;
    }

}
