(ns aoc-5-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset]))

;;seems like a case for transients or plain java arrays
;;get lines
;;find max x and y coordinates
;;make an array of size x * y
;;looking into working with java arrays/clojure I immediately find a borkdude post from a previous AOC - looks like I can use int-array and areduce
;;https://stackoverflow.com/questions/34153369/why-is-this-clojure-program-working-on-a-mutable-array-so-slow

(defn parse-line
  "Given a line: \"x1,y1 -> x2,y2\"
  returns a nested vector of ints: [[x1 y1] [x2 y2]]"
  [l]
  (let [[p1 p2] (st/split l #" -> ")
        [x1 y1] (map #(Integer/parseInt %) (st/split p1 #","))
        [x2 y2] (map #(Integer/parseInt %) (st/split p2 #","))]
    [[x1 y1] [x2 y2]]))

(comment
  (parse-line "1,2 -> 400,3")
  )

(defn parse-input
  [f]
  (->> f
       io/file
       io/reader
       line-seq
       (map parse-line)))

(defn get-bounds
  "Get the max x and y coordinates from the lines
  so that we can decide how big to make our array.
  We'll start the array at 0,0 and end it at max-x, max-y
  Even though technically there might not be lines touching 0,0."
  [lines]
  (let [x-points (concat (map ffirst lines) (map (comp first second) lines))
        y-points (concat (map (comp second first) lines) (map (comp second second) lines))
        max-x (inc (apply max x-points))
        max-y (inc (apply max y-points))]
    [max-x max-y]))

(defn make-canvas
  [[max-x max-y]]
  (int-array (* max-x max-y)))

(defn print-canvas
  [[max-x max-y] canvas]
  (let [rows (for [row (range max-y)]
               (map (partial aget canvas) (range (* row max-x) (* (inc row) max-x))))]
  (println "")
  (doseq [row rows]
    (println (apply str (interpose " " row))))
  (println "")))

(defn draw-point
  [[row-length column-length] canvas x y]
  (let [idx (+ x (* row-length y))
        existing-point (aget canvas idx)]
    (aset canvas idx (inc existing-point))))

(defn point-range
  [c1 c2]
  (if (< c1 c2)
    (range c1 (inc c2))
    (reverse (range c2 (inc c1)))))

(defn draw-line-no-diags
  [bounds canvas [[x1 y1] [x2 y2]]]
  (cond
    (= x1 x2)
    (doseq [y (point-range y1 y2)]
      (draw-point bounds canvas x1 y))

    (= y1 y2)
    (doseq [x (point-range x1 x2)]
      (draw-point bounds canvas x y1))

    :else
    ;;ignoring lines that are not horizontal or vertical
    nil))

(defn draw-lines-no-diags
  [bounds canvas lines]
  (doseq [line lines]
    (draw-line-no-diags bounds canvas line)))

(defn draw-line-with-diags
  [bounds canvas [[x1 y1] [x2 y2]]]
  (cond
    (= x1 x2)
    (doseq [y (point-range y1 y2)]
      (draw-point bounds canvas x1 y))

    (= y1 y2)
    (doseq [x (point-range x1 x2)]
      (draw-point bounds canvas x y1))

    :else
    (doseq [[x y] (partition 2 (interleave (point-range x1 x2) (point-range y1 y2)))]
      (draw-point bounds canvas x y))))

(comment
  (point-range 1 2)
  (point-range 2 1)
  (partition 2 (interleave (point-range 1 3) (point-range 1 3)))
  (partition 2 (interleave (point-range 9 7) (point-range 7 9)))
  )

(defn draw-lines-with-diags
  [bounds canvas lines]
  (doseq [line lines]
    (draw-line-with-diags bounds canvas line)))

(defn get-num-intersecting-points
  [canvas]
  (areduce canvas i ret (int 0)
           (if (< 1 (aget canvas i))
             (inc ret)
             ret)))

(comment
  ((comp first second) [[1 2] [3 4]])
  (parse-input "../sample-input.txt")


  ;;test drawing
  ;;it's like a lite-brite
  ;;or battleship
  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-point bounds canvas 9 9)
    (draw-line-no-diags bounds canvas [[0 0] [0 9]])
    (draw-point bounds canvas 0 5)
    (draw-line-no-diags bounds canvas [[3 4] [7 4]])
    (print-canvas bounds canvas))

  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-lines-no-diags bounds canvas lines)
    (print-canvas bounds canvas))

  ;;had to write a point-range function to draw 2,2 -> 2,1
  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-line-no-diags bounds canvas [[2 2] [2 1]])
    (print-canvas bounds canvas))

  ;;oh dear this is too big, definitely not worth printing
  #_(let [lines (parse-input "../input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-lines-no-diags bounds canvas lines)
    (print-canvas bounds canvas))

  ;;very slow, visualizing was maybe a silly idea
  (time (let [lines (parse-input "../input.txt")
         bounds (get-bounds lines)
         canvas (make-canvas bounds)]
     (draw-lines-no-diags bounds canvas lines)
     #_(print-canvas bounds canvas)))

  ;;this returns 5, as expected
  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-lines-no-diags bounds canvas lines)
    (get-num-intersecting-points canvas))

  ;;this should give my problem 1 answer
  (time (let [lines (parse-input "../input.txt")
              bounds (get-bounds lines)
              canvas (make-canvas bounds)]
          (draw-lines-no-diags bounds canvas lines)
          (get-num-intersecting-points canvas)))
  ;;and it does! but it takes an ungodly 12.5 seconds because this approach is pretty wasteful
  ;;but let's continue

  ;;test drawing diags
  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-line-with-diags bounds canvas [[0 0] [0 9]])
    (draw-line-with-diags bounds canvas [[0 0] [9 9]])
    (draw-line-with-diags bounds canvas [[9 7] [7 9]])
    (print-canvas bounds canvas))

  ;;prettyyyyy
  (let [lines (parse-input "../sample-input.txt")
        bounds (get-bounds lines)
        canvas (make-canvas bounds)]
    (draw-lines-with-diags bounds canvas lines)
    (print-canvas bounds canvas)
    (get-num-intersecting-points canvas))
  ;;gives 12, which is apparently the correct result

  ;;gives 22037, which is the correct answer
  ;;takes 14 seconds, which is a long time
  (time (let [lines (parse-input "../input.txt")
              bounds (get-bounds lines)
              canvas (make-canvas bounds)]
          (draw-lines-with-diags bounds canvas lines)
          (get-num-intersecting-points canvas)))

  ;;how to improve this?
  ;;it isn't really necessary to store state of every point, there's like a million and most of them are going to be blank
  ;;could maybe just mark each point in a hashmap, indexed by x y
  ;;can use transient map

  (time (let [lines (parse-input "../sample-input.txt")
              bounds (get-bounds lines)
              point-map (draw-lines-more-better bounds lines)]

          (get-num-intersecting-points-more-better point-map)))

  ;;heck yes - 14 seconds down to 219 milliseconds
  (time (let [lines (parse-input "../input.txt")
              bounds (get-bounds lines)
              point-map (draw-lines-more-better bounds lines)]

          (get-num-intersecting-points-more-better point-map)))
  )

(defn draw-point-more-better
  [[row-length column-length] m x y]
  (let [idx (+ x (* row-length y))
        existing-point (or (get m idx) 0)]
    (assoc! m idx (inc existing-point))))

(defn draw-line-more-better
  [m bounds [[x1 y1] [x2 y2]]]
  (cond
    (= x1 x2)
    (reduce (fn [m y]
              (draw-point-more-better bounds m x1 y))
            m
            (point-range y1 y2))

    (= y1 y2)
    (reduce (fn [m x]
              (draw-point-more-better bounds m x y1))
            m
            (point-range x1 x2))

    :else
    (reduce (fn [m [x y]]
              (draw-point-more-better bounds m x y))
            m
            (partition 2 (interleave (point-range x1 x2) (point-range y1 y2))))))

(defn draw-lines-more-better
  [bounds lines]
  (println "count lines " (count lines))
  (let [m (transient {})]
    (persistent! (reduce
                  (fn [m line]
                    (draw-line-more-better m bounds line))
                  m
                  lines))))

(defn get-num-intersecting-points-more-better
  [m]
  (reduce-kv (fn [sum _ v]
               (if (< 1 v)
                 (inc sum)
                 sum))
             0
             m))
