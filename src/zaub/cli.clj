(ns zaub.cli
  (:require [zaub.core :as zaub]
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
               :quit "quit"
               :new "new game"})

(defn- format-elem [elem]
  (format "%s" (if (some? elem) elem ".")))

(defn pad-col [min-size brd]
  (take min-size (concat brd (repeat nil))))

(defn pad-board [col-size board]
  (map (partial pad-col col-size) board))

(defn board-str [{:keys [col-size board]}]
  (str/join "\n" (map (partial str/join " ")
                      (map (fn [m] (map format-elem m))
                           (zaub/get-rows (pad-board col-size board))))))

(defmulti handle-cmd (fn [cmd game-info & args] (keyword cmd)))

(defmethod handle-cmd :default [cmd game-info & _]
  (println "Unknown command:" cmd)
  (print-help opts-map)
  game-info)

(defmethod handle-cmd :help [cmd game-info & _]
  (print-help opts-map)
  game-info)

(defmethod handle-cmd :push-unit [cmd game-info col-n-str unit-id]
  (let [col-n (parse-int col-n-str)
        {board :board} game-info]
    (assoc game-info
           :board
           (zaub/push-into-nth board
                               col-n
                               unit-id))))

(defmethod handle-cmd :pop-unit [cmd game-info col-n-str]
  (let [col-n (parse-int col-n-str)
        {board :board} game-info]
    (assoc game-info
           :board
           (zaub/pop-from-nth board
                              col-n))))

(defmethod handle-cmd :print-board [cmd game-info]
  (println (board-str game-info))
  game-info)

(defmethod handle-cmd :quit [_ _]
  nil)

(defmethod handle-cmd :new [cmd game-info]
  {:board (zaub/create-empty-board 6)
   :col-size 5})

(defn handle [game-info line]
  (when (some? line)
    (let [tokens (str/split line #" ")]
      (apply handle-cmd
             (first tokens)
             game-info
             (rest tokens)))))

(defn -main [& args]
  (println "Welcome to Zaub!")
  (println "Type help to view commands")
  (print "> ")
  (flush)
  (loop [game-info {:board (zaub/create-empty-board 6)
                    :col-size 5}
         lines (line-seq (BufferedReader. *in*))]
    (if (some? game-info)
      (recur (handle game-info
                     (first lines))
             (do (print "> ")
                 (flush)
                 (rest lines)))
      "Bye")))
