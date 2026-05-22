package com.lottery.common.concurrent;

import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Component
public class StructuredConcurrencySupport {

    // 结构化并发：try-with-resources 确保虚拟线程 executor 在任务完成后自动关闭，
    // 子任务全部结束时 scope 才退出，避免线程泄漏
    public <T> T execute(Callable<T> task) throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var future = executor.submit(task);
            try {
                return future.get();
            } catch (ExecutionException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof Exception exception) {
                    throw exception;
                }
                throw new IllegalStateException("Structured task failed", cause);
            }
        }
    }
}
