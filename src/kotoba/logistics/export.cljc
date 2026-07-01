(ns kotoba.logistics.export
  "Operator-facing export for a freight-transport actor.

  Renders tracking validation, shipments and consignments to CSV and JSON for
  settlement audit and downstream reporting. Pure data → text: no network."
  (:require [clojure.string :as str]
            [kotoba.logistics :as log]))

(defn- csv-cell [v]
  (let [s (str (if (nil? v) "" v))]
    (if (re-find #"[\",\n]" s)
      (str "\"" (str/replace s "\"" "\"\"") "\"")
      s)))

(defn- csv-row [vals] (str/join "," (map csv-cell vals)))

(defn- json-str [v]
  (-> (str (if (nil? v) "" v))
      (str/replace "\\" "\\\\")
      (str/replace "\"" "\\\"")
      (str/replace "\n" "\\n")))

(defn trackings->csv [trackings]
  (str/join "\n"
    (cons (csv-row ["tracking" "valid" "normalized"])
          (for [t trackings]
            (let [r (log/validate-tracking t)]
              (csv-row [t
                        (if (:logistics/valid? r) "yes" "no")
                        (or (:logistics/normalized r) "")]))))))

(defn shipments->csv [shipments]
  (str/join "\n"
    (cons (csv-row ["shipment_id" "origin" "destination" "carrier" "tracking" "status"])
          (for [s shipments]
            (csv-row [(:shipment/id s)
                      (:shipment/origin s)
                      (:shipment/destination s)
                      (:shipment/carrier s)
                      (or (:shipment/tracking s) "")
                      (name (:shipment/status s))])))))

(defn consignments->csv [consignments]
  (str/join "\n"
    (cons (csv-row ["consignment_id" "shipment" "legs" "declared_value" "status"])
          (for [c consignments]
            (csv-row [(:cons/id c)
                      (:cons/shipment c)
                      (count (:cons/route c))
                      (or (:cons/declared-value c) "")
                      (name (:cons/status c))])))))

(defn trackings->json [trackings]
  (str "["
       (str/join ","
                 (for [t trackings]
                   (let [r (log/validate-tracking t)]
                     (str "{\"tracking\":\"" (json-str t) "\","
                          "\"valid\":" (if (:logistics/valid? r) "true" "false") ","
                          "\"normalized\":\"" (json-str (:logistics/normalized r)) "\"}"))))
       "]"))

(defn shipments->json [shipments]
  (str "["
       (str/join ","
                 (for [s shipments]
                   (str "{\"shipment_id\":\"" (json-str (:shipment/id s)) "\","
                        "\"origin\":\"" (json-str (:shipment/origin s)) "\","
                        "\"destination\":\"" (json-str (:shipment/destination s)) "\","
                        "\"carrier\":\"" (json-str (:shipment/carrier s)) "\","
                        "\"status\":\"" (name (:shipment/status s)) "\"}")))
       "]"))
