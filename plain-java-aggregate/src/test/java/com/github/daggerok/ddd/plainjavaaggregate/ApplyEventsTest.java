package com.github.daggerok.ddd.plainjavaaggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

// interface Command {}
// @Value class Activate implements Command {}
// @Value class Increment implements Command {}
// @Value class Suspend implements Command {}

interface DomainEvent { }
@Data class Activated implements DomainEvent { }
@Data class Incremented implements DomainEvent { }
@Data class Suspended implements DomainEvent { }

interface Aggregate extends Function<DomainEvent, Aggregate> {

    Aggregate copy();

    @SuppressWarnings("unchecked")
    static <A extends Aggregate> A restore(A snapshot, Iterable<DomainEvent> eventStream) {
        A modified = (A) snapshot.copy();
        for (DomainEvent event : eventStream) {
            modified = (A) modified.apply(event);
        }
        return modified;
    }

    default <A extends Aggregate> A recreate(A snapshot, Iterable<DomainEvent> eventStream) {
        return Aggregate.restore(snapshot, eventStream);
    }
}

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
class MyAggregate implements Aggregate {

    private boolean active;
    private int state;

    @Override
    public Aggregate copy() {
        return MyAggregate.of(active, state);
    }

    @Override // recover
    public MyAggregate apply(DomainEvent event) {
        if (event instanceof Activated) {
            active = true;
            return this;
        }
        if (event instanceof Incremented) {
            state += 1;
            return this;
        }
        if (event instanceof Suspended) {
            active = false;
            return this;
        }
        throw new RuntimeException("unhandled DomainEvent: " + event.toString());
    }
}

class ApplyEventsTest {

    @Test
    void test_apply() throws Exception {
        // given:
        List<DomainEvent> history = Arrays.asList(
                new Incremented(),
                new Incremented(),
                new Suspended()
        );
        // and:
        MyAggregate aggregate = MyAggregate.class.newInstance()
                                                 .apply(new Activated());
        // when:
        for (DomainEvent event : history) {
            aggregate = aggregate.apply(event);
        }
        // then:
        assertThat(aggregate.getState()).isEqualTo(2);
        // and:
        assertThat(aggregate.isActive()).isFalse();
    }

    @Test
    void test_restore() throws Exception {
        // given:
        List<DomainEvent> history = Arrays.asList(
                new Incremented(),
                new Incremented(),
                new Suspended()
        );
        // and:
        MyAggregate snapshot = MyAggregate.class.newInstance()
                                                .apply(new Activated());
        // when:
        MyAggregate restored = Aggregate.restore(snapshot, history);
        // then:
        assertThat(restored.getState()).isEqualTo(2);
        // and:
        assertThat(restored.isActive()).isFalse();
    }

    @Test
    void test_recreate() throws Exception {
        // given:
        List<DomainEvent> history = Arrays.asList(
                new Incremented(),
                new Incremented(),
                new Suspended()
        );
        // and:
        MyAggregate snapshot = MyAggregate.class.newInstance()
                                                .apply(new Activated());
        // when:
        MyAggregate recreated = snapshot.recreate(snapshot, history);
        // then:
        assertThat(recreated.getState()).isEqualTo(2);
        // and:
        assertThat(recreated.isActive()).isFalse();
    }
}
