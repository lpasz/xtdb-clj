(ns db.core)


;; Earth 

;; The lib a require xtdb
(require '[xtdb.api :as xt])

;; Start a new node 
(def node (xt/start-node {}))

;; My Data
(def manifest
  {:xt/id :manifest
   :pilot-name "Lucas"
   :id/rocket "SB002-sol"
   :id/employee "22910x2"
   :badges "Setup"
   :cargo ["stereo" "gold fish" "slippers" "secret note"]})

;; Put the data on my node 
(xt/submit-tx node [;; Full version
                    ;; [::xt/put doc valid-time-start valid-time-end]
                    [::xt/put manifest]])

;; Sync my node
(xt/sync node)

;; Grab the manifest (id??) from my db/node
(xt/entity (xt/db node) :manifest)

;; Pluto

(xt/submit-tx
 node
 [[::xt/put
   {:xt/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}]

  [::xt/put
   {:xt/id :commodity/N
    :common-name "Nitrogen"
    :type :element/gas
    :density 1.2506
    :radioactive false}]

  [::xt/put
   {:xt/id :commodity/CH4
    :common-name "Methane"
    :type :molecule/gas
    :density 0.717
    :radioactive false}]])

(xt/sync node)

(xt/entity (xt/db node) :commodity/CH4)

;; Pu
(xt/submit-tx
 node
 [[::xt/put
   {:xt/id :stock/Pu
    :commod :commodity/Pu
    :weight-ton 21}
   #inst "2115-02-13T18"]

  [::xt/put
   {:xt/id :stock/Pu
    :commod :commodity/Pu
    :weight-ton 23}
   #inst "2115-02-14T18"]

  [::xt/put
   {:xt/id :stock/Pu
    :commod :commodity/Pu
    :weight-ton 22.2}
   #inst "2115-02-15T18"]

  [::xt/put
   {:xt/id :stock/Pu
    :commod :commodity/Pu
    :weight-ton 24}
   #inst "2115-02-18T18"]

  [::xt/put
   {:xt/id :stock/Pu
    :commod :commodity/Pu
    :weight-ton 24.9}
   #inst "2115-02-19T18"]])

(xt/sync node)

;; N and CH4

(xt/submit-tx
 node
 [[::xt/put
   {:xt/id :stock/N
    :commod :commodity/N
    :weight-ton 3}
   #inst "2115-02-13T18"
   #inst "2115-02-19T18"]

  [::xt/put
   {:xt/id :stock/CH4
    :commod :commodity/CH4
    :weight-ton 92}
   #inst "2115-02-15T18"
   #inst "2115-02-19T18"]])

(xt/entity (xt/db node #inst "2115-02-14") :stock/Pu)
(xt/entity (xt/db node #inst "2115-02-18") :stock/Pu)

(defn easy-ingest [node docs]
  (xt/submit-tx node
                (vec (for [doc docs]
                       [::xt/put doc])))
  (xt/sync node))

(easy-ingest node [{:xt/id :stock/Pu
                    :commod :commodity/Pu
                    :weight-ton 25.1}])

(xt/entity (xt/db node) :stock/Pu)

;; I'm done with pluto
(xt/submit-tx node [[::xt/put
                     {:xt/id :manifest
                      :pilot-name "Lucas"
                      :id/rocket "SB002-sol"
                      :id/employee "22910x2"
                      :badges ["SETUP" "PUT"]
                      :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

(xt/sync node)

(xt/entity (xt/db node) :manifest)

;; Mercury (see you space cowboy)

(defn easy-ingest [node docs]
  (xt/submit-tx node
                (vec (for [doc docs]
                          [::xt/put doc])))
  (xt/sync node))

(def data
  [{:xt/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}

   {:xt/id :commodity/N
    :common-name "Nitrogen"
    :type :element/gas
    :density 1.2506
    :radioactive false}

   {:xt/id :commodity/CH4
    :common-name "Methane"
    :type :molecule/gas
    :density 0.717
    :radioactive false}

   {:xt/id :commodity/Au
    :common-name "Gold"
    :type :element/metal
    :density 19.300
    :radioactive false}

   {:xt/id :commodity/C
    :common-name "Carbon"
    :type :element/non-metal
    :density 2.267
    :radioactive false}

   {:xt/id :commodity/borax
    :common-name "Borax"
    :IUPAC-name "Sodium tetraborate decahydrate"
    :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
    :type :mineral/solid
    :appearance "white solid"
    :density 1.73
    :radioactive false}])

(easy-ingest node data)

(xt/q (xt/db node)
      '{:find [element]
        :where [[element :type :element/metal]]})

(=
 (xt/q (xt/db node) '{:find [element] :where [[element :type :element/metal]]})
 (xt/q (xt/db node) {:find '[element] :where '[[element :type :element/metal]]})
 (xt/q (xt/db node) (quote {:find [element] :where [[element :type :element/metal]]})))

(xt/q (xt/db node)
      '{:find [name]
        :where [[e :type :element/metal]
                [e :common-name name]]})