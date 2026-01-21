package com.courier.api;

import com.courier.application.CourierTrackingService;
import com.courier.application.LocationUpdate;
import com.courier.domain.Location;
import com.courier.domain.StoreEntry;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

@RestController
public class CourierController {
    private final CourierTrackingService courierTrackingService;

    public CourierController(CourierTrackingService courierTrackingService) {
        this.courierTrackingService = courierTrackingService;
    }

    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void ingestLocation(@Valid @RequestBody LocationRequest request) {
        Location location = new Location(request.timeMillis(), request.lat(), request.lng());
        courierTrackingService.ingestLocation(new LocationUpdate(request.courierId(), location));
    }

    @GetMapping("/couriers/{courierId}/distance")
    public DistanceResponse getTotalDistance(@PathVariable("courierId") String courierId) {
        double totalDistance = courierTrackingService.getTotalDistanceMeters(courierId);
        return new DistanceResponse(courierId, totalDistance);
    }

    @GetMapping("/entries")
    public List<EntryResponse> getEntries(@RequestParam("courierId") String courierId) {
        List<StoreEntry> entries = courierTrackingService.getEntries(courierId);
        return entries.stream()
                .map(entry -> new EntryResponse(entry.courierId(), entry.storeName(), entry.timeMillis()))
                .toList();
    }
}
