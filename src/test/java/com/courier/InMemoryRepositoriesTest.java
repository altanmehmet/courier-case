package com.courier;

import com.courier.application.CourierState;
import com.courier.domain.StoreEntry;
import com.courier.infra.InMemoryCourierStateRepository;
import com.courier.infra.InMemoryEntryLogRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryRepositoriesTest {

    @Test
    void courierStateRepositoryReturnsSameInstancePerCourierId() {
        InMemoryCourierStateRepository repo = new InMemoryCourierStateRepository();
        CourierState a1 = repo.getOrCreate("a");
        CourierState a2 = repo.getOrCreate("a");
        CourierState b = repo.getOrCreate("b");

        assertSame(a1, a2);
        assertNotSame(a1, b);
    }

    @Test
    void courierStateRepositoryRejectsNullKeyBecauseConcurrentHashMap() {
        InMemoryCourierStateRepository repo = new InMemoryCourierStateRepository();
        assertThrows(NullPointerException.class, () -> repo.getOrCreate(null));
    }

    @Test
    void entryLogRepositoryReturnsUnmodifiableSnapshotsAndDoesNotMutateOldSnapshot() {
        InMemoryEntryLogRepository repo = new InMemoryEntryLogRepository();
        repo.add(new StoreEntry("c1", "S1", 1L));

        List<StoreEntry> snapshot1 = repo.findByCourierId("c1");
        assertEquals(1, snapshot1.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot1.add(new StoreEntry("c1", "S2", 2L)));

        repo.add(new StoreEntry("c1", "S2", 2L));
        List<StoreEntry> snapshot2 = repo.findByCourierId("c1");
        assertEquals(2, snapshot2.size());
        assertEquals(1, snapshot1.size(), "old snapshot must remain unchanged");
    }

    @Test
    void entryLogRepositoryReturnsEmptyListWhenNoEntries() {
        InMemoryEntryLogRepository repo = new InMemoryEntryLogRepository();
        assertEquals(List.of(), repo.findByCourierId("missing"));
    }
}

