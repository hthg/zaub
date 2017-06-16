(ns zaub.core.unit
   (:require [zaub.core.board :as zboard]))

(def attrs '(:tid :active :ttr :atk))

(defn unit [id & args]
  (zipmap attrs (cons id args)))

(defn- activate [unit]
  (assoc unit :active true))

(defn- select-for-activation [col]
  (let [id (:tid (first col))
        units (take-while #(and (= id (:tid %))
                                (not (:active %)))
                          col)]
    (if (<= 3 (count units)) units)))

(defn activate-col [col]
  (let [activated (map #(assoc % :active true)
                       (select-for-activation col))]
    (into core/queue (concat activated
                        (nthrest col (count activated))))))

(defn- tick-unit [u]
  (if (:active u)
    (assoc u :ttr (dec (:ttr u)))
    u))

(defn tick-col [col]
  (map tick-unit col))

(defn is-ready [u]
  (and (:active u)
       (= 0 (:ttf u))))
