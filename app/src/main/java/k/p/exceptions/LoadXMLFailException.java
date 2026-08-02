package k.p.exceptions;

/* JADX INFO: loaded from: classes.dex */
public class LoadXMLFailException extends Exception {
    private static final long serialVersionUID = 4383384909241379331L;
    private int lineNumber;
    private String message;

    public LoadXMLFailException(int lineNumber, String message) {
        this.lineNumber = lineNumber;
        this.message = message;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.message;
    }
}
