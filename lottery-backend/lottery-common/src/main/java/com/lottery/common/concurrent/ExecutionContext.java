package com.lottery.common.concurrent;

public final class ExecutionContext {

    // InheritableThreadLocal 可让子线程继承父线程的 trace ID，但与虚拟线程不兼容——
    // 虚拟线程创建时不会触发 inherit，需配合 MDC 或 Reactor Context 在响应式链路中传播
    private static final InheritableThreadLocal<String> TRACE_ID = new InheritableThreadLocal<>();

    private ExecutionContext() {
    }

    public static void bindTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String currentTraceId() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
