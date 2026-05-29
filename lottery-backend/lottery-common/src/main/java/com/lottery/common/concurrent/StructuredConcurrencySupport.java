package com.lottery.common.concurrent;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class StructuredConcurrencySupport {

    private static final java.util.concurrent.ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public <T> T execute(Callable<T> task) throws Exception {
        try {
            return executor.submit(task).get();
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception exception) {
                throw exception;
            }
            throw new IllegalStateException("Structured task failed", cause);
        }
    }

    public <T> List<T> executeAll(List<Callable<T>> tasks) throws Exception {
        try {
            List<Future<T>> futures = tasks.stream()
                    .map(executor::submit)
                    .toList();
            return futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException("Task interrupted", e);
                        } catch (ExecutionException e) {
                            Throwable cause = e.getCause();
                            if (cause instanceof RuntimeException re) {
                                throw re;
                            }
                            throw new IllegalStateException("Task failed", cause);
                        }
                    })
                    .toList();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Structured tasks failed", e);
        }
    }
}
