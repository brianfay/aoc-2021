(ns aoc-14-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference union]]))

(def example-replacements
  {"CH" "B"
   "HH" "N"
   "CB" "H"
   "NH" "C"
   "HB" "C"
   "HC" "B"
   "HN" "C"
   "NN" "C"
   "BH" "H"
   "NC" "B"
   "NB" "B"
   "BN" "B"
   "BB" "N"
   "BC" "B"
   "CC" "N"
   "CN" "C"})

(defn replace-pair
  [pair replacements]
  (if-let [replacement (replacements pair)]
    (str replacement (second pair))
    pair))

(defn replace-pairs
  [pairs replacements]
  (apply str (ffirst pairs) (map #(replace-pair % replacements) pairs)))

(defn get-pairs
  [s]
  (loop [s (seq s)
         pairs []]
    (let [pair (apply str (take 2 s))]
      (if (= 2 (count pair))
        (recur (rest s) (conj pairs pair))
        pairs))))

(def input
  (let [[raw-input-string raw-rules]
        (->> "input.txt"
             io/file
             io/reader
             line-seq
             (split-with #(not= % "")))]
    {:input-string (first raw-input-string)
     :replacements (->> raw-rules
                        (drop 1)
                        (mapv #(st/split % #" -> "))
                        (into {}))}))

(defn problem-1-answer
  []
  (let [{:keys [input-string replacements]} input]
    (->> (nth (iterate #(replace-pairs (get-pairs %) replacements) input-string) 10)
         frequencies
         (sort-by val)
         ((juxt last first))
         (map second)
         (apply -))))

(defn example-answer
  []
  (->> (nth (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB") 10)
       frequencies
       (sort-by val)
       ((juxt last first))
       (map second)
       (apply -)))

(defn problem-2-answer
  []
  (let [{:keys [input-string replacements]} input]
    (->> (nth (iterate #(replace-pairs (get-pairs %) replacements) input-string) 40)
         frequencies
         (sort-by val)
         ((juxt last first))
         (map second)
         (apply -))))

(comment
  (count (:replacements input))

  (apply str (map #(replace-pair % example-replacements) (get-pairs "NNCB")))
  (get-pairs "NNCB")
  (take 2 (drop 1 (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB")))
  (map (comp count get-pairs) (take 6 (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB")))
  (nth (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB") 3)
  (count (nth (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB") 5))
  (count (nth (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB") 10))
  (sort-by val (frequencies (nth (iterate #(replace-pairs (get-pairs %) example-replacements) "NNCB") 10)))

  (replace-pairs (get-pairs "NNCB") example-replacements)
  (replace-pairs (get-pairs "OFS") (:replacements input))
  (replace-pair "FS" (:replacements input))

  (get-pairs "OFSNKKHCBSNKBKFFCVNB")
  (replace-pairs (get-pairs "OFSNKKHCBSNKBKFFCVNB") (:replacements input))
  (replace-pair "OF" (:replacements input))


  (problem-1-answer)
  ;;2768

  (example-answer)
  ;;but I get the correct example answer :( 1588
  (problem-2-answer)

  ;;My friend gave me a hint (full result would be petabytes, must compress somehow)
  )

;;NCHB - four letters, 4^2 possible combinations, and we have 16 replacement rules (all are covered)
;;
;; input has 10 unique characters, 10^2 possible replacements (all are provided)


;;NNCB
;;NN becomes NCN, NC becomes NBC, CB becomes CHB
;;[NN NC CB]
;;NCNBCHB
;;[NC CN NB BC CH HB]
;;number of pairs multiplied by two each iteration

;;this is a tree, right?
;;  NN    NC     CB
;;NC CN  NB BC  CH HB
;;I don't want to keep the whole thing in memory
;;but I don't need to, because I only need the count of each letter
;;instead of calculating a full generation at a time, I can do a DFS traversal of this tree
;;want this to be post-order (thanks wikipedia for the refresher)

(defn next-gen-pairs
  [pair replacement]
  (let [new-char (replacement pair)]
    [(str (first pair) new-char) (str new-char (second pair))]))

(defn traverse
  ([pairs replacement depth]
   (traverse pairs replacement depth {(last (last pairs)) 1}))
  ([pairs replacement depth counts]
   (if (pos? depth)
     ;;keep recursively visiting subtree (post-order)
     (reduce
      (fn [counts pair]
        (traverse (next-gen-pairs pair replacement) replacement (dec depth) counts))
      counts
      pairs)
     ;;if we hit the bottom of the tree, count the first char of each pair
     (reduce
      (fn [counts pair]
        (update counts (first pair) #(if % (inc %) 1)))
      counts
      pairs))))

(defn problem-1-answer-improved
  []
  (let [{:keys [input-string replacements]} input]
    (->> (traverse (get-pairs input-string) replacements 10)
         (sort-by val)
         ((juxt last first))
         (map second)
         (apply -))))

(defn problem-2-answer
  []
  (let [{:keys [input-string replacements]} input]
    (->> (traverse (get-pairs input-string) replacements 40)
         (sort-by val)
         ((juxt last first))
         (map second)
         (apply -))))
;;SLOOOOOOOOOOWWWWWWWWWW, has been running for like 18 hours and no result


;;cheated and looked at reddit for inspiration
(defn count-smarter [input-string replacements n]
  (let [;{:keys [input-string replacements]} input
        initial-pairs (get-pairs input-string)
        replacement-pairs (zipmap (keys replacements)
                                  (map #(next-gen-pairs % replacements) (keys replacements)))
        pair-frequencies (nth (iterate
                               (fn [counts]
                                 (reduce
                                  (fn [counts [pair n]]
                                    (let [[r1 r2] (replacement-pairs pair)]
                                      counts
                                      (-> counts
                                          (update pair - n)
                                          (update r1 (fnil + 0) n)
                                          (update r2 (fnil + 0) n)
                                          )))
                                  counts
                                  counts))
                               (frequencies initial-pairs))
                              n)]
    pair-frequencies
    (reduce (fn [letter-frequencies [pair n]]
              (-> letter-frequencies
                  (update (first pair) (fnil + 0 0) n)))
            {(last (last initial-pairs)) 1}
            pair-frequencies)))

;;NCNBCHB
;;NB BC 
(get-pairs "NCNBCHB")

;;NCNBCHB
;; {"NC" 1
;;  "CN" 1
;;  "NB" 1
;;  "BC" 1
;;  "CH" 1
;;  "HB" 1}

;;NBCCNBBBCBHCB
{"NB" 2
 "BC" 2
 "CC" 1
 "CN" 1
 "BB" 2
 "BH" 1
 "HC" 1
 "CB" 1}

(count-smarter "NNCB" example-replacements 1)
{"CH" ["CB" "BH"],
 "HH" ["HN" "NH"],
 "BH" ["BH" "HH"],
 "BN" ["BB" "BN"],
 "NH" ["NC" "CH"],
 "NB" ["NB" "BB"],
 "HB" ["HC" "CB"],
 "BC" ["BB" "BC"],
 "CN" ["CC" "CN"],
 "CC" ["CN" "NC"],
 "BB" ["BN" "NB"],
 "CB" ["CH" "HB"],
 "HN" ["HC" "CN"],
 "HC" ["HB" "BC"],
 "NC" ["NB" "BC"],
 "NN" ["NC" "CN"]}
(comment
  (count-smarter "NNCB" example-replacements 1)
  {"NN" 1, "NC" 1, "CB" 1}

  (count-smarter "NNCB" example-replacements 1)
  {"CH" 1, "HH" 0, "BH" 0, "BN" 0, "NH" 0, "NB" 1, "HB" 1, "BC" 1, "CN" 1, "CC" 0, "BB" 0, "CB" 0, "HN" 0, "HC" 0, "NC" 1, "NN" 0}
  ;;CH NB HB BC CN NC

  (->> (count-smarter "NNCB" example-replacements 10)
      (sort-by val)
      ((juxt last first))
      (map second)
      (apply -))


  ;;problem 1 answer
  (->> (count-smarter (:input-string input) (:replacements input) 10)
       (sort-by val)
       ((juxt last first))
       (map second)
       (apply -))

  ;;problem 2 answer
  (->> (count-smarter (:input-string input) (:replacements input) 40)
       (sort-by val)
       ((juxt last first))
       (map second)
       (apply -))

  (next-gen-pairs "NN" example-replacements)
  (get-next-gen-pairs "NC" example-replacements)

  (traverse (get-pairs "NNCB") example-replacements 0)

  (problem-1-answer-improved)
  (time (def p2-answer (problem-2-answer)))

  ;;NNCB
  ;;- NN, + NC CN
  ;;- NC, + NB, BC
  ;;NCNBCHB

  ;;how do I count characters in a pair?
  ;;(get-pairs "NCNBCHB")
  ;;["NC" "CN" "NB" "BC" "CH" "HB"] 
  ;NCNBCHB
  ;;first character of each pair, plus last character of last pair

  ;;last character of last generation should always be the end character of first generation... this should work
  )
