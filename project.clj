(defproject noisebot "0.1.0-SNAPSHOT"
  :description ""
  :url "https://github.com/elimisteve/noisebot"
  :license {:name "AGPLv3"
            :url "https://www.gnu.org/licenses/agpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [irclj "0.5.0-alpha4"]]
  :main ^:skip-aot noisebot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
