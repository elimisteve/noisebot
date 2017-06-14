(ns noisebot.core
  (:gen-class))

(require '[irclj.core :as irc])
(require '[clojure.string :as string])

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
                (str "Now fake-monitoring: " (string/join ", " urls))))))

(defn parse-irc-cmd
  [text]
  (let [cmd-args (remove string/blank? (string/split text #"\s"))]
    (if (and (not (empty? cmd-args))
             (= \! (first text)))
      [(subs (first cmd-args) 1)  ; command without leading \!
       (rest cmd-args)]           ; args
      ["" '()])))

(defn irc-handle-privmsg
  "Parses the incoming IRC message, passes these values to per-feature handlers"
  [irc type]
  (let [{:keys [command target nick text]} type]
    (if (= command "PRIVMSG")
      (let [[cmd args] (parse-irc-cmd text)]
        (case cmd
          "" (printf "irc-handle-privmsg not handling non-command `%s`\n" text)
          ("mon" "monitor") (monitor-urls-irc target nick args)
          ;; Add new bot commands here
          "default"))
      (println "irc-handle-privmsg not handling non-PRIVMSG message" command text))))

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
