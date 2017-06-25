(ns zaub.core.unit
   (:require [zaub.core.board :as zboard]))

(def attrs '(:tid :active :tta :atk :uuid))

(defn unit [& args]
  (zipmap attrs args))

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
    (into zboard/queue (concat activated
                        (nthrest col (count activated))))))

(defn- tick-unit [u]
  (if (:active u)
    (update u :tta dec)
    u))

(defn tick-col [col]
  (into zboard/queue (map tick-unit col)))

(defn equals? [{uuid1 :uuid} {uuid2 :uuid}]
  (= uuid1 uuid2))

(defn ready-to-atk? [u]
  (and (:active u)
       (>= 0 (:tta u))))
