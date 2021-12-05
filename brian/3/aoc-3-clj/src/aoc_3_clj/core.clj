(ns aoc-3-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]))

(defn parse-input [f]
  (->> f
       io/file
       io/reader
       line-seq))

(defn common-bit-list
  [lines]
  (let [num-lines (count lines)
        half-num-lines (/ num-lines 2)
        num-bits (count (first lines))
        arr (make-array Integer/TYPE num-bits)]
    (doseq [l lines]
      (doall
       (map-indexed
        (fn [idx ch]
          (when (= \1 ch)
            (aset arr idx (inc (aget arr idx)))))
        l)))
    (reduce
     (fn [acc v]
       (conj
        acc
        (cond (< v half-num-lines)
              (int 0)

              (> v half-num-lines)
              (int 1)

              :else
              (int -1)))) ;;weird special case for equality, doesn't actually happen in my input list
     []
     (seq arr))))

(defn bit-list->decimal [s]
  (let [len (count s)]
    (reduce
    +
    (map-indexed
     (fn [idx v]
       (if (= 1 v) (Math/pow 2 (- len (inc idx))) 0))
     s))))

(defn binary-string->decimal [s]
  (let [len (count s)]
    (reduce
     +
     (map-indexed
      (fn [idx v]
        (if (= \1 v) (Math/pow 2 (- len (inc idx))) 0))
      s))))

(defn gamma-rate
  [lines]
  (bit-list->decimal (common-bit-list lines)))

(defn invert-bit-list [s]
  (reverse
   (reduce
    (fn [acc c]
      (if (= 0 c)
        (cons 1 acc)
        (cons 0 acc)))
    '()
    s)))

(defn epsilon-rate
  [lines]
  (-> lines
      common-bit-list
      invert-bit-list
      bit-list->decimal))

(defn common-bit-at-index
  [lines idx]
  (let [num-lines (count lines)
        half-num-lines (/ num-lines 2)
        bit-sum (reduce + (for [l lines]
                            (if (= \1 (nth l idx)) 1 0)))]
    (cond (< bit-sum half-num-lines)
          (int 0)

          (> bit-sum half-num-lines)
          (int 1)

          :else
          (int -1))))

(defn life-support-rating
  [lines bit-criteria]
  (loop [ls lines idx 0]
    (let [common-bit (common-bit-at-index ls idx)
          filtered-lines (filter
                          #(bit-criteria
                            common-bit
                            (Integer/parseInt (str (nth % idx))))
                          ls)]
      (cond
        (= (count filtered-lines) 1)
        (first filtered-lines)

        (= (count filtered-lines) 0)
        (last ls)

        :else
        (recur filtered-lines
               (inc idx))))))

(defn binary-string->decimal [s]
  (let [len (count s)]
    (reduce
     +
     (map-indexed
      (fn [idx v]
        (if (= \1 v) (Math/pow 2 (- len (inc idx))) 0))
      s))))

(defn oxygen-generator-rating
  [lines]
  (-> lines
      (life-support-rating
       (fn [common-bit bit]
         (or (and
              (= -1 common-bit)
              (= 1 bit))
             (= common-bit bit))))
      binary-string->decimal))

(defn co2-scrubber-rating
  [lines]
  (-> lines
      (life-support-rating
       (fn [common-bit bit]
         (or (and
              (= -1 common-bit)
              (= 0 bit))
             (and (not= -1 common-bit)
                  (not= common-bit bit)))))
      binary-string->decimal))

(defn- main
  [x]
  (let [lines (parse-input "../input.txt")]
    (println "Power consumption "
             (* (gamma-rate lines)
                (epsilon-rate lines)))
    (println "Life support rating "
             (int (* (oxygen-generator-rating lines)
                     (co2-scrubber-rating lines))))))
