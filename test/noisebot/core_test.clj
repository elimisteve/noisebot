(ns noisebot.core-test
  (:require [clojure.test :refer :all]
            [noisebot.core :refer :all]))

(deftest irc-tests
  (let [empty-list '()
        noncmd ["" empty-list]]
    (testing "Non-commands"
      (is (= noncmd (parse-irc-cmd "")))
      (is (= noncmd (parse-irc-cmd "!")))
      (is (= noncmd (parse-irc-cmd " !not at start")))
      (is (= ["mon" '("google.com")] (parse-irc-cmd "!mon google.com"))))
    (testing "Legit-formatted commands")
      (is (= ["mon" '("google.com")] (parse-irc-cmd "!mon   google.com   ")))
      (is (= ["monitor" '("google.com")] (parse-irc-cmd "!monitor \t google.com  \t ")))
      (is (= ["1" '("2" "3")] (parse-irc-cmd "!1 2 3")))))
