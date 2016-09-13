(ns boot.new.readux_starter
  (:require [boot.new.templates :refer [renderer year project-name
                                        ->files sanitize-ns name-to-path
                                        multi-segment]]))

(def render (renderer "readux-starter"))

(defn readux-starter
  "A readux starter template."
  [name]
  (let [main-ns (multi-segment (sanitize-ns name))
        data {:name (project-name name)
              :namespace main-ns
              :sanitized (name-to-path main-ns)
              :year (year)}]
    (println "Generating a new project called" name "based on the 'readux-starter' template.")
    (->files data
             ["README.md" (render "README.md" data)]
             ["build.boot" (render "build.boot" data)]
             [".gitignore" (render "gitignore" data)]
             ["LICENSE" (render "LICENSE" data)]
             ["src/cljs/{{nested-dirs}}/core.cljs" (render "core.cljs" data)]
             ["src/cljc/{{nested-dirs}}/core.cljs" (render "core.cljc" data)]
             ["public/index.html" (render "index.html" data)]
             ["public/css/style.css" (render "style.css" data)])))