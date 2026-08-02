package k.p.domain;

/* JADX INFO: loaded from: classes.dex */
public class ReturnStatus<T> {
    private T status;
    private boolean success;
    public static final ReturnStatus<Void> TRUE = new ReturnStatus<>(true);
    public static final ReturnStatus<Void> FALSE = new ReturnStatus<>(false);

    public ReturnStatus(boolean success, T status) {
        this.success = success;
        this.status = status;
    }

    public ReturnStatus(boolean success) {
        this(success, null);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T getStatus() {
        return this.status;
    }
}
