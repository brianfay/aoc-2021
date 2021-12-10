(ns aoc-9-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [union]]))

(def sample-input [2 1 9 9 9 4 3 2 1 0
                   3 9 8 7 8 9 4 9 2 1
                   9 8 5 6 7 8 9 8 9 2
                   8 7 6 7 8 9 6 7 8 9
                   9 8 9 9 9 6 5 6 7 8])

(comment (trampoline check-left #{[9 0]} 9 0))
(println "blahhhh")

(defn get-sample-point
  "Gets point from sample input data.
  Defaults to 9 if point is out of bounds (as if the table is surrounded by 9s above and below"
  [x y]
  (if (or (not (<= 0 x 9))
          (not (<= 0 y 4)))
    9
    (get
     sample-input
     (+ (* 10 y) x))))

(defn is-sample-min?
  "Checks if point from sample is a local minima, considering its four neighbors"
  [x y]
  (every?
   (fn [[x-off y-off]]
     (< (get-sample-point x y)
        (get-sample-point (+ x x-off) (+ y y-off))))
   [[-1 0] [1 0]
    [0 -1] [0 1]]))

(comment 
  (is-sample-min? 0 0) ;;false
  (is-sample-min? 1 0) ;;true
  (is-sample-min? 9 0) ;;true
  (is-sample-min? 2 2) ;;true
  (is-sample-min? 6 4) ;;true
  )

(defn get-sample-answer-1
  []
  (->> (for [x (range 10)
             y (range 5)]
         ((juxt get-sample-point is-sample-min?) x y))
       (filter second)
       (reduce (fn [sum [v _]] (+ sum (inc v))) 0)))

(defn get-sample-minimum-points
  []
  (->> (for [x (range 10)
             y (range 5)]
         [[x y] (is-sample-min? x y)])
       (filter second)
       (map first)))

(comment
  (get-sample-answer-1)
  ;;15
  (get-sample-minimum-points)

  (first (get-sample-minimum-points))
  )


;;depth first traverse starting from minimum point
;;edges are cardinal points
;;if a vertex is a 9, we want to stop the DFS

;;if x y not visited
;;if 9 mark visited, return
;;else visit cardinal points, then mark visited


;;check point - check left, check up, check right, check down




(declare check-up check-down check-left check-right)


(defn check-up
  [visited from-x from-y]
  (let [x from-x y (dec from-y)]
    (if (or (visited [x y])
            (= 9 (get-sample-point x y)))
      (constantly visited)
      (fn [& _]
        (comp (check-left (conj visited [x y]) x y)
              (check-right (conj visited [x y]) x y)
              (check-up (conj visited [x y]) x y))))))

(defn check-down
  [visited from-x from-y]
  (let [x from-x y (inc from-y)]
    (if (or (visited [x y])
            (= 9 (get-sample-point x y)))
      (constantly visited)
      (fn [& _]
        (comp (check-right (conj visited [x y]) x y)
              (check-left (conj visited [x y]) x y)
              (check-down (conj visited [x y]) x y))))))


(comment (trampoline check-left #{[9 0]} 9 0))
(println "                ")

(comment (visit-sample-points #{} 9 0))

(defn should-visit?
  [visited [x y]]
  (and (not (visited [x y]))
       (not= 9 (get-sample-point x y))))

(defn visit-sample-points
  [visited x y]
  (loop [[x y] [x y]
         visited #{}
         unvisited #{[x y]}]
    (let [unvisited (union unvisited
                           (set (filter (partial should-visit? visited)
                                        [[(dec x) y]
                                         [(inc x) y]
                                         [x (dec y)]
                                         [x (inc y)]])))]
      (if-let [[[x y] & rest-unvisited] (seq unvisited)]
        (recur [x y] (conj visited [x y]) (set rest-unvisited))
        visited))))

(comment
  (visit-sample-points #{} 9 0)
  (sort 
   (for [[x y] (get-sample-minimum-points)]
     (count (visit-sample-points #{} x y))
     ))
  )

(defn check-left
  [visited from-x from-y]
  (let [x (dec from-x) y from-y]
    (if (or (visited [x y])
            (= 9 (get-sample-point x y)))
      (constantly visited)
      (fn [& _]
        (println "checked left" x y)
        (comp ;(check-down (conj visited [x y]) x y)
              (check-up (conj visited [x y]) x y)
              (check-left (conj visited [x y]) x y))))))

(defn check-right
  [visited from-x from-y]
  (let [x (inc from-x) y from-y]
    (if (or (visited [x y])
            (= 9 (get-sample-point x y)))
      (constantly visited)
      (fn [& _]
        (comp (check-up (conj visited [x y]) x y)
              (check-down (conj visited [x y]) x y)
              (check-right (conj visited [x y]) x y))))))

(defn check-sample-in-dir
  [from-x from-y dir]
  (let [[x y] (case dir
                :left  [(dec from-x) from-y]
                :up    [from-x (dec from-y)]
                :down  [from-x (inc from-y)]
                :right [(inc from-x) from-y])]
    (when (not= 9 (get-sample-point x y))
      (union #{[x y]}
             (when-not (= dir :right)
               (check-sample-in-dir x y :left))
             (when-not (= dir :down)
               (check-sample-in-dir x y :up))
             (when-not (= dir :left)
               (check-sample-in-dir x y :right))
             (when-not (= dir :up)
               (check-sample-in-dir x y :down))))))

(comment
  (check-sample-in-dir 9 0 :left)
  )

;; (let [x 1
;;       y 0]
;;   (union (check-sample-in-dir x y :left)
;;          (check-sample-in-dir x y :up)
;;          (check-sample-in-dir x y :down)
;;          (check-sample-in-dir x y :right)))

;; (check-sample-point 1 0)
;; (check-up 0 2)
;; (check-left 9 0)
;;check-up
;;check-down
;;check-left
;;check-right
  ;; [[-1 0] [1 0]
  ;;  [0 -1] [0 1]]

(def input (-> "input.txt"
               io/file
               io/reader
               line-seq
               (->> (mapcat seq)
                    (mapv #(Integer/parseInt (str %))))))

(def row-length (int (Math/sqrt (count input))))

(defn get-point
  "Gets point from the actual input data.
  Defaults to 9 if point is out of bounds (as if the table is surrounded by 9s above and below"
  [x y]
  (if (or (not (<= 0 x 99))
          (not (<= 0 y 99)))
    9
    (get
     input
     (+ (* 100 y) x))))

(defn is-min?
  "Checks if point from sample is a local minima, considering its four neighbors"
  [x y]
  (every?
   (fn [[x-off y-off]]
     (< (get-point x y)
        (get-point (+ x x-off) (+ y y-off))))
   [[-1 0] [1 0]
    [0 -1] [0 1]]))

(defn get-answer-1
  []
  (->> (for [x (range 100)
             y (range 100)]
         ((juxt get-point is-min?) x y))
       (filter second)
       (reduce (fn [sum [v _]] (+ sum (inc v))) 0)))

(comment
  (get-answer-1)
  )


;;get minimum points
(defn get-minimum-points
  []
  (->> (for [x (range 100)
             y (range 100)]
         [[x y] (is-min? x y)])
       (filter second)
       (map first)))
;; (get-minimum-points)

;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 9 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 9 9 1 9 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 9 1 1 2 9 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 9 9 1 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
;;0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0



(defn should-visit?
  [visited [x y]]
  (and (not (visited [x y]))
       (not= 9 (get-point x y))))

(defn visit-points
  [visited x y]
  (loop [[x y] [x y]
         visited #{}
         unvisited #{[x y]}]
    (let [unvisited (union unvisited
                           (set (filter (partial should-visit? visited)
                                        [[(dec x) y]
                                         [(inc x) y]
                                         [x (dec y)]
                                         [x (inc y)]])))]
      (if-let [[[x y] & rest-unvisited] (seq unvisited)]
        (recur [x y] (conj visited [x y]) (set rest-unvisited))
        visited))))

(comment
  (visit-points #{} 9 0)
  take-last
  (->> (for [[x y] (get-minimum-points)]
         (count (visit-points #{} x y)))
       sort
       (take-last 3)
       (apply *))

  (->> [1 2 3] (apply *))
  )

;;check crosshair box surrounding low point
;;if val is 9, continue
;;otherwise, recursively check crosshair box surrounding new point, then add the point to list
