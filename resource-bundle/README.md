# Java Resource Bundle Loading Performance

We were interested in the performance overhead of calling `ResourceBundle.getBundle()` each time a value is needed from 
the resource, as opposed to defining a constant (in situations where a locale is unnecessary).

## Experiment

The test code here creates an object that accesses a Resource bundle in three different ways:
1. `ResourceBundle` gets the resource bundle every time it is called (i.e. `ResourceBundle.getBundle(BUNDLE_NAME).get(KEY)`)
1. `StaticResourceBundle` constructs the `ResourceBundle` statically, and never constructs it again (and thus, won't pick up any changes).
1. `StaticResourceBundleWithCaching` constructs the `ResourceBundle` statically, with a 5-minute cache, using the `ResourceBundle.Control` caching mechanism.

It then calls each method 1 million times, and records the elapsed time.
And, it does that 100 times, to get a rough assessment of the variance.

## Results

|METHOD                         |AVERAGE|VARIANCE|MIN|MAX|
|-------------------------------|-------|--------|---|---|
|ResourceBundle                 | 137.84|+/- 54.5|131|240|
|StaticResourceBundle           |   7.51|+/-  3.5|  7| 14|
|StaticResourceBundleWithCaching|   8.15|+/-  4.0|  7| 15|

Note: There's a lot of variance each time you run it, so these are just approximates.

## Conclusion

Static construction makes a huge difference in performance. The ResourceBundle method is always about 15-20 times slower than the other methods.

Adding caching adds a relatively small amount of overhead, but provides the benefit that changes to config files will be picked up as soon as the cache expires.
