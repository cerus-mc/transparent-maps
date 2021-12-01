package dev.cerus.transparentmaps.nms;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

public interface NmsAdapter {

    int COLOR_WHITE = 8 * 4 + 2;
    int COLOR_BLACK = 29 * 4 + 2;

    default int getBlockBaseColor(final Block block) {
        return this.getBlockBaseColor(block.getBlockData());
    }

    int getBlockBaseColor(BlockData blockData);

    int getBlockRgbColor(BlockData blockData);

    void saveWorldMap(ItemStack itemStack, World world);

    void saveWorldMap(int mapId, World world);

    byte[] getWorldMapData(ItemStack itemStack, World world);

    byte[] getWorldMapData(int mapId, World world);

    boolean acceptsVersion(final int major, final int minor, final Integer patch);

    //MapColor[] getAvailableMapColors()

    default void drawTextCentered(final int x, final int z, final String text, final byte startColor, final int size, final byte[] data) {
        final int w = MinecraftFont.Font.getWidth(text);
        this.drawText(x - (w / 2), z - (MinecraftFont.Font.getHeight() / 2), text, startColor, size, data);
    }

    default void drawText(int x, int z, final String text, final byte startColor, final int size, final byte[] data) {
        final MapFont font = MinecraftFont.Font;

        final int xStart = x;
        byte color = startColor;
        if (!font.isValid(text)) {
            throw new IllegalArgumentException("text contains invalid characters");
        } else {
            int currentIndex = 0;

            while (true) {
                if (currentIndex >= text.length()) {
                    return;
                }

                final char ch = text.charAt(currentIndex);
                if (ch == '\n') {
                    // Increment z if the char is a line separator
                    x = xStart;
                    z += font.getHeight() + 1;
                } else if (ch == '\u00A7' /*-> §*/) {
                    // Get distance from current char to end char (';')
                    final int end = text.indexOf(';', currentIndex);
                    if (end < 0) {
                        break;
                    }

                    // Parse color
                    try {
                        color = Byte.parseByte(text.substring(currentIndex + 1, end));
                        currentIndex = end;
                    } catch (final NumberFormatException var12) {
                        break;
                    }
                } else {
                    // Draw text if the character is not a special character
                    final MapFont.CharacterSprite sprite = font.getChar(text.charAt(currentIndex));

                    for (int row = 0; row < font.getHeight(); ++row) {
                        for (int col = 0; col < sprite.getWidth(); ++col) {
                            if (sprite.get(row, col)) {
                                for (int eX = 0; eX < size; eX++) {
                                    for (int eZ = 0; eZ < size; eZ++) {
                                        final int theX = x + (size * col) + (eX);
                                        final int theZ = z + (size * row) + (eZ);
                                        data[theX + theZ * 128] = color;
                                    }
                                }
                            }
                        }
                    }

                    // Increment x
                    x += (sprite.getWidth() + 1) * size;
                }

                ++currentIndex;
            }

            throw new IllegalArgumentException("Text contains unterminated color string");
        }
    }

}
