package at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes;

public enum StatusText {
    OK("OK"),
    FAIL("FAIL");

    private final String text;

    StatusText(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
