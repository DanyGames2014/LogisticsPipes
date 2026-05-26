package net.danygames2014.logisticspipes.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Keyboard;

public class TextInputWidget extends DrawContext {
    private final TextRenderer textRenderer;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private String textBeforeCursor = "";
    private String textAfterCursor = "";

    private boolean isFocused = false;
    private boolean displayCursor = true;
    private long lastCursorBlinkTime = 0;

    private static final int COLOR_BLACK = 0xFF000000;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_DARK_GREY = 0xFF555555;

    public TextInputWidget(TextRenderer textRenderer, int x, int y, int width, int height) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getText() {
        return textBeforeCursor + textAfterCursor;
    }

    public void setText(String text) {
        this.textBeforeCursor = text;
        this.textAfterCursor = "";
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public void render() {
        if (isFocused) {
            fill(x, y, x + width, y + height, COLOR_BLACK);
            fill(x + 1, y + 1, x + width - 1, y + height - 1, COLOR_WHITE);
        } else {
            fill(x + 1, y + 1, x + width - 1, y + height - 1, COLOR_BLACK);
        }
        fill(x + 2, y + 2, x + width - 2, y + height - 2, COLOR_DARK_GREY);

        textRenderer.draw(getText(), x + 5, y + 5, COLOR_WHITE);

        if (isFocused) {
            if (System.currentTimeMillis() - lastCursorBlinkTime > 500) {
                displayCursor = !displayCursor;
                lastCursorBlinkTime = System.currentTimeMillis();
            }
            if (displayCursor) {
                int cursorX = x + 5 + textRenderer.getWidth(textBeforeCursor);
                fill(cursorX, y + 3, cursorX + 1, y + height - 4, COLOR_WHITE);
            }
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clickedInside = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
        if (clickedInside) {
            isFocused = true;
            if (button == 1) {
                textBeforeCursor = "";
                textAfterCursor = "";
            }
            return true;
        } else {
            isFocused = false;
            return false;
        }
    }

    public boolean keyPressed(char character, int keyCode) {
        if (!isFocused) return false;

        displayCursor = true;
        lastCursorBlinkTime = System.currentTimeMillis();

        boolean isCtrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        switch (keyCode) {
            case Keyboard.KEY_RETURN:
            case Keyboard.KEY_ESCAPE:
                isFocused = false;
                return true;

            case Keyboard.KEY_BACK:
                if (!textBeforeCursor.isEmpty()) {
                    if (isCtrl) {
                        if (textBeforeCursor.endsWith(" ")) {
                            textBeforeCursor = textBeforeCursor.replaceAll("[ \\t]+$", "");
                        }
                        int lastSpace = textBeforeCursor.lastIndexOf(' ');
                        textBeforeCursor = (lastSpace == -1) ? "" : textBeforeCursor.substring(0, lastSpace + 1);
                    } else {
                        textBeforeCursor = textBeforeCursor.substring(0, textBeforeCursor.length() - 1);
                    }
                }
                return true;

            case Keyboard.KEY_LEFT:
                if (!textBeforeCursor.isEmpty()) {
                    if (isCtrl) {
                        String trimmed = textBeforeCursor.replaceAll("[ \\t]+$", "");
                        int lastSpace = trimmed.lastIndexOf(' ');
                        int cutIndex = (lastSpace == -1) ? 0 : lastSpace;
                        textAfterCursor = textBeforeCursor.substring(cutIndex) + textAfterCursor;
                        textBeforeCursor = textBeforeCursor.substring(0, cutIndex);
                    } else {
                        textAfterCursor = textBeforeCursor.substring(textBeforeCursor.length() - 1) + textAfterCursor;
                        textBeforeCursor = textBeforeCursor.substring(0, textBeforeCursor.length() - 1);
                    }
                }
                return true;

            case Keyboard.KEY_RIGHT:
                if (!textAfterCursor.isEmpty()) {
                    if (isCtrl) {
                        String trimmed = textAfterCursor.replaceAll("^[ \\t]+", "");
                        int nextSpace = trimmed.indexOf(' ');
                        int spacesSkipped = textAfterCursor.length() - trimmed.length();
                        int cutIndex = (nextSpace == -1) ? textAfterCursor.length() : (nextSpace + spacesSkipped);
                        textBeforeCursor += textAfterCursor.substring(0, cutIndex);
                        textAfterCursor = textAfterCursor.substring(cutIndex);
                    } else {
                        textBeforeCursor += textAfterCursor.substring(0, 1);
                        textAfterCursor = textAfterCursor.substring(1);
                    }
                }
                return true;

            case Keyboard.KEY_HOME:
                textAfterCursor = textBeforeCursor + textAfterCursor;
                textBeforeCursor = "";
                return true;

            case Keyboard.KEY_END:
                textBeforeCursor = textBeforeCursor + textAfterCursor;
                textAfterCursor = "";
                return true;

            case Keyboard.KEY_DELETE:
                if (!textAfterCursor.isEmpty()) {
                    textAfterCursor = textAfterCursor.substring(1);
                }
                return true;

            default:
                if (keyCode == Keyboard.KEY_V && isCtrl) {
                    textBeforeCursor += Screen.getClipboard();
                    return true;
                }

                if (Character.isLetterOrDigit(character) || character == ' ') {
                    if (textRenderer.getWidth(textBeforeCursor + character + textAfterCursor) <= (width - 10)) {
                        textBeforeCursor += character;
                    }
                    return true;
                }
                break;
        }
        return false;
    }
}
