
; have a sequence of our-goals, sitting in an atom, or a ref
; one can add to it

; goals contain entries like this: 
; ((== q# ~head) ~@body))


; (defmacro apply-macro [nom body]
;   `(~nom ~@body))
; (let [cf (apply-macro conde )]


(defmacro update-multo-fn [nom]
  (let [goalset (meta nom :goalset)
        args (meta nom :arg-spec)]
    `(defne ~nom ~args ~@goalset)))
  ;(swap! (meta nom :goalfun) (meta nom :reltype))) ;; argh too tired, make it so that it behaves like a defne possibly use core.logic legacy
; args?

(defmacro defmulte [nom args]
  ; create the empty atom-set of goals
  ; output a defn which retrieves the
  ; cached goals
  `(do
    (def ^{:goalset #{} :arg-spec ~args} ~nom)
    (update-multo-fn ~nom)))

(defn defmetho [nom & body]
  (swap! (meta nom :goalset) into body)
  (update-multo-fn nom))