# Dependency Tracking

Per `docs/standards/licenses.md`, every significant dependency added to the project is
documented here.

| Package                                | Version | License    | Purpose                              | Added    | Notes                                     |
|-----------------------------------------|---------|------------|---------------------------------------|----------|--------------------------------------------|
| `com.hivemq:hivemq-mqtt-client`         | 1.3.17  | Apache 2.0 | MQTT client for Shopping List cloud sync (HiveMQ Cloud) | 2026-07  | Async, TLS-capable, official Android support |
| `androidx.lifecycle:lifecycle-process`  | 2.9.0   | Apache 2.0 | `ProcessLifecycleOwner` — drives MQTT connect/disconnect with app foreground/background | 2026-07  | Reuses the existing `lifecycleVersion` catalog entry |
| `org.json:json`                         | 20240303 | Public Domain | Real `org.json` impl for JVM unit tests exercising snapshot JSON (de)serialization | 2026-07 | Test-only; the Android SDK's `org.json` stub throws `not mocked` on the JVM test classpath |
