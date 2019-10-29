
This is an example of utilizing two specific useful features of Logback logging:
* Reverse exception logging
* Filtering out stackframes by package name from the stack trace

We have an application with a simple call heirarchy:

* `com.sheerid.a.Application.main()` calls
  * `com.sheerid.b.B.doit()`, which calls
    * `com.sheerid.c.C.doit()`, which throws an `IllegalStateException`
  * and catches the exception and wraps it in a `RuntimeException`
* and catches the exception and uses a Logback logger to log it as an error with the underlying cause.
    
With no customization of the logger, the output would look like the following:

```
ERROR - our message
java.lang.RuntimeException: error calling C.doit
	at com.sheerid.b.B.doit(B.java:10)
	at com.sheerid.a.Application.main(Application.java:13)
Caused by: java.lang.IllegalStateException: error message
	at com.sheerid.c.C.doit(C.java:5)
	at com.sheerid.b.B.doit(B.java:8)
	... 1 common frames omitted
```

By using Logback's `%rEx` syntax in the pattern, we can reverse the order of the reported exceptions, so that the 
deepest appears first:

```
ERROR - our message
java.lang.IllegalStateException: error message
	at com.sheerid.c.C.doit(C.java:5)
	at com.sheerid.b.B.doit(B.java:8)
	... 1 common frames omitted
Wrapped by: java.lang.RuntimeException: error calling C.doit
	at com.sheerid.b.B.doit(B.java:10)
	at com.sheerid.a.Application.main(Application.java:13)
```

And by filtering out stack frames from packages `com.sheerid.a`, and `com.sheerid.b`, we get:

```
ERROR - our message
java.lang.IllegalStateException: error message
	at com.sheerid.c.C.doit(C.java:5)
 [2 skipped]
	... 1 common frames omitted
Wrapped by: java.lang.RuntimeException: error calling C.doit
 [2 skipped]
```

Both of these are achieved by configuring the logging pattern in [the configuration file, logback.xml](src/main/resources/logback.xml)
