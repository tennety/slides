(ns slides.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [slides.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'slides.core-test))
    0
    1))
