(ns zaub.cli
  (:require [zaub.core :as zaub]
            [clojure.string :as str])
  (:import [java.io BufferedReader]
           [zaub.core Game]))

(def unit-str {0 "s"
               1 "m"})

(def unit-id {"s" zaub.core/soldier
              "m" zaub.core/mage})

(defn opt-str [opt]
  (str (str/join "|" (:cmd opt))
       " : "
       (:descr opt)))

(defn print-help [opts]
  (println (str/join "\n"
                     (into ["Commands" "--------"]
                           (map (partial str "  ")
                                (map opt-str (vals opts)))))))

(defn coll-str [coll]
  (str/join " " (map (fn [x] (if (nil? x) "_" (get unit-str (:id x))))
                     coll)))

(defn board-row-strs [brd]
  (map coll-str
       (zaub/get-rows (zaub/pad-cols brd 5))))

(defn print-board [game]
  (println (str/join "\n" (board-row-strs (:board game)))))

(defn insert-unit [game col-n-str unit]
  (assoc game :board (zaub/insert-unit (:board game)
                                       (Integer/parseInt col-n-str)
                                       (zaub/make-unit (get unit-id unit)))))

(defn remove-unit [game col-n-str]
  (assoc game :board (zaub/remove-unit (:board game)
                                       (Integer/parseInt col-n-str))))

(def opts {:help {:cmd '("h" "help" "?")
                  :descr "print help"
                  :func #(do (print-help opts) %)}
           :unit {:cmd '("un" "unit")
                  :descr "insert unit"
                  :func #(insert-unit %1 %2 %3)}
           :remove-unit {:cmd '("rm" "remove-unit")
                         :descr "remove unit"
                         :func #(remove-unit %1 %2)}
           :print-board {:cmd '("pb" "print-board")
                         :descr "print game board"
                         :func #(do (print-board %) %)}
           :quit {:cmd '("q" "quit" nil)
                  :descr "quit"
                  :func (fn [_] nil)}
           :new {:cmd '("n" "new")
                 :descr "new game"
                 :func (fn [_] (zaub/create-new-game 6 5))}})

(defn handle [game cmd-in]
  (if (nil? cmd-in)
   nil
   (let [toks (str/split cmd-in #" ")
         cmd (first toks)
         opt (first (filter (fn [opt] (some #(= cmd %) (:cmd opt)))
                            (vals opts)))
         func (:func opt)]
     (if (nil? func)
       (do (println (str "No func for " cmd))
           game)
       (apply (:func opt) game (rest toks))))))

(defn -main [& args]
  (loop [game (zaub/create-new-game 6 5)
         lines (line-seq (BufferedReader. *in*))]
    (when (not (nil? game))
      (recur (handle game (first lines)) (rest lines))))
  (println "Bye"))
