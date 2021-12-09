(ns aoc-7-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference intersection union]]))

(defn parse-line
  [l]
  (let [[signal-patterns output] (st/split l #"\|")]
    [(-> signal-patterns
         st/trim
         (st/split #" "))

     (-> output
         st/trim
         (st/split #" "))]))

(def input (-> "input.txt"
               io/file
               io/reader
               line-seq
               (->> (map parse-line))))

(reduce
 (fn [sum digit]
   (case (count digit)
     2 (inc sum)
     3 (inc sum)
     4 (inc sum)
     7 (inc sum)
     sum))
 0
 (mapcat second input))
;;416 - problem 1 answer

(defn pattern-code-mapping [pattern-codes]
  (let [mapping-1 (first (filter #(= 2 (count %)) pattern-codes))
        mapping-7 (first (filter #(= 3 (count %)) pattern-codes))
        mapping-4 (first (filter #(= 4 (count %)) pattern-codes))
        mapping-8 (first (filter #(= 7 (count %)) pattern-codes))
        mapping-0-6-9 (filter #(= 6 (count %)) pattern-codes)
        mapping-2-3-5 (filter #(= 5 (count %)) pattern-codes)
        a (difference mapping-7 mapping-1)
        bd (difference mapping-4 mapping-7)
        gbfa (apply intersection mapping-0-6-9)
        gda (apply intersection mapping-2-3-5)
        bf (difference gbfa gda)
        f (difference bf bd)
        d (difference bd bf)
        b (difference bd d)
        c (difference mapping-1 f)
        g (difference gda d a)
        e (difference mapping-8 a b c d f g)]
    {(union a b c e f g) 0
     (union c f) 1
     (union a c d e g) 2
     (union a c d f g) 3
     (union b c d f) 4
     (union a b d f g) 5
     (union a b d e f g) 6
     (union a c f) 7
     (union a b c d e f g) 8
     (union a b c d f g) 9}))

(reduce +
 (for [[pattern-codes output] input]
   (->> output
        (map #(->> %
                   set
                   (get (pattern-code-mapping (map set pattern-codes)))))
        (apply str)
        Integer/parseInt)))
;;1043697 - problem 2 answer
