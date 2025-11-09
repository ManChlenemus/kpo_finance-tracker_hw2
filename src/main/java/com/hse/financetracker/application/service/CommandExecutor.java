package com.hse.financetracker.application.service;

import com.hse.financetracker.application.command.Command;
import com.hse.financetracker.infrastructure.decorator.TimingCommandDecorator;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor {

    public <T> T execute(Command<T> command) {
        Command<T> decoratedCommand = new TimingCommandDecorator<>(command);
        return decoratedCommand.execute();
    }
}