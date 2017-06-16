(ns zaub.cli.sandbox-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [zaub.core.board :as zboard]   
            [zaub.cli.sandbox :refer :all])) 

(def test-brd (zboard/create-board-from-cols '(("a" "b" "c")
                                             ("d")
                                             ("e" "f"))))
(def test-game-info {:board test-brd :col-size 5})

(deftest test-get-rows
  (zboard/get-rows test-brd))
(deftest test-print-board
  (handle-cmd "print-board" test-game-info))
(deftest test-pad-board
  (pad-board {:col-size 6 :board test-brd}))
(deftest test-board-str
  (board-str test-game-info))
(deftest test-push-unit
  (assoc test-game-info
         :board
         (zboard/push-into-nth (:board test-game-info)
                             5
                             :e)))
