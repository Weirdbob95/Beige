package util;

import java.util.ArrayList;
import java.util.Arrays;

public class IntMap {

    private int[] keys, values;

    private int ceilingInd(int key) {
        if (isEmpty()) {
            return 0;
        }
        int ind = Arrays.binarySearch(keys, key);
        if (ind >= 0) {
            return ind;
        } else {
            return -ind - 1;
        }
        /*int lowerBound = -1, upperBound = size();
            while (upperBound > lowerBound + 1) {
                int check = (lowerBound + upperBound) / 2;
                if (key > check) {
                    lowerBound = check;
                } else {
                    upperBound = check;
                }
            }
            return upperBound;*/
    }

    public int ceilingValue(int key) {
        return values[ceilingInd(key)];
    }

    // Both the start and end are inclusive
    public void clearRange(int startKey, int endKey) {
        int startInd = ceilingInd(startKey), endInd = ceilingInd(endKey + 1);
        int rangeSize = endInd - startInd;
        if (rangeSize > 0) {
            int[] newKeys = new int[size() - rangeSize];
            int[] newValues = new int[size() - rangeSize];
            for (int i = 0; i < startInd; i++) {
                newKeys[i] = keys[i];
                newValues[i] = values[i];
            }
            for (int i = endInd; i < size(); i++) {
                newKeys[i - rangeSize] = keys[i];
                newValues[i - rangeSize] = values[i];
            }
            keys = newKeys;
            values = newValues;
        }
    }

    public int firstKey() {
        return keys[0];
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int lastKey() {
        return keys[size() - 1];
    }

    public void put(int key, int value) {
        int ind = ceilingInd(key);
        if (ind < size() && keys[ind] == key) {
            values[ind] = value;
        } else {
            int[] newKeys = new int[size() + 1];
            int[] newValues = new int[size() + 1];
            for (int i = 0; i < ind; i++) {
                newKeys[i] = keys[i];
                newValues[i] = values[i];
            }
            newKeys[ind] = key;
            newValues[ind] = value;
            for (int i = ind; i < size(); i++) {
                newKeys[i + 1] = keys[i];
                newValues[i + 1] = values[i];
            }
            keys = newKeys;
            values = newValues;
        }
    }

    // Both the start and end are inclusive
    public int rangeSize(int startKey, int endKey) {
        int startInd = ceilingInd(startKey), endInd = ceilingInd(endKey + 1);
        return endInd - startInd;
    }

    public void simplify() {
        if (size() < 2) {
            return;
        }
        ArrayList<Integer> keyList = new ArrayList(), valueList = new ArrayList();
        for (int i = 0; i < size(); i++) {
            if (i == size() - 1 || values[i] != values[i + 1]) {
                keyList.add(keys[i]);
                valueList.add(values[i]);
            }
        }
        keys = new int[keyList.size()];
        values = new int[keyList.size()];
        for (int i = 0; i < keyList.size(); i++) {
            keys[i] = keyList.get(i);
            values[i] = valueList.get(i);
        }
    }

    public int size() {
        return keys == null ? 0 : keys.length;
    }
}
