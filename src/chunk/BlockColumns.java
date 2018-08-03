package chunk;

import org.joml.Vector3d;
import org.joml.Vector3i;
import util.IntMap;
import static util.MathUtils.AIR_COLOR;

public class BlockColumns {

    public final int size;
    private final IntMap[][] blockColumns;

    private boolean recomputeMinMax = true;
    private int minZ, maxZ;

    public BlockColumns(int size) {
        this.size = size;
        blockColumns = new IntMap[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                blockColumns[x][y] = new IntMap();
            }
        }
    }

    public boolean blockRangeEquals(int x, int y, int zMin, int zMax, int color) {
        return columnAt(x, y).rangeSize(zMin, zMax - 1) == 0 && getBlock(x, y, zMax) == color;
    }

    private IntMap columnAt(int x, int y) {
        return blockColumns[x + 1][y + 1];
    }

    private static int columnValueAt(IntMap c, int z) {
        if (c.isEmpty() || c.lastKey() < z) {
            return AIR_COLOR;
        } else {
            return c.ceilingValue(z);
        }
    }

    public int getBlock(int x, int y, int z) {
        return columnValueAt(columnAt(x, y), z);
    }

    public int getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
    }

    public int maxZ() {
        recomputeMinMax();
        return maxZ;
    }

    public int minZ() {
        recomputeMinMax();
        return minZ;
    }

    private void recomputeMinMax() {
        if (recomputeMinMax) {
            minZ = Integer.MAX_VALUE;
            maxZ = Integer.MIN_VALUE;
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    if (!blockColumns[x][y].isEmpty()) {
                        minZ = Math.min(minZ, blockColumns[x][y].firstKey());
                        maxZ = Math.max(maxZ, blockColumns[x][y].lastKey());
                    }
                }
            }
            recomputeMinMax = false;
        }
    }

    public void setBlock(int x, int y, int z, int color) {
        if (getBlock(x, y, z) != color) {
            int prevLowerColor = getBlock(x, y, z - 1);
            columnAt(x, y).put(z, color);
            if (prevLowerColor != color) {
                columnAt(x, y).put(z - 1, prevLowerColor);
            }
            recomputeMinMax = true;
        }
    }

    public void setBlockRange(int x, int y, int zMin, int zMax, int color) {
        int prevLowerColor = getBlock(x, y, zMin - 1);
        columnAt(x, y).clearRange(zMin, zMax);
        if (getBlock(x, y, zMax) != color) {
            columnAt(x, y).put(zMax, color);
        }
        if (prevLowerColor != color) {
            columnAt(x, y).put(zMin - 1, prevLowerColor);
        }
        recomputeMinMax = true;
    }

    public void setBlockRangeInfinite(int x, int y, int zMax, int color) {
        columnAt(x, y).clearRange(Integer.MIN_VALUE, zMax);
        if (getBlock(x, y, zMax) != color) {
            columnAt(x, y).put(zMax, color);
        }
        recomputeMinMax = true;
    }

    public void simplify() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                columnAt(x, y).simplify();
            }
        }
    }

    public boolean solid(Vector3i pos) {
        return getBlock(pos) != AIR_COLOR;
    }

    public boolean solid(Vector3d min, Vector3d max) {
        for (int x = Math.max(0, (int) Math.floor(min.x)); x <= Math.min(size - 3, (int) Math.ceil(max.x)); x++) {
            for (int y = Math.max(0, (int) Math.floor(min.y)); y <= Math.min(size - 3, (int) Math.ceil(max.y)); y++) {
                if (!blockRangeEquals(x, y, (int) Math.floor(min.z), (int) Math.ceil(max.z), AIR_COLOR)) {
                    return true;
                }
            }
        }
        return false;
    }

}
