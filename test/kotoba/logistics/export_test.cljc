(ns kotoba.logistics.export-test
  (:require [clojure.test :refer [deftest is testing]]
            [kotoba.logistics :as log]
            [kotoba.logistics.export :as ex]))
(deftest csv-export
  (let [csv (ex/trackings->csv ["1Z999AA101234" "x"])]
    (is (re-find #"tracking,valid,normalized" csv))
    (is (re-find #"1Z999AA101234,yes" csv))))
