(ns aoc-4-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset]))

(defn get-moves
  [lines]
  (map #(Integer/parseInt %)
       (st/split (first lines) #",")))

(defn get-boards
  [lines]
  (let [boards (->> (drop 1 lines)
                    (remove st/blank?)
                    (partition 5))]
    (partition
     25
     (apply concat
            (for [b boards
                  row b]
              (-> row
                  (st/split #" ")
                  (->> (remove st/blank?)
                       (map #(Integer/parseInt %)))))))))

(defn parse-input [f]
  (let [lines (->> f
                   io/file
                   io/reader
                   line-seq)]
    {:moves (get-moves lines)
     :boards (get-boards lines)}))

(defn check-horizontals [board moves]
  (some identity
        (for [row (partition 5 board)]
          (cset/subset? (set row) (set moves)))))

(defn check-verticals [board moves]
  (some identity
        (for [i (range 5)]
          (let [row (take-nth 5 (drop i board))]
            (cset/subset? (set row) (set moves))))))

(defn check-board [board moves]
  (or
   (check-horizontals board moves)
   (check-verticals board moves)))

(defn winning-boards [boards moves]
  (->> (for [board boards]
         (some identity
               (map #(when (check-board board (take % moves))
                       {:board board :num-moves %})
                    (map inc (range (count moves))))))
       (filter identity)
       (sort-by :num-moves)))

(defn winning-board [boards moves]
  (first (winning-boards boards moves)))

(defn last-winning-board [boards moves]
  (last (winning-boards boards moves)))

(defn sum-unmarked-numbers
  [board marked-numbers]
  (reduce + (remove
             (set marked-numbers)
             board)))

(defn score
  [board marked-numbers]
  (* (sum-unmarked-numbers board marked-numbers)
     (last marked-numbers)))

(defn -main
  [f]
  (let [{:keys [moves boards]} (parse-input f)
        {:keys [board num-moves]} (winning-board boards moves)]
    (println "The score of the winning board is: " (score board (take num-moves moves))))
  (let [{:keys [moves boards]} (parse-input f)
        {:keys [board num-moves]} (last-winning-board boards moves)]
    (println "The score of the last winning board is: " (score board (take num-moves moves)))))

(comment
  (-main "../sample-input.txt")
  (-main "../input.txt")

  ;;Checking my results against Ben's program's results because I don't know why this is breaking
  ;;It's not cheating... it's just... okay maybe it's slightly cheating
  (-main "../../../ben/day4/input"))

(comment
  ;;Not thoroughly testing this upfront caused me a lot of pain
  ;;First it turned out I was only checking the first vertical column
  ;;Then after I "fixed" it enough to solve the first problem
  ;;it turned out I was creating a horrifying ghost column out of
  ;;just the last 4 rows of the first column
  ;;which made it look like there were multiple boards tied for last place
  (def fake-board [1  2  3  4  5
                   6  7  8  9  10
                   11 12 13 14 15
                   16 17 18 19 20
                   21 22 23 24 25])

  (check-horizontals [1  2  3  4  5
                      6  7  8  9  10
                      11 12 13 14 15
                      16 17 18 19 20
                      21 22 23 24 25]
                     [1 2 3 4 5])

  (check-horizontals [1  2  3  4  5
                      6  7  8  9  10
                      11 12 13 14 15
                      16 17 18 19 20
                      21 22 23 24 25]
                     [21 22 23 24 25])

  (check-horizontals [1  2  3  4  5
                      6  7  8  9  10
                      11 12 13 14 15
                      16 17 18 19 20
                      21 22 23 24 25]
                     [26 27 28 29 30])

  (check-verticals  [1  2  3  4  5
                     6  7  8  9  10
                     11 12 13 14 15
                     16 17 18 19 20
                     21 22 23 24 25]
                    [1 6 11 16 21])

  (check-verticals  [1  2  3  4  5
                     6  7  8  9  10
                     11 12 13 14 15
                     16 17 18 19 20
                     21 22 23 24 25]
                    [5 10 15 20 25])

  (check-verticals  [1  2  3  4  5
                     6  7  8  9  10
                     11 12 13 14 15
                     16 17 18 19 20
                     21 22 23 24 25]
                    [6 11 16 21 26])
  (check-board [1  2  3  4  5
                6  7  8  9  10
                11 12 13 14 15
                16 17 18 19 20
                21 22 23 24 25]
                [6 11 16 21 26])
  )
