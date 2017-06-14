(ns noisebot.core-test
  (:require [clojure.test :refer :all]
            [noisebot.core :refer :all]))

(defmacro h-parse
  "h-parse == helper for testing parse-irc-cmd"
  [wanted text-in]
  `(is (= ~wanted (parse-irc-cmd ~text-in))))

(deftest irc-tests
  (let [noncmd ["" '()]]
    (testing "Non-commands"
      (h-parse noncmd "")
      (h-parse noncmd "!")
      (h-parse noncmd " !not at start")
    (testing "Legit-formatted commands")
      (h-parse ["mon" '("google.com")]     "!mon google.com")
      (h-parse ["mon" '("google.com")]     "!mon   google.com   ")
      (h-parse ["monitor" '("google.com")] "!monitor \t google.com  \t ")
      (h-parse ["1" '("2" "3")]            "!1 2 3"))))
