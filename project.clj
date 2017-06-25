(defproject zaub "0.1.0-SNAPSHOT"
  :description "Zaub, the game."
  :url "https://github.com/hthg/zaub"
  :license {:name "GNU General Public License v3"
            :url "http://fsf.org/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 ]
  :main ^:skip-aot zaub.cli.game
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
