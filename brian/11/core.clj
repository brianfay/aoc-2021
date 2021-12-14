(ns aoc-11-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference union]]))

(def sample-input
  [5 4 8 3 1 4 3 2 2 3
   2 7 4 5 8 5 4 7 1 1
   5 2 6 4 5 5 6 1 7 3
   6 1 4 1 3 3 6 1 4 6
   6 3 5 7 3 8 5 4 7 8
   4 1 6 7 5 2 4 6 4 5
   2 1 7 6 8 4 1 7 2 1
   6 8 8 2 8 8 1 1 3 4
   4 8 4 6 8 4 8 5 5 4
   5 2 8 3 7 5 1 5 2 6])

(def sample-2
  [1 1 1 1 1
   1 9 9 9 1
   1 9 1 9 1
   1 9 9 9 1
   1 1 1 1 1])

(def input (-> "input.txt"
               io/file
               io/reader
               line-seq
               (->> (mapcat (fn [s] (map #(Integer/parseInt (str %)) (seq s)))))
               vec)))

(defn print-output
  [table rowsize]
  (println "")
  (doseq [l (partition rowsize table)]
    (println l)))

(defn inc-at-point
  [table rowsize x y]
  (if (and (<= 0 x (dec rowsize))
           (<= 0 y (dec rowsize)))
    (update table (+ x (* y rowsize)) inc)
    table))

(defn set-at-point
  [table rowsize x y v]
  (if (and (<= 0 x rowsize)
           (<= 0 y rowsize))
    (assoc table (+ x (* y rowsize)) v)
    table))

(defn flash-at-point
  [table rowsize x y]
  (reduce (fn [table [x y]]
            (inc-at-point table rowsize x y))
          table
          [[(dec x) (dec y)] [x (dec y)]   [(inc x) (dec y)]
           [(dec x)      y]  #_[x      y]  [(inc x)      y]
           [(dec x) (inc y)] [x (inc y)]   [(inc x) (inc y)]]))

(defn has-tens?
  [table]
  (some #(= 10 %) table))

(defn get-tens-or-greater
  [table rowsize]
  (set
   (keep
    identity
    (for [y (range rowsize)
          x (range rowsize)]
      (when (<= 10 (get table (+ x (* y rowsize)))) [x y])))))

(defn flash-tens
  [table rowsize]
  (loop [table table
         already-flashed #{}]
    (let [potential-flashers (get-tens-or-greater table rowsize)
          flashers (difference potential-flashers already-flashed)]
      (if (empty? flashers)
        (reduce (fn [t [x y]]
                  (set-at-point t rowsize x y 0))
                table
                already-flashed)
        (recur (reduce
                  (fn [t [x y]]
                    (flash-at-point t rowsize x y))
                  table
                  flashers)
                 (union already-flashed flashers))))))

(defn run-step
  [table rowsize]
  (-> (mapv inc table)
      (flash-tens rowsize)))

(defn count-flashes
  [table rowsize num-steps]
  (->> (take (inc num-steps) (iterate #(run-step % rowsize) table))
       (mapcat #(filter zero? %))
       count))

(defn find-synchronous-flash
  [table rowsize]
  (count (take-while #(not (every? zero? %)) (iterate #(run-step % 10) table))))

(comment

  (has-tens? [0 1 2 3 4 10])
  (print-output 5 sample-2)
  (print-output 5 (mapv (comp #(mod % 10) inc) sample-2))

  (print-output 5 (mapv inc sample-2))

  (print-output (inc-at-point sample-2 5 1 4) 5)

  (print-output (flash-at-point sample-2 5 2 2) 5)

  (print-output
   (flash-tens [0 10 9
                0 9 9
                0 0 0] 3)
   3)

  (print-output
   (run-step [1 1 1 1 1
                1 9 9 9 1
                1 9 1 9 1
                1 9 9 9 1
                1 1 1 1 1]
               5)
   5)

  (print-output
   (run-step (run-step [1 1 1 1 1
                        1 9 9 9 1
                        1 9 1 9 1
                        1 9 9 9 1
                        1 1 1 1 1]
                       5) 5)
   5)

  (print-output
   (flash-tens [2 2 2 2 2
                2 10 10 10 2
                2 10 2 10 2
                2 10 10 10 2
                2 2 2 2 2]
               5)
   5)

  (run-step sample-input 10)
  (take 2 (iterate #(run-step % 10) sample-input))
  (count-flashes sample-input 10 2)
  (count-flashes sample-input 10 100)
  (count-flashes input 10 100)
  (find-synchronous-flash sample-input 10)
  (find-synchronous-flash input 10)
  )
