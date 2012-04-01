(ns herpderp.rulesets.temporal
  (:refer-clojure :exclude [== = > < >= <=])
  (:use [herpderp.core]
        [clojure.core.logic]
        [clojure.core.logic.arithmetic]))

; implement all the before / after during stuff
; make it so that it's extensible to various event formats

; so you should include id's in defshovel
; and include ways to attach several shovels to one relation

; we need some sort of core.logic multimethod

; beginning and end timestamps
(defrel begin-ts ^:index event ^:index stamp)
(defrel end-ts ^:index event ^:index stamp)

;(defne time-of)

(defne before [e1 e2]
  ([e1 e2] 
    (fresh [t1 t2] 
      (end-ts e1 t1) 
      (begin-ts e2 t2) 
      (< t1 t2))))

(defne after [e1 e2]
  ([e1 e2] (before e2 e1)))

(defne starts-before [e1 e2]
  ([e1 e2]
    (fresh [t1 t2]
      (begin-ts e1 t1)
      (begin-ts e2 t2)
      (< t1 t2))))

(defne starts-after [e1 e2]
  ([e1 e2] (starts-before e2 e1)))

(defne ends-before [e1 e2]
  ([e1 e2]
    (fresh [t1 t2]
      (end-ts e1 t1)
      (end-ts e2 t2)
      (< t1 t2))))

(defne ends-after [e1 e2]
  ([e1 e2] (ends-before e2 e1)))

(defne starts-during [e1 e2]
  ([e1 e2] 
    (fresh [ts1 ts2 te2] 
      (begin-ts e1 ts1)
      (begin-ts e2 ts2)
      (end-ts e2 te2)
      (>= ts1 ts2)
      (>= te2 ts1)))))

(defne ends-during [e1 e2]
  ([e1 e2] 
    (fresh [te1 ts2 te2] 
      (end-ts e1 te1)
      (begin-ts e2 ts2)
      (end-ts e2 te2)
      (>= te1 ts2)
      (>= te2 te1)))))

(defne during [e1 e2]
  ([e1 e2] (starts-during e1 e2) (ends-during e1 e2)))

; this would really profit from some sort of :about modifier
; problem is: defne doesn't accept variable number of arguments ?
(defne until [e1 e2]
  ([e1 e2] (fresh [t]
    (end-ts e1 t)
    (begin-ts e2 t))))

(defne from-on [e1 e2]
  ([e1 e2] (until e2 e1)))

(defne synchronous [e1 e2]
  ([e1 e2]
    (fresh [ts te]
      (begin-ts e1 ts)
      (begin-ts e2 ts)
      (end-ts e1 te)
      (end-ts e2 te))))

(defne timepoint [e p]
  "Holds for events that have no temporal expansion."
  ([e p] (begin-ts e p) (end-ts e p)))

(defne timeline [e]
  "Holds for events that start before they end."
  ([e] 
    (fresh [ts te]
      (begin-ts e ts)
      (end-ts e te)
      (< ts te))))

(defne time-consistent [e]
  "Excludes situations where end time lies before starting time."
  ([e] (fresh [p] (timepoint [e p])))
  ([e] (timeline e)))
