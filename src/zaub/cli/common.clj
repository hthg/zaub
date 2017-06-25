(ns zaub.cli.common
  (:require [clojure.string :as str]
            [zaub.core.board :as zboard]))

(defn parse-int [n]
  (Integer/parseInt n))

(defn- format-unit [unit]
  (format "%s" (if-some [id (:tid unit)]
                 (if (:active unit)
                   (str/upper-case id)
                   id)
                 ".")))

(defn pad-col [min-size col]
  (reverse (take min-size (concat col (repeat nil)))))

(defn pad-board [col-size board]
  (map (partial pad-col col-size) board))

(defn board-str [{:keys [col-size board]}]
  (str/join "\n" (map (partial str/join " ")
                      (map (fn [m] (map format-unit m))
                           (zboard/get-rows (pad-board col-size board))))))
