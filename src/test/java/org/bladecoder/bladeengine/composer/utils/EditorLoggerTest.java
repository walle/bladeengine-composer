package org.bladecoder.bladeengine.composer.utils;

import org.junit.Test;
import static org.junit.Assert.*;

// TODO: Replace dummy test with real tests

public class EditorLoggerTest {
  @Test
  public void toggleChangesBetweenDebugAndError() {
    EditorLogger.level = EditorLogger.Levels.DEBUG;
    assertEquals(EditorLogger.Levels.DEBUG, EditorLogger.level);
    EditorLogger.toggle();
    assertEquals(EditorLogger.Levels.ERROR, EditorLogger.level);
    EditorLogger.toggle();
    assertEquals(EditorLogger.Levels.DEBUG, EditorLogger.level);
  }
}
