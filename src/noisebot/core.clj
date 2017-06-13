(ns noisebot.core
  (:gen-class))

(require '[irclj.core :as irc])
(require '[clojure.string :as str])

(def mynick "noisebot")
(def channel "#noisebridge")

(def connection (atom nil))

;; By default, logs go to stdout; use this to quiet them
(defn eat-log [& args]
  (comment pprint args))

(defn message!
  ([msg]           (irc/message @connection channel msg))
  ([dest-chan msg] (irc/message @connection dest-chan msg)))

(defn disconnect! []
  (when @connection
    (swap! connection irc/kill)))

(defn monitor-urls-irc
  [target nick urls]
  ;; If user PM'd this bot this request, repond to that user in a PM,
  ;; otherwise respond to the channel the request came from (target).
  (when (not (empty? urls))
    (let [reply-to (if (= target mynick) nick target)]
      (println "monitor-urls-irc: About to start monitoring" urls
               "then reporting failed attempts to" reply-to)
      (message! reply-to
                (str "Now fake-monitoring: " (str/join ", " urls))))))

(defn irc-handle-privmsg
  "Parses the incoming IRC message, passes these values to per-feature handlers"
  [irc type & args]
  (let [{:keys [text target nick command]} type]
    (if (and (= command "PRIVMSG") (= \! (first text)))
      (let [cmd-params (remove str/blank? (str/split text #"\s"))]
        (when (not (empty? cmd-params))
          (let [cmd (subs (first cmd-params) 1)
                params (rest cmd-params)]
            (case cmd
              ("mon" "monitor") (monitor-urls-irc target nick params)
              "default"))))
      (println "irc-handle-privmsg not handling message" command "with text" text))))

(def callbacks {:raw-log eat-log
                :privmsg irc-handle-privmsg})

(defn connect! []
  (disconnect!)
  (reset! connection (irc/connect "irc.freenode.net"
                                  6667
                                  mynick
                                  :callbacks callbacks))
  (irc/join @connection channel))


(defn -main
  [& args]
  (connect!))
