(def version "0.1.4")

(task-options!
 pom {:project     'readux-starter/boot-template
      :version     version
      :description "readux starter template for Boot new"
      :url         "https://github.com/readux/readux-starter-template"
      :scm         {:url "https://github.com/readux/readux-starter-template"}
      :license     {"MIT" "https://mit-license.org/"}})

(set-env! :resource-paths #{"src"}
          :dependencies   '[[org.clojure/clojure   "RELEASE"]
                            [seancorfield/boot-new "RELEASE"]])

(deftask local
  "build & install jar into local (~/.m2) Maven repository"
  []
  (comp (pom) (jar) (install)))

;; These tasks are a simplification of what's found in
;; https://github.com/adzerk-oss/bootlaces/
(defn- get-clojars-creds []
  (mapv #(System/getenv %) ["CLOJARS_USER" "CLOJARS_PASS"]))

(deftask ^:private collect-clojars-creds
  []
  (let [[user pass] (get-clojars-creds)
        cred-map (atom {})]
    (if (and user pass)
      (swap! cred-map assoc :username user :password pass)
      (do  (println "CLOJARS_USER/CLOJARS_PASS UNSET")
           (print "Username: ")
           (#(swap! cred-map assoc :username %) (read-line))
           (print "Password: ")
           (#(swap! cred-map assoc :password %)
             (apply str (.readPassword (System/console))))))
    (merge-env! 
      :repositories 
      [["deploy-clojars"
        (merge @cred-map {:url "https://clojars.org/repo"})]])))

(deftask clojars
  "Build & push to library to clojars Maven repository"
  []
  (collect-clojars-creds)
  (comp (pom) (jar) (push :repo "deploy-clojars")))