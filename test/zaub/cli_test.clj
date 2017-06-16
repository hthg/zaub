(ns zaub.cli-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [zaub.core :as zaub]   
            [zaub.cli :refer :all])) 

(def test-brd (zaub/create-board-from-cols '(("a" "b" "c")
                                             ("d")
                                             ("e" "f"))))
(def test-game-info {:board test-brd :col-size 5})

(deftest test-get-rows
  (zaub/get-rows test-brd))
(deftest test-print-board
  (handle-cmd "print-board" test-game-info))
(deftest test-pad-board
  (pad-board {:col-size 6 :board test-brd}))
(deftest test-board-str
  (board-str test-game-info))
(deftest test-push-unit
  (assoc test-game-info
         :board
         (zaub/push-into-nth (:board test-game-info)
                             5
                             :e)))
