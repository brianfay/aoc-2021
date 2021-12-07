(ns aoc-7-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

(comment
  (def sample-input [16 1 2 0 4 2 7 1 2 14])


  (apply min
         (map (fn [a]
                (reduce
                 +
                 (for [i sample-input]
                   (Math/abs (- a i)))))
              (range (inc (apply max sample-input)))))
  ;;37

  (def input
    (-> "input.txt"
        slurp
        (st/trim-newline)
        (st/split #",")
        (->> (map #(Integer/parseInt %)))))


  (time
   (apply
    min
    (map (fn [a]
           (reduce
            +
            (for [i input]
              (Math/abs (- a i)))))
         (range (apply max input)))))
  ;;344605 - correct answer but takes a gnarly 5 seconds

  (time
   (apply
    min
    (map (fn [a]
           (reduce
            +
            (for [i input]
              (Math/abs (- a i)))))
         (range (apply max input)))))

  (+ 1)              ;1
  (+ 1 2)            ;3
  (+ 1 2 3)          ;6
  (+ 1 2 3 4)        ;10
  (+ 1 2 3 4 5)      ;15
  (+ 1 2 3 4 5 6)    ;21
  (+ 1 2 3 4 5 6 7)  ;28
  ;;I googled these numbers and they're "triangular numbers""
  ;;maybe I can memoize these?

  ;;actually I see a formula
  ;;this is kinda sorta cheating... but eff it I'm not Friedrich Gauss
  (defn triangular-number
    [n]
    (* n (inc n) 0.5))
  (triangular-number 1)
  (triangular-number 2)
  (triangular-number 3)

  (time
   (apply
    min
    (map (fn [a]
           (reduce
            +
            (for [i input]
              (int (triangular-number (Math/abs (- a i)))))))
         (range (apply max input)))))
  )
