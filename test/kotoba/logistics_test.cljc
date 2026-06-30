(ns kotoba.logistics-test
  (:require [clojure.test :refer [deftest is testing]]
            [kotoba.logistics :as log]))

(deftest tracking-test
  (is (log/tracking-valid? "1Z 999 AA1 0123 4567"))
  (is (not (log/tracking-valid? "SHORT")))
  (is (not (log/tracking-valid? nil)))
  (is (= :malformed-tracking (:logistics/error (log/validate-tracking "x")))))

(deftest shipment-test
  (let [s (log/shipment "SH1" "Tokyo" "Osaka" "Yamato" :tracking "1Z999AA101234" :weight 2.5 :items 3)]
    (is (= :booked (:shipment/status s)))
    (is (= 3 (:shipment/items s)))
    (is (not (log/delivered? s))))
  (is (nil? (log/shipment "SH1" "A" "B" "C" :status :frob))))

(deftest leg-test
  (is (= :road (:leg/mode (log/leg "A" "B" :road :distance-km 50))))
  (is (nil? (log/leg "A" "B" :teleport))))

(deftest consignment-test
  (let [s (log/shipment "SH1" "A" "B" "C")
        c (log/consignment "C1" s [(log/leg "A" "B" :road)] :declared-value 1000)]
    (is (= "SH1" (:cons/shipment c)))
    (is (= 1 (count (:cons/route c))))))
