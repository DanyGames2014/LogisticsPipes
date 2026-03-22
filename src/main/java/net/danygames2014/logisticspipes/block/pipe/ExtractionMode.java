package net.danygames2014.logisticspipes.block.pipe;

public enum ExtractionMode {
    Normal,
    LeaveFirst,
    LeaveLast,
    LeaveFirstAndLast,
    Leave1PerStack;

    public ExtractionMode next() {
        int next = this.ordinal() + 1;

        if (next >= values().length){
            next = 0;
        }
        return ExtractionMode.values()[next];
    }


    public String getExtractionModeString() {
        return switch (this) {
            case Normal -> "Normal";
            case LeaveFirst -> "Leave 1st stack";
            case LeaveLast -> "Leave last stack";
            case LeaveFirstAndLast -> "Leave first & last stack";
            case Leave1PerStack -> "Leave 1 item per stack";
            default -> "Unknown!";
        };
    }
}
