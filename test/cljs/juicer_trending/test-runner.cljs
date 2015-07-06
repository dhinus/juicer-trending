(ns juicer_trending.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [juicer_trending.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'juicer_trending.core-test))
    0
    1))
