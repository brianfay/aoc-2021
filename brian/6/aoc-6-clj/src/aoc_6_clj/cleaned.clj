(ns aoc-6-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

(defn input-list->🐟🐟🐟
  [🐟-list]
  (let [freaky-🐟 (frequencies 🐟-list)]
    [(or (get freaky-🐟 0) 0)
     (or (get freaky-🐟 1) 0)
     (or (get freaky-🐟 2) 0)
     (or (get freaky-🐟 3) 0)
     (or (get freaky-🐟 4) 0)
     (or (get freaky-🐟 5) 0)
     (or (get freaky-🐟 6) 0)
     (or (get freaky-🐟 7) 0)
     (or (get freaky-🐟 8) 0)]))

(defn parse-input
  [f]
  (-> f
      slurp
      st/trim-newline
      (st/split #",")
      (->> (map #(Integer/parseInt %)))
      input-list->🐟🐟🐟))

(defn simulate-day
  [[🐟0 🐟1 🐟2 🐟3 🐟4 🐟5 🐟6 🐟7 🐟8]]
  [🐟1
   🐟2
   🐟3
   🐟4
   🐟5
   🐟6
   (+ 🐟0 🐟7)
   🐟8
   🐟0])

(defn count-🐟🐟🐟
  [🐟🐟🐟]
  (reduce + 🐟🐟🐟))

(defn simulate-days
  [🐟🐟🐟 num-days]
  (nth (iterate simulate-day 🐟🐟🐟) 256))

(comment
  (-> "../input.txt"
      parse-input
      (simulate-days 256)
      count-🐟🐟🐟)
  )
