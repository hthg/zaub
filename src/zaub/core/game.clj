(ns zaub.core.game
   (:require [zaub.core.board :as zboard]
             [zaub.core.unit :as zunit])) 

(def attrs '(:board :col-size :hp :next-unit))

(defn game [& args]
  (zipmap attrs args))

(defn alter-board [game col-func]
  (assoc game :board #(mapv col-func (:board game))))
