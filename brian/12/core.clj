(ns aoc-12-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference union]]))

(defn adjacency-list
  [edges]
  (reduce (fn [m [e1 e2]]
            (-> m
                (update e1 set)
                (update e2 set)
                (update e1 conj e2)
                (update e2 conj e1)))
          {}
          edges))

(def example-caves
  [["start" "A"]
   ["start" "b"]
   ["A" "c"]
   ["A" "b"]
   ["b" "d"]
   ["A" "end"]
   ["b" "end"]])

(defn get-paths
  [edges]
  (let [vertices (set (apply concat edges))
        connections (adjacency-list edges)
        [large-caves small-caves] ((juxt #(set (get % true)) #(set (get % false)))
                                   (group-by #(Character/isUpperCase (first %))
                                             (disj vertices "start" "end")))]
    (loop [unfinished-paths (map (fn [e] ["start" e])
                                 (connections "start"))
           finished-paths nil]
      (if-let [[p & ps] (seq unfinished-paths)]
        (if (= "end" (last p))
          (recur ps (conj finished-paths p))
          (let [visited-small-caves (set (filter small-caves (set p)))
                last-node (last p)
                next-options (difference (disj (connections last-node) "start")
                                         visited-small-caves)
                new-paths (map (fn [e] (conj p e)) next-options)]
            (recur (concat ps new-paths) finished-paths)))
        finished-paths))))

(def input (->> "input.txt"
               io/file
               io/reader
               line-seq
               (mapv #(st/split % #"-"))))

(defn get-paths-2
  [edges]
  (let [vertices (set (apply concat edges))
        connections (adjacency-list edges)
        [large-caves small-caves] ((juxt #(set (get % true)) #(set (get % false)))
                                   (group-by #(Character/isUpperCase (first %))
                                             (disj vertices "start" "end")))]
    (loop [unfinished-paths (map (fn [e] ["start" e])
                                 (connections "start"))
           finished-paths nil]
      (if-let [[p & ps] (seq unfinished-paths)]
        (if (= "end" (last p))
          (recur ps (conj finished-paths p))
          (let [num-small-cave-visits (frequencies (filter small-caves p))
                visited-small-caves (if (some #(= 2 (val %)) num-small-cave-visits)
                                      (set (filter small-caves (set p)))
                                      #{})
                last-node (last p)
                next-options (difference (disj (connections last-node) "start")
                                         visited-small-caves)
                new-paths (map (fn [e] (conj p e)) next-options)]
            ;;concat causes a stack overflow here... it's too lazy for its own good
            (recur (reduce conj ps new-paths) finished-paths)))
        finished-paths))))

(comment
  ;;rank by how many steps away from end?
  (tree-seq {:a {:b 3}})
  (group-by val (frequencies ["a" "a" "b" "c" "b"]))
  (adjacency-list example-caves)
  (count (get-paths example-caves))
  (get-paths-2 example-caves)

  (count [["start" "A" "c" "A" "b" "A" "end"]
          ["start" "A" "b" "A" "c" "A" "end"]
          ["start" "A" "c" "A" "b" "end"]
          ["start" "b" "A" "c" "A" "end"]
          ["start" "A" "c" "A" "end"]
          ["start" "A" "b" "A" "end"]
          ["start" "A" "b" "end"]
          ["start" "b" "A" "end"]
          ["start" "A" "end"]
          ["start" "b" "end"]])

  (time (count (get-paths input)))
  ;;4707, takes close to 2 seconds

  (time (count (get-paths-2 input)))
  ;;130493, takes a little over 2 seconds
 )
