# re-frame-datatable-example

A [re-frame](https://github.com/Day8/re-frame) application that shows most of the capabilities of [re-frame-datatable](https://github.com/kishanov/re-frame-datatable)
using GMail-like interface as an example.

## Features

* Uses most of the features of DataTable as described at [Documentation Website](https://kishanov.github.io/re-frame-datatable/)
* Uses [clojure.spec](http://clojure.org/guides/spec) to define underlying model and to generate sample data for the Table
* Uses [Semantic UI](http://semantic-ui.com/) for most of the CSS needs

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```
