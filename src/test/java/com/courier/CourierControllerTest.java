package com.courier;

import com.courier.api.CourierController;
import com.courier.api.DistanceResponse;
import com.courier.api.EntryResponse;
import com.courier.api.LocationRequest;
import com.courier.application.CourierTrackingService;
import com.courier.application.LocationUpdate;
import com.courier.domain.StoreEntry;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CourierControllerTest {

    @Test
    void ingestLocationMapsRequestToDomainAndDelegatesToService() {
        CourierTrackingService service = mock(CourierTrackingService.class);
        CourierController controller = new CourierController(service);

        LocationRequest request = new LocationRequest("courier-1", 1234L, 40.0, 29.0);
        controller.ingestLocation(request);

        ArgumentCaptor<LocationUpdate> captor = ArgumentCaptor.forClass(LocationUpdate.class);
        verify(service).ingestLocation(captor.capture());

        LocationUpdate update = captor.getValue();
        assertEquals("courier-1", update.courierId());
        assertNotNull(update.location());
        assertEquals(1234L, update.location().timeMillis());
        assertEquals(40.0, update.location().lat());
        assertEquals(29.0, update.location().lng());
    }

    @Test
    void getTotalDistanceReturnsResponseWithCourierIdAndDistance() {
        CourierTrackingService service = mock(CourierTrackingService.class);
        when(service.getTotalDistanceMeters("courier-1")).thenReturn(42.5);

        CourierController controller = new CourierController(service);
        DistanceResponse response = controller.getTotalDistance("courier-1");

        assertEquals("courier-1", response.courierId());
        assertEquals(42.5, response.totalDistanceMeters(), 0.00001);
    }

    @Test
    void getEntriesMapsDomainEntriesToResponseDto() {
        CourierTrackingService service = mock(CourierTrackingService.class);
        when(service.getEntries("courier-1")).thenReturn(List.of(
                new StoreEntry("courier-1", "Test Store", 1000L),
                new StoreEntry("courier-1", "Another Store", 2000L)
        ));

        CourierController controller = new CourierController(service);
        List<EntryResponse> responses = controller.getEntries("courier-1");

        assertEquals(2, responses.size());
        assertEquals(new EntryResponse("courier-1", "Test Store", 1000L), responses.get(0));
        assertEquals(new EntryResponse("courier-1", "Another Store", 2000L), responses.get(1));
    }
}

