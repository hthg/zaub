(ns zaub.core
  (:gen-class)
  (:require [clojure.string :as str]))

(def queue (clojure.lang.PersistentQueue/EMPTY))

(defrecord Game [board
                 row-n])

(defrecord UnitType [id
                     health
                     pow
                     def
                     delay])

(defrecord Unit [id dmg])

(def soldier (->UnitType 0
                         100
                         10
                         10
                         3))

(def mage (->UnitType 1
                      80
                      20
                      5
                      4))

(def unit-types [soldier mage])

(defn make-unit [unit-type]
  (map->Unit (merge unit-type
                    {:dmg 0
                     :active false
                     :ticks 0
                     :ticking false})))

(defn pad-col [col n]
  (if (<= n 0)
    col
    (recur (conj col nil) (dec n))))

(defn pad-cols [cols max-len]
  (map pad-col
       cols
       (map (partial - max-len)
            (map count cols))))

(defn get-rows [brd]
  (when (every? not-empty brd)
    (conj (get-rows (map pop brd))
          (map peek brd))))

(defn do-to-nth-col [brd col-n func]
  (let [col (nth brd col-n nil)]
    (when-not (nil? col)
      (let [new-brd (take-last (- (count brd) col-n 1) brd)]
        (into (conj new-brd (func col))
              (reverse (take col-n brd)))))))

(defn insert-unit [brd col-n unit]
  (do-to-nth-col brd col-n
                 (fn [col] (conj col unit))))

(defn remove-unit [brd col-n]
  (do-to-nth-col brd col-n
                 (fn [col] (pop col))))

(defn create-empty-board [col-n]
  ((fn add-cols [brd col-n]
     (if (<= col-n 0)
       brd
       (recur (cons queue brd) (dec col-n))))
   '() col-n))

(defn create-random-board [col-n unit-n unit-type-set]
  ((fn add-units [brd unit-n unit-set]
     (if (<= unit-n 0)
       brd
       (recur (insert-unit brd
                           (rand-int (count brd))
                           (make-unit (rand-nth unit-type-set)))
              (dec unit-n)
              unit-set)))
   (create-empty-board col-n) unit-n unit-type-set))

(defn create-new-game [col-n row-n]
  (->Game (create-random-board col-n row-n unit-types)
         row-n))
