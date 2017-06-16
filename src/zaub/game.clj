(ns zaub.game
   (:require [zaub.core :as zcore]
             [zaub.unit :as zunit])) 

(def attrs '(:board))

(defn game [brd & args]
  (zipmap attrs (cons brd args)))

(defn do-attacks [game]
  (let [per-col (map (filter is-ready) (:board game))])
  )
