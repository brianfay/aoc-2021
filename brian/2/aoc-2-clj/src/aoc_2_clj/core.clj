(ns aoc-2-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

(defn parse-input [f]
  (->> f
       io/file
       io/reader
       line-seq
       (map #(st/split % #" "))))


(defn calculate-offset
  "Takes a sequence of direction/value pairs (like [\"forward\" \"3\"])
  Returns the calculated offset [x y] from running through all these pairs (assumes that starting position was [0 0])"
  [direction-val-pairs]
  (reduce (fn [[x y] [direction value]]
           (let [value (Integer/parseInt value)]
             (case direction
               "forward" [(+ x value) y]
               "down"    [x (+ y value)]
               "up"      [x (- y value)])))
          [0 0]
          direction-val-pairs))

(defn calculate-offset-with-aim
  "Takes a sequence of direction/value pairs (like [\"forward\" \"3\"])
  Returns the calculated offset [x y] from running through all these pairs (assumes that starting position was [0 0])
  This one considers the aim factor in problem 2"
  [direction-val-pairs]
  (drop-last (reduce (fn [[x y aim] [direction value]]
                       (let [value (Integer/parseInt value)]
                         (case direction
                           "forward" [(+ x value) (+ y (* aim value)) aim]
                           "down"    [x y (+ aim value)]
                           "up"      [x y (- aim value)])))
                     [0 0 0]
                     direction-val-pairs)))

(defn solve-problem-1 []
  (println "horizontal position * depth: "
           (->> "../input.txt"
                parse-input
                calculate-offset
                (apply *))))

(defn solve-problem-2 []
  (println "horizontal position * depth accounting for aim: "
           (->> "../input.txt"
                parse-input
                calculate-offset-with-aim
                (apply *))))

(defn -main []
  (solve-problem-1)
  (solve-problem-2))
