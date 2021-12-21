(ns aoc-14-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [difference union]]))

(def hex->binary
  {"0" "0000"
   "1" "0001"
   "2" "0010"
   "3" "0011"
   "4" "0100"
   "5" "0101"
   "6" "0110"
   "7" "0111"
   "8" "1000"
   "9" "1001"
   "A" "1010"
   "B" "1011"
   "C" "1100"
   "D" "1101"
   "E" "1110"
   "F" "1111"})

(def binary-input
  (->> (slurp "input.txt")
       (map (comp hex->binary str))))

(defn binary-string->decimal [s]
  (let [len (count s)]
    (bigint (reduce
             +
             (map-indexed
              (fn [idx v]
                (if (= \1 v) (Math/pow 2 (- len (inc idx))) 0))
              s)))))

(declare parse-packet)

(defn parse-length-type-id [{:keys [raw-string offset] :as p}]
  (-> p
      (assoc :length-type-id (Integer/parseInt (str (first (subs raw-string offset)))))
      (update :offset inc)))

(defn parse-type-0-subpackets
  [{:keys [raw-string offset] :as p}]
  (let [bit-len (binary-string->decimal (take 15 (subs raw-string offset)))
        subpackets-str (subs raw-string (+ 15 offset))
        subpackets (loop [subpackets []
                          offset 0]
                     (if (< offset bit-len)
                       (let [sp (parse-packet (subs subpackets-str offset))]
                         (recur (conj subpackets sp) (+ offset (:offset sp))))
                       subpackets))]
    (-> p
        (assoc :subpackets subpackets)
        (update :offset + 15 bit-len))))

(defn parse-type-1-subpackets
  [{:keys [raw-string offset] :as p}]
  (let [num-packets (binary-string->decimal (take 11 (subs raw-string offset)))
        subpackets-str (subs raw-string (+ 11 offset))
        [subpackets sp-offset] (loop [subpackets []
                                   offset 0
                                   n num-packets]
                              (if (> n 0)
                                (let [sp (parse-packet (subs subpackets-str offset))]
                                  (def spppp sp)
                                  (recur (conj subpackets sp) (+ offset (:offset sp)) (dec n)))
                                [subpackets offset]))]
    (-> p
        (assoc :subpackets subpackets)
        (update :offset + 11 sp-offset))))

(defn parse-operator-packet
  [{:keys [raw-string] :as p}]
  (let [p (parse-length-type-id p)]
    (if (= 0 (:length-type-id p))
      (parse-type-0-subpackets p)
      (parse-type-1-subpackets p))))


#_(defn parse-packet [{:keys [raw-string] :as p}]
  (let [len 0
        p (-> p
              parse-version
              parse-type-id)]
    (cond (= 4 (:type-id p))
          (parse-literal-data-packet p)

          :else
          (parse-operator-packet p))))

(defn parse-version
  [{:keys [raw-string offset] :as p}]
  (assoc p
         :version (binary-string->decimal (subs raw-string offset (+ 3 offset)))
         :offset (+ 3 offset)))

(defn parse-type-id
  [{:keys [raw-string offset] :as p}]
  (assoc p
         :type-id (binary-string->decimal (subs raw-string offset (+ 3 offset)))
         :offset (+ 3 offset)))

(defn parse-literal-value-packet
  [{:keys [raw-string offset] :as p}]
  (let [s (subs raw-string offset)]
    (loop [s s #_(drop-while #(= % \0) s) ;;(description mentions "leading" but I think it means "trailing"?)
           binary-number []
           len 0]
      (let [group (take 5 s)]
        (if (= \1 (first group))
          (recur (nthrest s 5) (conj binary-number (rest group)) (+ 5 len))
          (-> p
              (assoc :value (->> (conj binary-number (rest group))
                                 (map #(apply str %))
                                 (apply str)
                                 binary-string->decimal))
              (update :offset + 5 len)))))))

(defn parse-typed-packet
  [{:keys [type-id raw-string offset] :as p}]
  (cond (= 4 type-id)
        (parse-literal-value-packet p)

        :else
        (parse-operator-packet p)))

(defn parse-packet
  [s]
  (let [p (-> {:raw-string s
               :offset 0}
              parse-version
              parse-type-id
              parse-typed-packet)
        offset (:offset p)
        ;; offset (+ offset (- 4 (rem offset 4))) ;;remove any leftover zeroes from final hex character
        ;; ;;now remove any trailing hex 0's
        ;; offset (loop [offset offset] (if (and (<= (+ 4 offset) (count s))
        ;;                                       (= "0000" (subs s offset (+ offset 4))))
        ;;                                (recur (+ offset 4))
        ;;                                offset) )
        ]
    (assoc p :offset offset))
  )

;; {:version 1
;;  :type-id 1
;;  :subpackets [{:version
;;                :type-id}]}

(defn get-version-number-sum
  [hex-string]
  (let [s (apply str (map (comp hex->binary str) hex-string))
        p (parse-packet s)]
    (loop [sum 0
           packets [p]]
      (if-let [[p & ps] (seq packets)]
        (if (:subpackets p)
          (recur (+ sum (:version p)) (concat ps (:subpackets p)))
          (recur (+ sum (:version p)) ps))
        sum))))

(defn packet-value
  [p]
  (or (:value p)
      (case (int (:type-id p))
        0 (apply + (map packet-value (:subpackets p)))
        1 (apply * (map packet-value (:subpackets p)))
        2 (apply min (map packet-value (:subpackets p)))
        3 (apply max (map packet-value (:subpackets p)))
        5 (if (> (packet-value (first (:subpackets p)))
                 (packet-value (second (:subpackets p))))
            1 0)
        6 (if (< (packet-value (first (:subpackets p)))
                 (packet-value (second (:subpackets p))))
            1 0)
        7 (if (= (packet-value (first (:subpackets p)))
                 (packet-value (second (:subpackets p))))
            1 0))))

(comment
  (def example-literal-packet (apply str (map (comp hex->binary str) "D2FE28")))
  ;;24 characters - 3 (version id) - 3 (type id)
  ;;18 characters - 5 (group 1) - 5 (group 2) - 5 (group 3)
  ;;3 characters
  (parse-packet example-literal-packet)
  (-> (parse-packet example-literal-packet)
      :raw-string
      (subs 16))


  ;;6 + 15 = 21 - neeed to get up to 24
  (- 4 (rem 22 4))

  (def example-operator-packet (apply str (map (comp hex->binary str) "38006F45291200")))

  (parse-packet example-operator-packet)

  subpackets-str
  (parse-packet "1101000101001010010001001000")
  (parse-packet (subs "1101000101001010010001001000" 11))
  (parse-packet "110100010100")
  (parse-packet "01010010001001000")

  (def example-operator-packet-1 (apply str (map (comp hex->binary str) "EE00D40C823060")))
  (parse-packet example-operator-packet-1)

  (parse-packet (apply str (map (comp hex->binary str) "8A004A801A8002F478")))
  (get-version-number-sum "8A004A801A8002F478")
  ;;16
  (parse-packet (apply str (map (comp hex->binary str) "620080001611562C8802118E34")))
  (get-version-number-sum "620080001611562C8802118E34")
  ;;12
  (get-version-number-sum "C0015000016115A2E0802F182340")
  ;;23
  (get-version-number-sum "A0016C880162017C3686B18A3D4780")
  ;;31

  (get-version-number-sum (slurp "input.txt"))
  (apply str (map (comp hex->binary str) (slurp "input.txt")))
  (parse-packet (apply str (map (comp hex->binary str) (slurp "input.txt"))))

  (binary-string->decimal "00000110101")

  )
