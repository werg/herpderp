(ns herpderp.core
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))

; --------------------------------------------------------------
; defining rules

; a rule has a head/body construction
; or rather: 
; condition -> consequent

; these rules get checked on every shovel.
; they can rely on vanilla core.logic relations for their inferences

(defmacro defrule [nom [& varvec] head-expr body-expr]
  "A rule is a logical implication: head-expr => body-expr.
   If the condition head-expr is fulfilled, body is evaluated and logic variables specified in the varvector are shared between head and body."
  `(defn ~nom [] 
      (run* [q#]
        (fresh [~@varvec]
          (conda 
            [~head-expr ~body-expr (== q# {:data [~@varvec] :success :pass})]
            [~head-expr            (== q# {:data [~@varvec] :success :fail})])))
    ; TODO: also register the rule with the suite
    ))

(def rule-not-tbl
  {:pass :fail :fail :pass})

(defn rule-not [rule-output]
  "Negation can be turned into a construction with the success/failure message (?)"
  (map #(assoc % :success (rule-not-tbl (:success %))) rule-output))


; -----------------------------------------------------------
; shoveling in
;
; for now: check all rules at every shovel-in (if so configured)
; later on: analyze to see when which kinds of facts about the state
;           are accessed, and make a map from those to their containing goals
;           and then map all those goals to all their containing goals
;           so map an expression to a list of containing goals

;           hence make the (state-fact .. macro)
;           register its structures with that registry

; 
; one can define shovel-rules that all get called on the incoming shoveled item
; using doseq
; i bet this is some kind of inbuilt thing and has a real name:
(defn giaku-foreach [funs item]
  "Applies a seq of functions to an item. Used for side-effects."
  (doseq [f funs]
    (f item)))

(defn shovel-in [item shovels rules]
  "Assert (shovel) attributes of new item. Then check all rules and return their success/failure messages."
  (do
    (giaku-foreach shovels item)
    (map #(%) rules)))

