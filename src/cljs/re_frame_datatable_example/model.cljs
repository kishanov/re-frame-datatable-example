(ns re-frame-datatable-example.model
  (:require [cljs.spec :as s]
            [cljs.spec.impl.gen :as gen]
            [clojure.test.check.generators]))


(def first-names #{"James" "Mary" "John" "Patricia" "Robert" "Jennifer" "Michael" "Elizabeth" "William" "Linda"
                   "David" "Barbara" "Richard" "Susan" "Joseph" "Jessica" "Thomas" "Margaret" "Charles" "Sarah"
                   "Christopher" "Karen" "Daniel" "Nancy" "Matthew" "Betty" "Anthony" "Dorothy" "Donald" "Lisa"
                   "Mark" "Sandra" "Paul" "Ashley" "Steven" "Kimberly" "George" "Donna" "Kenneth" "Carol"})


(def last-names #{"Smith" "Johnson" "Williams" "Jones" "Brown" "Davis" "Miller" "Wilson" "Moore" "Taylor"
                  "Anderson" "Thomas" "Jackson" "White" "Harris" "Martin" "Thompson" "Garcia" "Martinez" "Robinson"
                  "Clark" "Rodriguez" "Lewis" "Lee" "Walker" "Hall" "Allen" "Young" "Hernandez" "King" "Wright"
                  "Lopez" "Hill" "Scott" "Green" "Adams" "Baker" "Gonzalez" "Nelson" "Carter"})


(def domains #{"gmail.com" "yahoo.com" "hotmail.com" "outlook.com" "inbox.com" "mail.com"})


(s/def ::first-name first-names)
(s/def ::last-name last-names)
(s/def ::domain domains)


(def email-regex #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")

(s/def ::email-address
  (s/with-gen
    (s/and string? #(re-matches email-regex %))
    #(gen/fmap (fn [[fname lname domain]]
                 (str (clojure.string/lower-case fname)
                      "_"
                      (clojure.string/lower-case lname)
                      "@"
                      domain))
               (gen/tuple (s/gen ::first-name) (s/gen ::last-name) (s/gen ::domain)))))


(def letters (set (map char (range 97 123))))


(s/def ::word
  (s/with-gen
    string?
    #(gen/fmap (fn [chars]
                 (clojure.string/join chars))
               (s/gen (s/coll-of letters :min-count 2 :max-count 7)))))


(s/def ::subject
  (s/with-gen
    string?
    #(gen/fmap (fn [words]
                 (->> words
                      (clojure.string/join \space)
                      (clojure.string/capitalize)))
               (s/gen (s/coll-of ::word :min-count 1 :max-count 4)))))


(s/def ::body
  (s/with-gen
    string?
    #(gen/fmap (fn [words]
                 (->> words
                      (clojure.string/join \space)
                      (clojure.string/capitalize)))
               (s/gen (s/coll-of ::word :min-count 1 :max-count 123)))))


(s/def ::from ::email-address)
(s/def ::to (s/coll-of ::email-address :min-count 1 :max-count 5 :distinct true))
(s/def ::date
  (s/with-gen
    #(instance? js/Date %)
    #(gen/fmap (fn [epoch]
                 (js/Date. (* (+ epoch (rand-int 20000000)) 1000)))
               (s/gen (s/int-in 1454104654 1482958036)))))


(s/def ::email
  (s/keys :req-un [::to ::from ::subject ::body ::date]))
(s/def ::thread (s/coll-of ::email :min-count 1 :max-count 9))


(def sample-inbox (gen/sample (s/gen ::thread) 42))
