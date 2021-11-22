# raycenity-misc

Various utils which happen to be very common in my side-projects.

Note that the utils I describe below are not all of them.

## misc.observer

A lightweight API for the observer pattern.

Observers subscribe to `Observable` with lambdas, and they will automatically unsubscribe if garbage collected.

`Publisher` is an `Observable` which you can publish events to. Generally a class will store a `Publisher` as a private field and expose it down-casting to an `Observable`.

Supports multiple observer priorities in case you want to guarantee that some observers are notified before others. Contains observable and reactive property delegates. Contains an observable list and set.

## misc.enm

A map whose keys are enums, internally backed by an array. It's not partial AKA all keys have values unless Value is optional.

Also provides an enum-set which wrap's Java's, and enum helpers.

## String escape and unescape

Copied from Stack Overflow:

https://stackoverflow.com/questions/2406121/how-do-i-escape-a-string-in-java

https://stackoverflow.com/questions/3537706/how-to-unescape-a-java-string-literal-in-java
