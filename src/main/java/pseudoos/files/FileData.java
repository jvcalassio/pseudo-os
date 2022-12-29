package files;

public class FileData {

    private final Integer startingPosition;
    private final int size;

    private final FileOwnedBy ownedBy;
    private final Integer ownerPID;

    public FileData(final Integer startingPosition,
                    final int size,
                    final FileOwnedBy ownedBy,
                    final Integer ownerPID) {
        this.startingPosition = startingPosition;
        this.size = size;
        this.ownedBy = ownedBy;
        this.ownerPID = ownerPID;
    }

    public FileData(final int size, final FileOwnedBy ownedBy, final Integer ownerPID) {
        this.startingPosition = null;
        this.size = size;
        this.ownedBy = ownedBy;
        this.ownerPID = ownerPID;
    }

    public Integer getStartingPosition() {
        return startingPosition;
    }

    public int getSize() {
        return size;
    }

    public FileOwnedBy getOwnedBy() {
        return ownedBy;
    }

    public Integer getOwnerPID() {
        return ownerPID;
    }
}
