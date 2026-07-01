(ns kotoba.logistics.ui-test
  (:require [clojure.test :refer [deftest is testing]]
            [kotoba.logistics :as log]
            [kotoba.logistics.ui :as ui]))

(deftest dashboard-renders-contracts
  (testing "empty dashboard renders a page"
    (let [html (ui/dashboard {})]
      (is (re-find #"<html>" html))
      (is (re-find #"Operator Console" html))))
  (testing "populated dashboard renders records"
    (let [html (ui/dashboard {:trackings ["1Z999AA101234"], :shipments [(log/shipment "SH1" "Tokyo" "Osaka" "Yamato" :tracking "1Z999AA101234" :status :in-transit)], :consignments [(log/consignment "C1" (log/shipment "SH1" "A" "B" "c") [(log/leg "A" "B" :road)])]})]
      (is (re-find #"in-transit" html)))))

(deftest dashboard-is-read-only
  (testing "the console never renders a write surface"
    (let [html (ui/dashboard {:trackings ["1Z999AA101234"], :shipments [(log/shipment "SH1" "Tokyo" "Osaka" "Yamato" :tracking "1Z999AA101234" :status :in-transit)], :consignments [(log/consignment "C1" (log/shipment "SH1" "A" "B" "c") [(log/leg "A" "B" :road)])]})]
      (is (re-find #"read-only · governor-gated" html))
      (is (not (re-find #"<form" html)))
      (is (not (re-find #"<button" html))))))
