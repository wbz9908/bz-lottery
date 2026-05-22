package com.lottery.common.web;

// 仅作为 trace ID 常量容器，不是 Servlet Filter——实际传播逻辑在 gateway 的 traceWebFilter 中
public final class TraceIdFilter {

    public static final String TRACE_ID = "X-Trace-Id";
    public static final String MDC_TRACE_ID = "traceId";

    private TraceIdFilter() {
    }
}
