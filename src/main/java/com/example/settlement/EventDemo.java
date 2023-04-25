package com.example.settlement;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yangwu_i
 * @date 2023/4/15 12:25
 */
public class EventDemo {
    static interface DomainEvent {}

    @Getter
    static class UserCreatedEvent implements DomainEvent {
        private final String name = "user created";
    }

    static interface DomainEventHandler<T extends DomainEvent> {
        boolean canHandle(T event);
        void handle(T event);
    }


    static class UserCreatedHandler implements DomainEventHandler<UserCreatedEvent> {
        @Override
        public boolean canHandle(UserCreatedEvent event) {
            return event != null;
        }

        @Override
        public void handle(UserCreatedEvent event) {
            System.out.println("User created: " + event.getName());
        }
    }

    static class DomainEventPublisher {
        private List<DomainEventHandler> handlers;

        public DomainEventPublisher() {
            handlers = new ArrayList<>();
        }

        public void register(DomainEventHandler handler) {
            handlers.add(handler);
        }

        public void publish(DomainEvent event) {
            for (DomainEventHandler handler : handlers) {
                if (handler.canHandle(event)) {
                    handler.handle(event);
                }
            }
        }
    }

    public static void main(String[] args) {
        DomainEventPublisher publisher = new DomainEventPublisher();
        publisher.register(new UserCreatedHandler());
        publisher.publish(new UserCreatedEvent());
    }

}
