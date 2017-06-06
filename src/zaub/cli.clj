(ns zaub.cli
  (:require [zaub.core :as zaub]
            [clojure.string :as str])
  (:import [java.io BufferedReader]
           [zaub.core Game]))

(def unit-str-map {0 "s"
               1 "m"})

(def unit-id {"s" zaub.core/soldier
              "m" zaub.core/mage})

(defn print-help [opts]
  (println (str/join "\n"
                     (into ["Commands" "--------"]
                           (map (partial str)
                                (keys opts)
                                (repeat " : ")
                                (vals opts))))))

(defn unit-str [unit]
  (if unit
    (let [s (unit-str-map (:id unit))]
      (if (:active unit)
        (str/upper-case s)
        s))
    "_"))

(defn coll-str [coll]
  (str/join " " (map unit-str coll)))

(defn board-row-strs [brd]
  (map coll-str
       (zaub/get-rows (zaub/pad-cols brd 5))))

(defn print-board [game]
  (println (str/join "\n" (board-row-strs (:board game)))))

(defn insert-unit [game col-n-str unit]
  (assoc game :board (zaub/insert-unit (:board game)
                                       (Integer/parseInt col-n-str)
                                       (zaub/make-unit (get unit-id unit)))))

(defn remove-unit [game col-n-str]
  (assoc game :board (zaub/remove-unit (:board game)
                                       (Integer/parseInt col-n-str))))

(def opts-map {:help "print help"
               :unit "insert unit, COL-N UNIT-ID"
               :remove-unit "remove unit, COL-N"
               :print-board "print game board"
               :quit "quit"
               :new "new game"})

(defmulti opt (fn [game cmd & args] (keyword cmd)))

(defmethod opt :default [game cmd & _]
  (println "Unknown command:" cmd)
  (print-help opts-map)
  game)

(defmethod opt :help [game & _]
  (print-help opts-map)
  game)

(defmethod opt :unit [game cmd & args]
  (let [[col-n-str unit-id-str] args]
    (insert-unit game col-n-str unit-id-str)))

(defmethod opt :remove-unit [game cmd & args]
  (let [[col-n-str] args]
    (remove-unit game col-n-str)))

(defmethod opt :print-board [game & _]
  (print-board game)
  game)

(defmethod opt :quit [& _]
  nil)

(defmethod opt :new [game & _]
  (zaub/create-new-game 6 5)
  game)

(defn handle [game line]
  (when (not (nil? line))
    (println)
    (let [game (apply opt game (str/split line #" "))]
      (println)
      game)))

(defn -main [& args]
  (println "Welcome to Zaub!")
  (println "Type help to view commands")
  (loop [game (zaub/create-new-game 6 5)
         lines (line-seq (BufferedReader. *in*))]
    (when (not (nil? game))
      (recur (handle game (first lines)) (rest lines))))
  "Bye")
