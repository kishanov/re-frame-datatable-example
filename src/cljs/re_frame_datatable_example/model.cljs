(ns re-frame-datatable-example.model
  (:require [cljs.spec :as s]
            [cljs.spec.impl.gen :as gen]))


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
(s/def ::email
  (s/with-gen
    (s/and string? #(re-matches email-regex %))
    #(gen/fmap (fn [[fname lname domain]]
                 (str (clojure.string/lower-case fname)
                      "_"
                      (clojure.string/lower-case lname)
                      "@"
                      domain))
               (gen/tuple (s/gen ::first-name) (s/gen ::last-name) (s/gen ::domain)))))
