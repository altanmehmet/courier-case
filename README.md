# Courier Tracking Case

Clean, in-memory courier tracking service with REST API. It ingests courier locations, logs store entrances within 100m (with a 1 minute cooldown), and exposes total traveled distance per courier.

## Requirements
- Java 17+ (tested with 17)
- Maven 3.9+

## Run
```bash
mvn spring-boot:run
```

## API
### Ingest location (POST)
```bash
curl -X POST http://localhost:8080/locations \
  -H 'Content-Type: application/json' \
  -d '{"courierId":"courier-1","timeMillis":1723900000000,"lat":40.9923307,"lng":29.1244229}'
```
- Request body:
  - `courierId` (string, required, not blank)
  - `timeMillis` (number, required, >= 1, epoch millis)
  - `lat` (double, -90..90), `lng` (double, -180..180)
- Responses: `202 Accepted` on success; `400` with validation errors; `401/403` if JWT security is enabled and missing/invalid token.

### Get total distance (GET)
```bash
curl http://localhost:8080/couriers/courier-1/distance
```
- Response example:
  ```json
  {"courierId":"courier-1","totalDistanceMeters":123.45}
  ```

### Get store entries (GET)
```bash
curl 'http://localhost:8080/entries?courierId=courier-1'
```
- Response example:
  ```json
  [
    {"courierId":"courier-1","storeName":"Test Store","timeMillis":1723900000000}
  ]
  ```

## Notes
- Time format is epoch millis.
- Distances are returned in meters.
- Store data is loaded from `src/main/resources/stores.json`.

## Test
```bash
mvn test
```

## Security (JWT)
- Default: **disabled** (`security.enabled=false`), all endpoints are open.
- To enable it via `application.properties` or arguments:
  ```
  security.enabled=true
  security.hmac-secret=please-change-me-32chars-placeholder
  ```
  Then send `Authorization: Bearer <jwt>` on every API call (HS256, with `sub` and `exp` claims).
- The value `please-change-me-32chars-placeholder` is a **demo placeholder** committed only for local testing and the case review.  
  In a real deployment this secret must come from environment/secret management and **never** be committed to version control.
- **Demo/Test token generation** (using the same `security.hmac-secret`):
  - On `jwt.io`:
    - Header: `{"alg":"HS256","typ":"JWT"}`
    - Payload: `{"sub":"test-user","exp": <future unix epoch seconds>}`
    - Secret: `please-change-me-32chars-placeholder`
