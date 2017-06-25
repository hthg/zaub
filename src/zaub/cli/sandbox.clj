(ns zaub.cli.sandbox
  (:require [zaub.core.board :refer :all]
            [zaub.core.unit :refer :all]
            [clojure.string :as str])
  (:import [java.io BufferedReader]))

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
