HNBT
=====
A specification and implementation of Human NBT data. It can convert to and from HNBT and NBT. This library is particularly useful in creating readable tests against the NBT data format, since the data structure can be kept in another file and easily read.

[![Build Status](https://travis-ci.org/kenzierocks/HNBT.svg?branch=master)](https://travis-ci.org/kenzierocks/HNBT)
[![Code Coverage](https://codecov.io/github/kenzierocks/HNBT/coverage.svg?branch=master)](https://codecov.io/github/kenzierocks/HNBT?branch=master)

Format
=====
There are representations for every tag in Minecraft.
Here is the official but informal definition (formal definitions can be found by looking at the parser files):

## Key to definitions:

|Look|Meaning|
|----|-------|
|`<foo>`|Required `foo`|
|`[foo]`|Optional `foo`|
|`_`|Required Whitespace (` \t\r\n`)|
|` `|Optional Whitespace (` \t\r\n`)|

## Root Tag
The file must always start with a root tag. This tag looks like a Compound Tag, but it must have a `tag-name` of `root`.

## Tag Format

Tags (`<tag>`) are always laid out as follows.
The `tag-name` is required when inside a compound tag and required to be *not* present in other contexts.
The `tag-name` can be almost anything, but some special characters are not allowed. A full definition can be seen in the lexer.
Valid `tag-type`s are `compound`, `byte`, `byte-array`, `short`, `int`, `int-array`, `long`, `float`, `double`, `list`, and `string`.

```
<tag-type>_[tag-name] = <tag-value>
```

## Tag Values

### Compound Tag
Compound tag values look like this:

```
{
    <tag>,
    <tag>,
    <tag>,
    ....
}
```
There can be as many or as little `tag`s present, the only requirement is that they are separated by commas.

### List Tag
List tag values look like this:

```
[
    <tag>,
    <tag>,
    <tag>,
    ....
]
```
There can be as many or as little `tag`s present, the only requirement is that they are separated by commas.

### String Tag
String tag values look like any regular Java string literal, including double quotes.

### Byte, Short, Int, Long Tags
These tag values look like base 10 integers, and are limited to their respective type.

### Float, Double Tags
These tag values look like base 10 decimals or integers, and are limited to their respective type.

### Byte Array, Int Array Tags
These tag values look like the List Tag value, except instead of `<tag>` they use their respective values.

## Sample HNBT

```hnbt
compound root = {
    list list = [],
    byte byte = 127,
    byte-array byte-array = [],
    short short = 32767,
    int int = 2147483647,
    int-array int-array = [],
    long long = 9223372036854775807,
    float float = 42.0,
    double double = 42.5,
    string string = "\n to you too",
    compound compound = {
        string nested = "lvl up!"
    }
}
```
