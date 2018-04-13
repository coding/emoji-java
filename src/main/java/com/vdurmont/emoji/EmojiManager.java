package com.vdurmont.emoji;

import java.io.*;
import java.util.*;

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
public class EmojiManager {
  private static final String PATH = "/emojis.json";
  private static final String EMOJIS_SER_FILE = "emojis.ser";
  private static final Map<String, Emoji> EMOJIS_BY_ALIAS =
    new HashMap<String, Emoji>();
  private static final Map<String, Set<Emoji>> EMOJIS_BY_TAG =
    new HashMap<String, Set<Emoji>>();
  private static List<Emoji> allEmojis = null;
  private static EmojiTrie emojiTrie = null;

  public EmojiManager() {
      if(allEmojis == null){
          List<Emoji> emojis = loadDataFromBinaryToEmojiList();
          allEmojis = emojis;
          for (Emoji emoji : emojis) {
              for (String tag : emoji.getTags()) {
                  if (EMOJIS_BY_TAG.get(tag) == null) {
                      EMOJIS_BY_TAG.put(tag, new HashSet<Emoji>());
                  }
                  EMOJIS_BY_TAG.get(tag).add(emoji);
              }
              for (String alias : emoji.getAliases()) {
                  EMOJIS_BY_ALIAS.put(alias, emoji);
              }
          }
          emojiTrie = new EmojiTrie(emojis);
      }
  }

  /**
   * Returns all the {@link com.vdurmont.emoji.Emoji}s for a given tag.
   *
   * @param tag the tag
   *
   * @return the associated {@link com.vdurmont.emoji.Emoji}s, null if the tag
   * is unknown
   */
  public static Set<Emoji> getForTag(String tag) {
    if (tag == null) {
      return null;
    }
    return EMOJIS_BY_TAG.get(tag);
  }

  /**
   * Returns the {@link com.vdurmont.emoji.Emoji} for a given alias.
   *
   * @param alias the alias
   *
   * @return the associated {@link com.vdurmont.emoji.Emoji}, null if the alias
   * is unknown
   */
  public static Emoji getForAlias(String alias) {
    if (alias == null) {
      return null;
    }
    return EMOJIS_BY_ALIAS.get(trimAlias(alias));
  }

  private static String trimAlias(String alias) {
    String result = alias;
    if (result.startsWith(":")) {
      result = result.substring(1, result.length());
    }
    if (result.endsWith(":")) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }


  /**
   * Returns the {@link com.vdurmont.emoji.Emoji} for a given unicode.
   *
   * @param unicode the the unicode
   *
   * @return the associated {@link com.vdurmont.emoji.Emoji}, null if the
   * unicode is unknown
   */
  public static Emoji getByUnicode(String unicode) {
    if (unicode == null) {
      return null;
    }
    return emojiTrie.getEmoji(unicode);
  }

  /**
   * Returns all the {@link com.vdurmont.emoji.Emoji}s
   *
   * @return all the {@link com.vdurmont.emoji.Emoji}s
   */
  public static Collection<Emoji> getAll() {
    return allEmojis;
  }

  /**
   * Tests if a given String is an emoji.
   *
   * @param string the string to test
   *
   * @return true if the string is an emoji's unicode, false else
   */
  public static boolean isEmoji(String string) {
    if (string == null) return false;

    EmojiParser.UnicodeCandidate unicodeCandidate = EmojiParser.getNextUnicodeCandidate(string.toCharArray(), 0);
    return unicodeCandidate != null &&
            unicodeCandidate.getEmojiStartIndex() == 0 &&
            unicodeCandidate.getFitzpatrickEndIndex() == string.length();
  }

  /**
   * Tests if a given String only contains emojis.
   *
   * @param string the string to test
   *
   * @return true if the string only contains emojis, false else
   */
  public static boolean isOnlyEmojis(String string) {
    return string != null && EmojiParser.removeAllEmojis(string).isEmpty();
  }

  /**
   * Checks if sequence of chars contain an emoji.
   * @param sequence Sequence of char that may contain emoji in full or
   * partially.
   *
   * @return
   * &lt;li&gt;
   *   Matches.EXACTLY if char sequence in its entirety is an emoji
   * &lt;/li&gt;
   * &lt;li&gt;
   *   Matches.POSSIBLY if char sequence matches prefix of an emoji
   * &lt;/li&gt;
   * &lt;li&gt;
   *   Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an
   *   emoji
   * &lt;/li&gt;
   */
  public static EmojiTrie.Matches isEmoji(char[] sequence) {
    return emojiTrie.isEmoji(sequence);
  }

  /**
   * Returns all the tags in the database
   *
   * @return the tags
   */
  public static Collection<String> getAllTags() {
    return EMOJIS_BY_TAG.keySet();
  }

  private static List<Emoji> loadDataFromJsonToEmojiList(){
      List<Emoji> emojis = new ArrayList<Emoji>();
      try {
          InputStream stream = EmojiLoader.class.getResourceAsStream(PATH);
          emojis = EmojiLoader.loadEmojis(stream);
          stream.close();
      } catch (IOException e) {
          e.printStackTrace();
      }
      return emojis;
  }

  private static List<Emoji> loadDataFromBinaryToEmojiList(){
      List<Emoji> emojis = new ArrayList<Emoji>();
      try {
          InputStream fileIn = EmojiLoader.class.getResourceAsStream("/" + EMOJIS_SER_FILE);
          ObjectInputStream in = new ObjectInputStream(fileIn);
          emojis = (List<Emoji>) in.readObject();
          in.close();
          fileIn.close();
      } catch (Exception e) {
          e.printStackTrace();
      }
      return emojis;
  }

  public static void transferEmojiJsonToBinaryFile(){
      List<Emoji> emojis = loadDataFromJsonToEmojiList();
      try {
          FileOutputStream fileout = new FileOutputStream(EMOJIS_SER_FILE);
          ObjectOutputStream out = new ObjectOutputStream(fileout);
          out.writeObject(emojis);
          out.close();
          fileout.close();
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      } catch (IOException e){
          e.printStackTrace();
      }
  }

}
