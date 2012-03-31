(ns herpderp.test.core
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic :as logic])
  (:use [herpderp.core]
        [midje.sweet]))

(facts "about helper functions"
  (def se-target [])
  (defn se-fu [item i]
    (def se-target (conj se-target (* item i))))
  (giaku-foreach [#(se-fu % 1) #(se-fu % 2) #(se-fu % 3)] 2)
  se-target => [2 4 6])

(fact "about defining and running rules"
  (defrule foobar [a b] (logic/== a 4) (logic/conso a [] b))
  (foobar) => '({:data [4 (4)], :success :pass}))
