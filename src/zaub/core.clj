(ns zaub.core)

(def queue (clojure.lang.PersistentQueue/EMPTY))

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
  (vec (map (partial into queue) cols)))

(defn create-empty-board [size]
  (vec (take size (repeat queue))))
