(ns zaub.core.board-test
  (:require [clojure.test :refer :all]
            [zaub.core.board :refer :all]))

(deftest test-create-empty-board
  (let [siz (rand-int 100)
        brd (create-empty-board siz)]
    (is (vector? brd)
        "creates a board that IS NOT a vector")
    (is (= siz (count brd))
        "creates INCORRECT number of cols")
    (is (every? queue? brd)
        "creates board NOT MADE UP of queues")))

(deftest test-create-board-from-cols
  (let [cols '((:a :b :c) (:d :e))
        brd (create-board-from-cols cols)]
    (is (vector? brd)
        "creates a board that IS NOT a vector")
    (is (every? queue? brd)
        "creates board NOT MADE UP of queues")
    (is (= (mapv #(into queue %) cols) brd))))

(deftest test-get-rows
  (let [cols '((:a :b :c) (:d :e) (:g) (:j :k :l))
        rows '((:a :d :g :j) (:b :e nil :k) (:c nil nil :l))]
    (is (= rows (get-rows (create-board-from-cols cols)))
        "returns INCORRECT rows")))

(deftest test-push-into-nth
  (let [cols '((:a :b :c) (:d) (:e :f) (:g) ())
        orig-brd (create-board-from-cols cols)
        col-n 2
        brd (push-into-nth orig-brd 2 :z)]
    (is (vector? brd)
        "modified board is NOT a vector")
    (is (queue? (brd col-n))
        "modified col is NOT a queue")
    (is (= '(:e :f :z) (seq (brd col-n)))
        "modified non-empty col does NOT have the correct elements")  
    (is (= '(:z) (seq ((push-into-nth brd 4 :z) 4)))
        "modified empty col does NOT have the correct elements")))

(deftest test-pop-from-nth
  (let [cols '((:a :b :c) (:d) (:e :f) (:g) ())
        orig-brd (create-board-from-cols cols)
        col-n 2
        brd (pop-from-nth orig-brd 2)]
    (is (vector? brd)
        "modified board is NOT a vector")
    (is (queue? (brd col-n))
        "modified col is NOT a queue")
    (is (= '(:f) (seq (brd col-n)))
        "modified non-empty col does NOT have the correct elements")))
