# kotoba-logistics

[![CI](https://github.com/kotoba-lang/logistics/actions/workflows/ci.yml/badge.svg)](https://github.com/kotoba-lang/logistics/actions/workflows/ci.yml)

**Shipments, tracking numbers, routes and freight in pure Clojure.** A
[kotoba-lang](https://github.com/kotoba-lang) capability library for the
[`cloud-itonami-4920`](https://github.com/gftdcojp/cloud-itonami-4920)
community freight-transport open business: shipment records with status,
tracking-number normalization and structural validation, multi-modal route
legs, and freight consignments.

No network, no I/O. Portable `.cljc` across JVM / ClojureScript / SCI /
GraalVM.

## Contract

```clojure
(require '[kotoba.logistics :as log])

(log/tracking-valid? "1Z 999 AA1 0123 4567")     ; => true
(log/shipment "SH1" "Tokyo" "Osaka" "Yamato" :tracking "1Z999AA101234" :weight 2.5)
(log/leg "A" "B" :road :distance-km 50)
(log/consignment "C1" sh [leg1 leg2] :declared-value 1000)
```

## License

Apache License 2.0.
