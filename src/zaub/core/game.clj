(ns zaub.core.game
   (:require [zaub.core.board :as zboard]
             [zaub.core.unit :as zunit])) 

(def attrs '(:board))

(defn game [brd & args]
  (zipmap attrs (cons brd args)))

(defn do-attacks [game]
  (let [per-col (map (filter is-ready) (:board game))])
  )
