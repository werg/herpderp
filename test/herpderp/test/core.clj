(ns herpderp.test.core
  (:refer-clojure :exclude [==])
  (:require [clojure.core.logic : as logic])
  (:use [herpderp.core]
        [midje.sweet]))

(fact "about defining and running rules"
  (defrule foobar a (logic/== a 4))
  (run-rule foobar 4) => {:item 4, :success :pass})
