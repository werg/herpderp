(ns herpderp.core
  (:refer-clojure :exclude [==])
  (:use clojure.core.logic))


(defn run-rule [rulename item]
  (let 
    [result (run 1 [q]
              (fresh [suc]
                (rulename item suc) 
                (== q {:item item :success suc})))]
    (first result)))


(defmacro defrule [rulename head & body]
  `(defna ~rulename [~head succ#]
    ([~head :pass] ~@body)
    ([~head :fail])))


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
    (map #(%) rules))

; a rule has a head/body construction
; or rather: 
; condition -> consequent

; these rules get checked on every shovel.
; they can rely on vanilla core.logic relations for their inferences

(defn run-rule [head body]
  "Run a rule with head and body, where head and body are defne's."
  (run* [q]
    (fresh [r]
      (conda 
        [(head r) (body r) (== q {:data r :success :pass})]
        [(head r)          (== q {:data r :success :fail})]))))

(defmacro defrule [nom [& varvec] head-expr body-expr]
  "A rule is a logical implication: head-expr => body-expr.
   If the condition head-expr is fulfilled, body is evaluated and logic variables specified in the varvector are shared between head and body."
  `(do
    (defne he# [~@varvec] ([~head-expr]))
    (defne be# [~@varvec] ([~body-expr]))
    (defn ~nom (run-rule he# be#))
    ; TODO: also register the rule with the suite
    ))

; TODO: consider actually not to put in head/body in as defne's, rather leave them as expressions
; because it's stupid that head and tail can't share vars? actually they can't anyway (?), just by the hierarchical structure. so maybe it's ok?

(def rule-not-ttl
  {:pass :fail :fail :pass})

(defn rule-not [rule-output]
  "Negation can be turned into a construction with the success/failure message (?)"
  (map (assoc % :success (rule-not-ttl (:success %))) rule-output))
