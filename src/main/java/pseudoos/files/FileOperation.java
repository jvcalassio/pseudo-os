package files;

public enum FileOperation {
    CREATE("0"),
    DELETE("1");

    private final String operationNumber;

    FileOperation(String operationNumber) {
        this.operationNumber = operationNumber;
    }

    public static FileOperation fromString(final String number) {
        for (FileOperation op : FileOperation.values()) {
            if (op.getOperationNumber().equals(number)) {
                return op;
            }
        }
        throw new IllegalArgumentException(number);
    }

    public String getOperationNumber() {
        return operationNumber;
    }

}
