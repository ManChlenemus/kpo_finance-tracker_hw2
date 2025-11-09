package com.hse.financetracker.infrastructure.decorator;

import com.hse.financetracker.application.command.Command;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimingCommandDecorator<T> implements Command<T> {

    private final Command<T> wrappedCommand;

    @Override
    public T execute() {
        long startTime = System.nanoTime();
        T result = wrappedCommand.execute();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;

        System.out.println("[LOG] Execution time for " + wrappedCommand.getClass().getSimpleName() + ": " + duration + "ms");
        return result;
    }
}