package com.vdurmont.emoji;

import org.junit.Ignore;
import org.junit.Test;

public class GenerateBinaryEmojisFileTools {

    @Test
    @Ignore
    public void transferEmojiJsonToBinaryFileTest(){
        // You have to copy the emojis.ser file to src/main/resources folder manually.
        EmojiManager emojiManager = new EmojiManager();
        emojiManager.transferEmojiJsonToBinaryFile();
    }
}
