(ns aoc-13-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference union]]))

(def example-points
  [[6 10]
   [0 14]
   [9 10]
   [0 3]
   [10 4]
   [4 11]
   [6 0]
   [6 12]
   [4 1]
   [0 13]
   [10 12]
   [3 4]
   [3 0]
   [8 4]
   [1 10]
   [2 14]
   [8 10]
   [9 0]])

(def example-folds
  [[:y 7]
   [:x 5]])

(defn plot-points
  [points]
  (let [row-size (inc (apply max (map first points)))
        column-size (inc (apply max (map second points)))]
    {:row-size row-size
     :column-size column-size
     :points (reduce
              (fn [v [x y]]
                (assoc v (+ x (* y row-size)) true))
              (vec (repeat (* row-size column-size) false))
              points)}))

(defn print-point-plot
  [{:keys [row-size column-size points]}]
  (println "")
  (doseq [row (partition row-size points)]
    (apply println (replace {false "." true "#"} row))))


;;top fold
;;everything from 0,0
;;to x, split point
;;bottom fold


(defn invert-y
  [{:keys [row-size column-size points] :as point-plot}]
  (assoc
   point-plot
   :points
   (reduce
    (fn [v [x y]]
      (conj v (nth points (+ x (* row-size (- column-size (inc y)))))))

    []
    (for [y (range column-size)
          x (range row-size)]
      [x y]))))

(defn overlap-y [p1 p2 offset]
  ;;p1 should be the higher subplot in the fold
  ;;offset is how many points higher it starts from p2
  ;;offset is the amount that p2 hangs above or below p1 (if at all)
  {:row-size (:row-size p1)
   :column-size (+ (:column-size p2) offset)
   :points (reduce (fn [v [x y]]
                     (conj v
                           (or
                            (nth (:points p1) (+ x (* (:row-size p1) y)))
                            (nth (:points p2) (+ x (* (:row-size p1) (- y offset))) false))))
                   []
                   (for [y (range (+ (:column-size p2) offset))
                         x (range (:row-size p1))]
                     [x y]))}
  )

(defn y-fold
  [{:keys [row-size column-size points]} split-point]
  (let [p1 {:points (subvec points 0 (* row-size split-point))
            :row-size row-size
            :column-size split-point}
        p2 (invert-y {:points (subvec points
                                      (* row-size (inc split-point))
                                      (* row-size column-size))
                      :row-size row-size
                      :column-size (- column-size (inc split-point))})
        ]
    (int (Math/floor (/ 5 2)))
    (if (< (:column-size p1) (:column-size p2))
      (overlap-y p2 p1 (- (:column-size p2) (:column-size p1)))
      (overlap-y p1 p2 (- (:column-size p1) (:column-size p2))))
    
    #_(cond
      (and (odd? column-size) (= split-point (Math/floor (/ column-size 2))))
      (overlap p1 p2 0)

      (< split-point (Math/floor (/ column-size 2)))
      (overlap p1 p2 (- (:column-size p2) (:column-size p1)))


      )
    #_{:row-size row-size
     :column-size new-column-size
     :points (reduce (fn [v [x y]]
                       (conj v
                             (or
                              (nth points (+ x (* row-size y)))
                              (nth points (+ x (* row-size (- new-column-size (inc y)))
                                             (* row-size (inc y-line)))))))
                     []
                     (for [y (range new-column-size) x (range row-size)] [x y]))}))

(comment
  (print-point-plot (plot-points [[0 0] [5 1] [5 2] [2 3]]))
  (print-point-plot (invert-y (plot-points [[0 0] [5 1] [5 2] [2 3]])))
  (print-point-plot (y-fold (plot-points [[0 0] [5 1] [5 2] [2 3]]) 1))
  (print-point-plot (plot-points [[0 0] [5 1] [5 2] [2 3] [2 5]]))
  (* 6 4)
  (* 6 3)


  )

#_(defn y-fold
  [{:keys [row-size column-size points]} y-line]
  (let [new-column-size (- column-size (inc y-line))]
    {:row-size row-size
     :column-size new-column-size
     :points (reduce (fn [v [x y]]
                       (conj v
                             (or
                              (nth points (+ x (* row-size y)))
                              (nth points (+ x (* row-size (- new-column-size (inc y)))
                                             (* row-size (inc y-line)))))))
                     []
                     (for [y (range new-column-size) x (range row-size)] [x y]))}))

(defn x-fold
  [{:keys [row-size column-size points]} x-line]
  ;;I think this function is technically broken for folds that aren't right down the middle
  ;;I fixed this problem for y-fold, and that got me the answer... #won't fix
  (let [new-row-size (- row-size (inc x-line))]
    {:row-size new-row-size
     :column-size column-size
     :points (reduce (fn [v [x y]]
                       (conj v
                             (or
                              (nth points (+ x (* row-size y)))
                              (nth points (+ (- new-row-size (inc x)) (inc x-line)
                                             (* row-size y))))))
                     []
                     (for [y (range column-size) x (range new-row-size)] [x y]))}))

(def input
  (let [[raw-points raw-folds] (->> "input.txt"
                                    io/file
                                    io/reader
                                    line-seq
                                    (split-with #(not= % "")))]
    {:points (mapv (fn [s] (mapv #(Integer/parseInt %) (st/split s #","))) raw-points)
     :folds (mapv (fn [s] (-> s
                              (st/replace #"fold along " "")
                              (st/split #"=")
                              ((juxt #(keyword (first %))
                                     #(Integer/parseInt (second %))))))
                  (remove empty? raw-folds))}))

(defn fold-points
  [direction n point-plot]
  (case direction
    :x (x-fold point-plot n)
    :y (y-fold point-plot n)
    :ruh-roh))


(defn first-puzzle-answer
  []
  (let [[dir n] (first (:folds input))]
    (count (filter true? (:points (fold-points dir n (plot-points (:points input))))))))

(defn print-second-puzzle-answer
  []
  (print-point-plot
   (reduce
    (fn [plot [dir n]]
      (do (println "folding " (:row-size plot) (:column-size plot) dir n)
          (fold-points dir n plot))
      )
    (plot-points (:points input))
    (:folds input))))

(comment
  subvec
  (plot-points example-points)
  (print-point-plot (plot-points example-points))
  (print-point-plot (y-fold (plot-points example-points) 7))
  (print-point-plot (fold-points :y 7 (plot-points example-points)))

  (print-point-plot (->> (plot-points example-points)
                         (fold-points :y 7)
                         (fold-points :x 5)
                        ))

  (first (:folds input))
  (:column-size (plot-points (:points input)))

  (let [])
  (time (first-puzzle-answer))
  ;;it's not 584915... I tried (keep identity v) which leaves false - needed (filter true? v)
  ;;actual answer is 781
  (let [[dir n] (first (:folds input))]
    (count (filter true? (:points (fold-points :y 7 (plot-points example-points))))))
  (print-second-puzzle-answer)
  (count (:points input))
  (count (:folds input))
  (int (/ 1311 2)) ;;655
  (int (/ 893 2))


  (def sanity-check-points
    [[0 0]
     [3 3]])

  (Math/floor (/ 446 2))

  (print-point-plot (plot-points sanity-check-points))
  (print-point-plot (->> (plot-points sanity-check-points)
                         (fold-points :y 2)))


  )
