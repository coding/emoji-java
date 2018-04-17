# emoji-java for Coding
This repository is derived from [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java).
Coding makes some change in this version to be used in product more suitable.

## How to build it?
The project uses maven as build tool. You can use `mvn clean install` to build jar.
After that, you can copy the jar under `target/` into your project libs.

## How to use it?
##### Via Gradle:
```gradle
compile files('libs/emoji-java-4.0.0-coding.jar')
```

## What change the Coding makes?
+ Use binary format to load emoji resource instead of json.
+ Use constructor in EmojiManager instead of static function. The intent is to enable lazy loading for emoji resource.

## How to maintain if emojis resource change?
If `src/main/resource/emojis.json` changed, you can run `GenerateBinaryEmojisFileTools` to generate binary file. 
The `emoji.ser` will be generated under the root path. You should copy the file to `src/main/resource` manually.