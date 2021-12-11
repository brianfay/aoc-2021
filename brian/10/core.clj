(ns aoc-10-clj.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st]
            [clojure.set :as cset :refer [union]]))

(defn try-pop [stack c]
  (if (= c (first stack))
    (rest stack)
    (reduced
     (case c
       \( \)
       \[ \]
       \< \>
       \{ \}
       :unrecognized-char))))

(defn get-wrong-closing-character
  [s]
  (reduce (fn [stack c]
            (case c
              \( (cons \( stack)
              \[ (cons \[ stack)
              \< (cons \< stack)
              \{ (cons \{ stack)
              \) (try-pop stack \()
              \] (try-pop stack \[)
              \> (try-pop stack \<)
              \} (try-pop stack \{)
              stack))
          '()
          s))

(defn get-char-score
  [c]
  (case c
    \) 3
    \] 57
    \} 1197
    \> 25137))

(defn get-score [lines]
  (->> (map get-wrong-closing-character lines)
       (filter char?)
       (map get-char-score)
       (apply +)))

(def input (-> "input.txt"
               io/file
               io/reader
               line-seq))

(defn generate-closing-string
  [incomplete]
  (reduce
   (fn [s c]
     (case c
       \( (str s ")")
       \[ (str s "]")
       \< (str s ">")
       \{ (str s "}")))
   ""
   incomplete))

(defn get-autocomplete-score
  [s]
  (let [char-val {\] 2
                  \) 1
                  \} 3
                  \> 4}]
    (reduce
     (fn [score c]
       (+ (char-val c)
          (* 5 score)))
     0
     s)))

(comment
  (get-autocomplete-score "])}>") ;;294
  )

(defn get-autocomplete-scores
  [lines]
  (eduction
   (comp (map get-wrong-closing-character)
         (remove char?)
         (map generate-closing-string)
         (map get-autocomplete-score))
   lines))

(defn get-problem-2-answer
  []
  (let [scores (seq (get-autocomplete-scores input))]
    (-> scores
        sort
        (nth (Math/floor (/ (count scores) 2))))))

(comment

  (Math/floor (/ 11 2))
  (get-problem-2-answer)
  ;;2116639949
  (generate-closing-string (get-wrong-closing-character "((([<"))
  (generate-closing-string (get-wrong-closing-character "[(()[<>])]({[<{<<[]>>( "))
  (get-score input)
  ;;374061
  )

(comment
  (get-wrong-closing-character "([])")
  (get-wrong-closing-character "{()()()}")
  (get-wrong-closing-character "[<>({}){}[([])<>]]")
  (get-wrong-closing-character "[({(<(())[]>[[{[]{<()<>>")
  (get-wrong-closing-character "{([(<{}[<>[]}>{[]{[(<()>")

  (get-score ["[({(<(())[]>[[{[]{<()<>>"
              "[(()[<>])]({[<{<<[]>>("
              "{([(<{}[<>[]}>{[]{[(<()>"
              "(((({<>}<{<{<>}{[]{[]{}"
              "[[<[([]))<([[{}[[()]]]"
              "[{[{({}]{}}([{[{{{}}([]"
              "{<[[]]>}<{[{[{[]{()[[[]"
              "[<(<(<(<{}))><([]([]()"
              "<{([([[(<>()){}]>(<<{{"
              "<{([{{}}[<[[[<>{}]]]>[]]"])
  )
