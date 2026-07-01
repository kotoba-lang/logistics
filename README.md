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

## Operator console (UI/UX)

A read-only HTML dashboard renders tracking validation, shipments (status badges) and consignments for an operator. Built on
[`kotoba-lang/html`](https://github.com/kotoba-lang/html) (Hiccup→HTML) +
[`kotoba-lang/css`](https://github.com/kotoba-lang/css) (EDN→CSS). Pure data
→ markup; the console never exposes a write surface (no `<form>`/`<button>`)
— writes stay behind the governor.

```clojure
(require '[kotoba.logistics.ui :as ui])

(ui/dashboard
  {:trackings ["1Z999AA101234"]
   :shipments [(log/shipment "SH1" "Tokyo" "Osaka" "Yamato" :status :in-transit)]
   :consignments [(log/consignment "C1" sh [leg])]})
;; => "<html>...read-only · governor-gated...</html>"
```

## Export (CSV / JSON)

Audit-grade CSV (RFC-4180 quoting) and JSON (quote/backslash/newline
escaped) for tracking validation, shipments and consignments.

```clojure
(require '[kotoba.logistics.export :as ex])

(ex/trackings->csv trackings)
(ex/shipments->csv shipments)  ; status
(ex/trackings->json trackings)
```

## License

Apache License 2.0.
