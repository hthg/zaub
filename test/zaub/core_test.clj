(ns zaub.core-test
  (:require [clojure.test :refer :all]
            [zaub.core :refer :all]))

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
    (is (= ['(:a :d :g)
            '(:b :e :h)
            '(:c :f :i)]
           (get-rows (create-board-from-cols '((:a :b :c)
                                               (:d :e :f)
                                               (:g :h :i))))))))
