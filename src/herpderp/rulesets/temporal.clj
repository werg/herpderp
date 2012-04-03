(ns herpderp.rulesets.temporal
  (:refer-clojure :exclude [== = > < >= <=])
  (:use [herpderp.core]
        [clojure.core.logic]
        [clojure.core.logic.arithmetic]))

; implements all the before / after during stuff
; make it so that it's extensible to various event formats

; basic relations are
; (precedes e1 e2)
; (coincide e1 e2)
; todo: how to include :about n ? (metadata?)

; probably these relations are driven by a partial order
; so precedes is a transitive relation

; define a basic relation
(defrel t< e1 e2)

(defn precedes [e1 e2]
  "A transitive relation over temporal precedence."
  (conde
    [(t< e1 e2)]
    [(fresh [e3]
      (t< e1 e3) 
      (precedes e3 e2))]))

(defn precincides [e1 e2]
  (conde
    [precedes e1 e2]
    [coincide e1 e2]))

; -----------------------------------------------------------
; relations of states

(defn before [s1 s2]
  (fresh [e1 e2] 
    (end s1 e1)
    (begin s2 e2)
    (precedes e1 e2)))

(defn after [s1 s2]
  (before s2 s1))


(defn starts-before [s1 s2]
  (fresh [e1 e2]
    (begin s1 e1)
    (begin s2 e2)
    (precedes e1 e2)))

(defn starts-after [s1 s2]
  (starts-before s2 s1))

(defn ends-before [s1 s2]
  (fresh [e1 e2]
    (end s1 e1)
    (end s2 e2)
    (precedes e1 e2)))

(defn ends-after [s1 s2]
  (ends-before s2 s1))

(defn starts-during [s1 s2]
  (fresh [es1 es2 ee2] 
    (begin s1 es1)
    (begin s2 es2)
    (end s2 ee2)
    (precincides es2 es1)
    (precincides es1 ee2)))

(defn ends-during [s1 s2]
  (fresh [ee1 es2 ee2] 
    (end s1 ee1)
    (begin s2 es2)
    (end s2 ee2)
    (precincides es2 ee1)
    (precincides ee1 ee2)))

(defn during [s1 s2]
  (starts-during s1 s2) (ends-during s1 s2))

; this would really profit from some sort of :about modifier
; problem is: defne doesn't accept variable number of arguments ?
; we might want to put :about into the metadata
(defn until [s1 s2]
  (fresh [e1 e2]
    (end s1 e1)
    (begin s2 e2)
    (coincide e1 e2)))

(defn from-on [s1 s2]
  (until s2 s1))

(defn synchronous [s1 s2]
  (fresh [es1 es2 ee1 ee2]
    (begin s1 es1)
    (begin s2 es2)
    (end s1 ee1)
    (end s2 ee2)
    (coincide es1 es2)
    (coincide ee1 ee2)))

(defn timepoint [s]
  "Holds for states that have no temporal expansion."
  (fresh [es ee]
    (begin s es)
    (end s ee)
    (coincide ee es)))

(defn timeline [s]
  "Holds for states that start before they end."
  (fresh [es ee]
    (begin s es)
    (end s ee)
    (precedes es ee)))

(defn time-consistent [s]
  "Excludes situations where end time lies before starting time."
  (conde
    [(timepoint s)]
    [(timeline s)])