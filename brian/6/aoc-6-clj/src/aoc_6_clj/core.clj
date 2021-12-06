(ns aoc-6-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

(defn simulate-day-prob1
  [list-of-🐟]
  (reduce
   (fn [acc 🐟]
     (if (zero? 🐟)
       (cons 8 (cons 6 acc))
       (cons (dec 🐟) acc)))
   '()
   list-of-🐟))

(defn parse-input
  [f]
  (-> f
      slurp
      st/trim-newline
      (st/split #",")
      (->> (map #(Integer/parseInt %)))))

(comment
  ((simulate-day-prob1 (simulate-day-prob1 '(3 4 3 1 2))))

  (count (nth (iterate simulate-day-prob1 '(3 4 3 1 2)) 18))
  ;;26
  (count (nth (iterate simulate-day-prob1 '(3 4 3 1 2)) 80))
  ;;5934


  (parse-input "../input.txt")
  (parse-input "../input.txt")

  (time (count (nth (iterate simulate-day (parse-input "../input.txt")) 80)))
  ;;379114 - the first answer

  ;; (count (nth (iterate simulate-day '(3 4 3 1 2)) 256))
  ;;Nooooope, not gonna happen


  ;;fish are always in range 0 - 8
  ;;so keeping a whole list of each fish is unnecessary
  ;;we can store number of fish of each type in a vector
  [0 1 2 3 4 5 6 7 8]
  )

(defn simulate-day-prob2
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

(defn count-🐟
  [🐟🐟🐟]
  (reduce + 🐟🐟🐟))

(defn input-list->🐟-vec
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

(comment
  ;;3 4 3 1 2 - one 1, one 2, two 3's, one 4
  [0 1 1 2 1 0 0 0 0]
  ;;should generate
  ;;2 3 2 0 1 - one 0, one 1, two 2s, one 3
  [1 1 2 1 0 0 0 0 0]
  (simulate-day-prob2 [0 1 1 2 1 0 0 0 0])
  ;;[1 1 2 1 0 0 0 0 0] tada

  ;;another day should generate one 0, two 1s, one 2, one 6, one 8
  ;;or [1 2 1 0 0 0 1 0 1]
  (simulate-day-prob2 [1 1 2 1 0 0 0 0 0])

  (count-🐟 (nth (iterate simulate-day-prob2 [0 1 1 2 1 0 0 0 0]) 18))
  ;;26

  (count-🐟 (nth (iterate simulate-day-prob2 [0 1 1 2 1 0 0 0 0]) 80))
  ;;5934

  (count-🐟 (nth (iterate simulate-day-prob2 [0 1 1 2 1 0 0 0 0]) 256))
  ;;26984457539


  (count-🐟 (nth (iterate simulate-day-prob2
                          (input-list->🐟-vec (parse-input "../input.txt")))
                 256))
  1702631502303
  )
