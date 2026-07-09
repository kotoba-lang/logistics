(ns kotoba.logistics.export-test
  (:require [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [kotoba.logistics :as log]
            [kotoba.logistics.export :as ex]))
(deftest csv-export
  (let [csv (ex/trackings->csv ["1Z999AA101234" "x"])]
    (is (re-find #"tracking,valid,normalized" csv))
    (is (re-find #"1Z999AA101234,yes" csv))))

(deftest shipments-csv-export-quotes-a-bare-carriage-return
  ;; RFC 4180 requires quoting a field containing CR, LF, or a comma --
  ;; \r alone is also a line terminator every standard CSV reader
  ;; recognizes, but the check here only ever covered \n. Verified
  ;; against Python's csv module: an unquoted bare \r split the row into
  ;; two corrupted rows on read-back.
  (let [s [(log/shipment "S1" (str "Warehouse" (char 13) "A") "Store B" "Carrier X")]
        csv (ex/shipments->csv s)]
    (is (str/includes? csv "\"Warehouse\rA\""))))

(deftest shipments-json-export-escapes-every-c0-control-character
  ;; RFC 8259 requires EVERY control character U+0000-U+001F to be
  ;; escaped, not just \ " and \n -- a shipment origin containing a raw
  ;; tab or other control byte would otherwise be copied through raw,
  ;; producing invalid JSON (verified against Python's strict json
  ;; module).
  (let [s [(log/shipment "S1" (str "Warehouse" (char 9) "A" (char 1) "x") "Store B" "Carrier X")]
        j (ex/shipments->json s)]
    (is (str/includes? j "\"origin\":\"Warehouse\\tA\\u0001x\""))))
