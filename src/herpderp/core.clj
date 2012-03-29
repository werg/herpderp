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

