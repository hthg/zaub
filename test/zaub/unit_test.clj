(ns zaub.unit-test
  (:require [clojure.test :refer :all]
            [zaub.core :refer :all]  
            [zaub.unit :refer :all]))

(defn- unit-coll [coll]
  (map unit coll))

(def test-brd (create-board-from-cols (map unit-coll
                                           '(("a" "a" "a" "b" "b")
                                             ("c" "c" "c" "c")
                                             ("d" "d")
                                             ("e" "e" "e" "f" "f" "f")))))
