package common.block;


public class Block {
    private Integer content;
    private boolean used;

    public Block() {
        this.content = null;
        this.used = false;
    }

    public void alloc(Integer content) {
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

    public Integer getContent() {
        return content;
    }
}
