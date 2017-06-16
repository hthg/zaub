(ns zaub.core)

(def queue (clojure.lang.PersistentQueue/EMPTY))

(defn queue? [c]  (isa? clojure.lang.PersistentQueue
                        (class c)))

(defn get-rows [brd]
  (loop [rows '()
         cur-brd brd]
    (if (every? empty? cur-brd)
      (reverse rows)
      (recur (cons (map first cur-brd) rows)
             (map rest cur-brd)))))

(defn push-into-nth [brd index elem]
  (assoc brd index (conj (nth brd index)
                         elem)))

(defn pop-from-nth [brd index]
  (assoc brd index (pop (nth brd index))))

(defn create-board-from-cols [cols]
  (mapv (partial into queue) cols))

(defn create-empty-board [size]
  (vec (take size (repeat queue))))

(defn push-into-random-col [brd elem]
  (push-into-nth brd (rand-int (count brd)) elem))

(defn create-random-board
  ([brd rand-coll]
   (reduce push-into-random-col
           brd
           rand-coll))
  ([brd rand-coll trans-fn]
   (reduce push-into-random-col
           brd
           (map trans-fn rand-coll))))
