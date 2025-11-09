package com.hse.financetracker.application.command;

@FunctionalInterface
public interface Command<T> {
    T execute();
}