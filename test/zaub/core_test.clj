(ns zaub.core-test
  (:require [clojure.test :refer :all]
            [zaub.core :refer :all]))

(def test-brd (create-board-from-cols (map unit-coll
                                           '(("a" "a" "a" "b" "b")
                                             ("c" "c" "c" "c")
                                             ("d" "d")
                                             ("e" "e" "e" "e" "f" "f" "f" "f")))))

(deftest test-create-empty-board
  (testing "created with correct number of cols"
    (is (let [siz (rand-int 100)]
          (= siz (count (create-empty-board siz))))))
  (testing "made up of collections"
    (is (every? coll? (create-empty-board 3)))))

(deftest test-create-board-from-cols
  (testing "correctly create board"
    (is (= ['(:a :b :c)
            '(:d :e :f)]
           (create-board-from-cols '((:a :b :c)
                                     (:d :e :f)))))))

(deftest test-push-into-nth
  (testing "correctly pushed into queue"
    (is (let [brd-siz (rand-int 100)
              col-n (rand-int brd-siz)
              elem :tst]
          (some #{elem} (nth (push-into-nth (create-empty-board brd-siz)
                                            col-n
                                            elem)
                             col-n))))))

(deftest test-get-rows
  (testing "correctly gets rows"
    (is (= ['(:a :d :g :j)
            '(:b :e :h :k)
            '(:c :f :i :l)]
           (get-rows test-brd)))))

(deftest test-pop-from-nth
  (testing "correctly pops from queue"
    (is (assoc test-brd 2 (pop (nth test-brd 2)))
        (pop-from-nth test-brd 2))))

(deftest test-activate
  (is (= {:active true}
         (activate {}))))
