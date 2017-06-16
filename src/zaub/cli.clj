(ns zaub.cli
  (:require [zaub.core :refer :all]
            [zaub.unit :refer :all]
            [clojure.string :as str])
  (:import [java.io BufferedReader]))

(defn- parse-int [n]
  (Integer/parseInt n))

(defn print-help [opts]
  (println (str/join "\n"
                     (into ["Commands"
                            "--------"]
                           (map (partial str/join "  ")
                                (seq opts))))))


(def opts-map {:help "print help"
               :push-unit "insert unit, COL-N UNIT-ID"
               :pop-unit "remove unit, COL-N"
               :print-board "print game-info board"
               :new "new game"
               :activate "activate units"
               :quit "quit"})

(def unit-char {:a "a"
                :b "b"})

(defn- format-unit [unit]
  (format "%s" (if (some? unit)
                 (let [id (name (:tid unit))]
                   (if (:active unit)
                     (str/upper-case id)
                     id))
                 ".")))

(defn pad-col [min-size col]
  (reverse (take min-size (concat col (repeat nil)))))

(defn pad-board [col-size board]
  (map (partial pad-col col-size) board))

(defn board-str [{:keys [col-size board]}]
  (str/join "\n" (map (partial str/join " ")
                      (map (fn [m] (map format-unit m))
                           (get-rows (pad-board col-size board))))))

(defmulti handle-cmd (fn [cmd game-info & args] (keyword cmd)))

(defmethod handle-cmd :default [cmd game-info & _]
  (println "Unknown command:" cmd)
  (print-help opts-map)
  game-info)

(defmethod handle-cmd :help [cmd game-info & _]
  (print-help opts-map)
  game-info)

(defmethod handle-cmd :push-unit [cmd game-info col-n-str type-id]
  (let [col-n (parse-int col-n-str)
        {:keys [board col-size]} game-info]
    (if (>= col-size (count (nth board col-n)))
      (assoc game-info
             :board
             (push-into-nth board
                            col-n
                            (unit (keyword type-id) false 2 3)))
      game-info)))

(defmethod handle-cmd :pop-unit [cmd game-info col-n-str]
  (let [col-n (parse-int col-n-str)
        {board :board} game-info]
    (assoc game-info
           :board
           (pop-from-nth board
                         col-n))))

(defmethod handle-cmd :print-board [cmd game-info]
  game-info)

(defmethod handle-cmd :quit [cmd & _]
  nil)

(defn new-game []
  {:board (create-empty-board 6)
   :col-size 5
   :hp-left 20})

(defmethod handle-cmd :new [cmd & _]
  (new-game))

(defmethod handle-cmd :activate [cmd game-info]
  (assoc game-info :board
         (mapv activate-col (:board game-info))))

(defn handle [game-info line]
  (when (some? line)
    (let [tokens (str/split line #" ")
          game-info-after (apply handle-cmd
                                 (first tokens)
                                 game-info
                                 (rest tokens))]
      (if (some? game-info-after)
        (println (board-str game-info-after)))
      game-info-after)))

(defn sandbox [& args]
  (println "Welcome to Zaub!")
  (println "Type help to view commands")
  (print "> ")
  (flush)
  (loop [game-info new-game
         lines (line-seq (BufferedReader. *in*))]
    (if (some? game-info)
      (recur (handle game-info
                     (first lines))
             (do (print "> ")
                 (flush)
                 (rest lines)))
      "Bye")))

(defn random-units [unit-types n-units]
  (map #(unit % false 3 2)
       (take n-units (random-sample 0.1 (cycle unit-types)))))

(defn do-ticks [game]
  (assoc game :board (map tick-col (:board game))))

(defn do-attacks [game]
  (let [attacking (map (partial filter filter-attacking) (:board game))])
  game)

(defn do-user-cmd [game]
  game)

(defn do-activate [game]
  (assoc game :board (map activate-col (:board game))))

(defn do-turn [game]
  ((comp do-activate
         do-user-cmd
         do-attacks
         do-ticks) game))

(defn game []
  (println "Welcome to Zaub!")
  (println "Type help to view commands")
  (print "> ")
  (flush)
  (loop [game-info (assoc (new-game)
                          :board (push-random-units (:board (new-game))
                                                    '("a" "b" "c")
                                                    5))
         lines (line-seq (BufferedReader. *in*))]
    (if (some? game-info)
      (recur (handle game-info
                     (first lines))
             (do (print "> ")
                 (flush)
                 (rest lines)))
      "Bye")))

(defn -main [& args]
  (case (first args)
    "sandbox" (sandbox)
    (game)))
