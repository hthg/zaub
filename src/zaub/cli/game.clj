(ns zaub.cli.game
  (:require [zaub.core.board :as zboard]
            [zaub.core.unit :as zunit]
            [zaub.core.game :as zgame]
            [zaub.cli.common :as cli-common]
            [clojure.string :as str])
  (:import [java.io BufferedReader]
           [java.util UUID]))

(def unit-types '("a" "b" "c"))

(defn- make-unit [id]
  (zunit/unit id false 3 2 (.toString (UUID/randomUUID))))

(defn- random-units [unit-types n-units]
  (take n-units (random-sample 0.1 (cycle unit-types))))

(defn- random-unit [unit-types]
  (first (random-units unit-types 1)))

(defn- create-randomized-board []
  (zboard/create-randomized-board 6 (random-units unit-types 4) make-unit))

(defn- init-game []
  (zgame/game (create-randomized-board) 5 10 (random-unit unit-types)))

(defn- do-ticks [game]
  (update game :board #(mapv zunit/tick-col %)))

(defn- in-coll?
  ([elem coll]
   (some #(= elem %) coll))
  ([elem coll comp-fn]
   (some #(comp-fn elem %) coll)))

(defn- filter-col [col to-exclude]
  (into zboard/queue
        (filter #(not (in-coll? % to-exclude zunit/equals?)) col)))

(defn- do-attacks [game]
  (let [{:keys [:board :hp]} game
        atkr-cols (map (partial take-while zunit/ready-to-atk?) board)
        dmg-cols (map #(reduce + (map :atk %)) atkr-cols)
        dmg-total (reduce + dmg-cols)
        new-brd (map filter-col board atkr-cols)]
    (assoc game
           :board new-brd
           :hp (- hp dmg-total))))

(defn- prompt []
  (print "> ")
  (flush))

(defn- do-push [game col-num unit]
  (update game :board #(zboard/push-into-nth % col-num unit)))

(defn- do-take [game col-num]
  (update game :board #(zboard/pop-from-nth % col-num)))

(defn- do-user-cmd [game]
  (println (str "HP: " (:hp game)))
  (println (str "Next unit: " (:next-unit game)))
  (println (cli-common/board-str game))
  (println (apply str (take (dec (* 2 (count (:board game))))
                            (cycle "-"))))
  (prompt)
  (let [{:keys [:board :col-size]} game]
    (when-some [line (first (line-seq (BufferedReader. *in*)))]
      (let [toks (str/split line #" ")
            cmd (first toks)
            args (rest toks)]
        (when-not (= cmd "q")
          (case cmd
            "push" (do-push game
                            (cli-common/parse-int (first args))
                            (make-unit (:next-unit game)))
            "take" (do-take game (cli-common/parse-int (first args)))
            (do (println "push COL-N")
                (println "take COL-N")
                (recur game))))))))

(defn- do-activate [game]
  (update game :board #(mapv zunit/activate-col %)))

(defn- do-turn [game]
  (if (< 0 (:hp game))
    (assoc ((comp do-activate
                  do-ticks
                  do-attacks)
            (do-user-cmd game))
           :next-unit
           (random-unit unit-types))
    (println "You win!")))

(defn start []
  (println "Welcome to Zaub!")
  (println "Type help to view commands")
  (loop [game (init-game)]
    (if (some? game)
      (recur (do-turn game))
      "Bye")))
