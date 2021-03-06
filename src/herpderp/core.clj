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
  "Assert (shovel) attributes of new item into the knowledge base.
   Then check all rules and return their success/failure messages."
  (do
    (giaku-foreach shovels item)
    (map #(%) rules)))


; defshovel:
; have a target rel
; we need arity of arguments, like in defrel
; we create a function where we pour in the event and receive a list of all the asserts that should happen
; shovel does the asserts

; a non-logic, functional form of defshovel, where tuples are generated by a straight gentuple function
(defmacro defshovel-f [nom [& rel-args] fun]
  "Define a new shovel with associated relation, shoveling event items into facts of that relation.
  third argument is a function that gets called on the incoming item, producing a sequence of tuples for that relation

   (defshovel myshovel [p1 p2 p3] (fn [[a b c]] [a b c]))"
  (let [gentuple-fun (symbol (str nom "-gentuple-fun"))
        shovel (symbol (str nom "-shovel"))]
    `(do
      (defrel ~nom ~@rel-args)
      (defn ~shovel [~'item]
        (let [~'tuples (gentuple-fun ~'item)]
          (facts ~nom ~'tuples))))))
; TODO: put gentuple and shovel in metadata of relation like so: (def barrel (with-meta barrel {:foo 1}))

; a relational version of defshovel
; every gentuple is a binary defne: item, tuple
(defmacro defshovel-r [nom [& rel-args] & body]
  "Define a new shovel with associated relation, shoveling event items into facts of that relation.
   The body is that of a defne binary predicate, putting the event item in relation to a fact-tuple of the relation.

   (defshovel myshovel [p1 p2 p3] ([event [p1 p2 p3]] (== event [p1 p2 p3])))"
  (let [gentuple (symbol (str nom "-gentuple"))]
    `(do
      (defne ~gentuple [~'item ~'tuple] ~@body)
      (defshovel-f ~nom [~@rel-args] 
        (fn [~'item] (run* [~'tuple] (~gentuple ~'item ~'tuple)))))))

; TODO: learn how to attach to source-object namespace
;from core.logic line 1605 
(comment (let [rel-ns (:ns (meta rel))
           rel-set (var-get (ns-resolve rel-ns (set-sym (.name rel) arity)))
           tuples (map vec tuples)]))