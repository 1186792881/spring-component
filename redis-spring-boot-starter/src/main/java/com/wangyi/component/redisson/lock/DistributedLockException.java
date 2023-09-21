package com.wangyi.component.redisson.lock;

public class DistributedLockException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;

    public DistributedLockException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public DistributedLockException(Exception e) {
        super(e);
        this.code = 500;
        this.message = e.getMessage();
    }

    public DistributedLockException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
