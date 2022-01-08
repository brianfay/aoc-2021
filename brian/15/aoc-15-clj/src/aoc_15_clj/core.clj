(ns aoc-15-clj.core
  (:require [clojure.java.io :as io]
            [clojure.data.priority-map :refer [priority-map priority-map-by]]))

(def example-table
  [1 1 6 3 7 5 1 7 4 2
   1 3 8 1 3 7 3 6 7 2
   2 1 3 6 5 1 1 3 2 8
   3 6 9 4 9 3 1 5 6 9
   7 4 6 3 4 1 7 1 1 1
   1 3 1 9 1 2 8 1 3 7
   1 3 5 9 9 1 2 4 2 1
   3 1 2 5 4 2 1 6 3 9
   1 2 9 3 1 3 8 5 2 1
   2 3 1 1 9 4 4 5 8 1])

(def example-rowsize 10)
(def example-columnsize 10)

(defn xy->idx
  ([rowsize [x y]] (xy->idx rowsize x y))
  ([rowsize x y] (+ (* rowsize y) x)))

(defn idx->xy [rowsize idx]
  [(rem idx rowsize)
   (quot idx rowsize)])

(defn in-bounds?
  ([rowsize columnsize idx]
   (apply in-bounds? rowsize columnsize (idx->xy rowsize idx)))
  ([rowsize columnsize x y]
   (and (<= 0 x (dec rowsize))
        (<= 0 y (dec columnsize)))))

(defn risk-at-point
  ([table rowsize columnsize x y] (risk-at-point (xy->idx rowsize x y)))
  ([table rowsize columnsize idx] (get table idx)))

;;There's a nice write up of Dijkstra's in "Algorithms" by Panos Louridas

(defn neighbors
  "Returns a list of indexes of neighboring nodes - unvisited and in-bounds,
  in the four cardinal directions of current-node."
  [table rowsize columnsize unvisited current-node]
  (let [[x y] (idx->xy rowsize (first current-node))
        candidate-idxs  [[x (dec y)]
                         [(dec x) y] [(inc x) y]
                         [x (inc y)]]
        valid-idxs (filter #(and (apply in-bounds? rowsize columnsize %)
                                 (unvisited (xy->idx rowsize %)))
                           candidate-idxs)]
    (map (partial xy->idx rowsize) valid-idxs)))

(defn update-estimates
  [table rowsize columnsize [current-node-idx [current-node-estimate _]] estimates nodes]
  (reduce
   (fn [estimates idx]
     (let [existing-estimate (first (estimates idx))
           new-estimate (+ current-node-estimate
                           (risk-at-point table rowsize columnsize idx))]
       (cond-> estimates
         (< new-estimate existing-estimate)
         (assoc idx [new-estimate current-node-idx]))) )
   estimates
   nodes))

(defn path-from [estimates idx]
  (loop [idx idx
         path '()]
    (let [prev-node (second (estimates idx))]
      (if (= :start prev-node) path
          (recur prev-node (cons idx path))))))

(defn initial-estimates
  [start-idx rowsize columnsize]
  (assoc (reduce (fn [m idx]
                   (assoc m idx [Integer/MAX_VALUE nil]))
                 (priority-map-by #(< (first %1) (first %2)))
                 (range (* rowsize columnsize)))
         start-idx
         [0 :start]))

(defn update-priority
  [m estimates points-to-update]
  (reduce
   (fn [m idx]
     (assoc m idx (first (estimates idx))))
   m
   points-to-update))

(defn shortest-path
  [table rowsize columnsize [start-x start-y] [end-x end-y]]
  (let [neighbors (partial neighbors table rowsize columnsize)
        update-estimates (partial update-estimates table rowsize columnsize)
        risk-at-point (partial risk-at-point table rowsize columnsize)]
    (loop [estimates (into []
                           (concat [[(xy->idx rowsize start-x start-y) :start]]
                                   (repeat (dec (* rowsize columnsize))
                                           [Integer/MAX_VALUE ;;distance
                                            nil])));;through which node
           unvisited (into
                      (priority-map)
                      (for [idx (range (* rowsize columnsize))]
                        [idx (first (estimates idx))]))]
      (if-let [idx (ffirst unvisited)]
        (let [node [idx (estimates idx)]
              neighbors (neighbors unvisited node)
              new-estimates (update-estimates node estimates neighbors)]
          (recur new-estimates
           (dissoc (update-priority unvisited new-estimates neighbors) (first node))))
        (path-from estimates (xy->idx rowsize end-x end-y))))))

(defn shortest-path-risk
  [table rowsize columnsize [start-x start-y] [end-x end-y]]
  (reduce + (map (partial risk-at-point table rowsize columnsize)
                 (shortest-path table rowsize columnsize [start-x start-y] [end-x end-y]))))

(defn parse-line [l]
  (map (comp #(Integer/parseInt %) str) l))

(let [lines (->> "../input.txt"
                 io/file
                 io/reader
                 line-seq)
      rowsize (count (first lines))
      columnsize (count lines)
      table (->> lines (mapcat parse-line) (into []))]
  (def input-table table)
  (def input-rowsize rowsize)
  (def input-columnsize columnsize))

(comment
  (neighbors example-table
             example-rowsize
             example-columnsize
             (set (range 100))
             [90 []])

  (time (shortest-path example-table example-rowsize example-columnsize [0 0] [9 9]))
  (shortest-path-risk example-table example-rowsize example-columnsize [0 0] [9 9])

  (shortest-path input-table input-rowsize input-columnsize
                 [0 0]
                 [(dec input-rowsize) (dec input-columnsize)])

  (time (shortest-path-risk input-table input-rowsize input-columnsize
                       [0 0]
                       [(dec input-rowsize) (dec input-columnsize)]))
  ;;502 - wrong answer
  ;;takes like 23 seconds with no priority queue

  ;;With priority queue for unvisited, goes down to 134 ms (phew)

  )

(defn inc-tile-num
  [n]
  (if (= 9 n) 1 (inc n)))

(defn expand-tile
  [tile rowsize columnsize]
  (reduce
   (fn [acc [x y]]
     (let [tile-x-offset (quot x rowsize)
           tile-y-offset (quot y columnsize)
           mapped-x (rem x rowsize)
           mapped-y (rem y rowsize)
           val (tile (xy->idx rowsize mapped-x mapped-y))
           ]
       (conj acc (nth (iterate inc-tile-num val) (+ tile-x-offset tile-y-offset)))))
   []
   (for [y (range (* 5 columnsize))
         x (range (* 5 rowsize))]
     [x y])))

(comment
  (shortest-path (expand-tile example-table example-rowsize example-columnsize)
                 (* 5 example-rowsize)
                 (* 5 example-columnsize)
                 [0 0]
                 [49 49])
  (shortest-path-risk (expand-tile example-table example-rowsize example-columnsize)
                      (* 5 example-rowsize)
                      (* 5 example-columnsize)
                      [0 0]
                      [49 49])
  ;;315

  (time (shortest-path (expand-tile input-table input-rowsize input-columnsize)
                       (* 5 input-rowsize)
                       (* 5 input-columnsize)
                       [0 0]
                       [499 499]))
  ;;3 and a quarter-ish seconds

  (time (shortest-path-risk (expand-tile input-table input-rowsize input-columnsize)
                            (* 5 input-rowsize)
                            (* 5 input-columnsize)
                            [0 0]
                            [499 499]))
  ;;2901
  )
;;lulz im in ur codez