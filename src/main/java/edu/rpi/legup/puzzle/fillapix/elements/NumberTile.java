package edu.rpi.legup.puzzle.fillapix.elements;

import edu.rpi.legup.model.elements.NonPlaceableElement;

public class NumberTile extends NonPlaceableElement {
    private int object_num;

    public NumberTile() {
        super(
                "FPIX-UNPL-0001",
                "Number Tile",
                "A numbered tile",
                "edu/rpi/legup/images/fillapix/tiles/NumberTile.png");
        object_num = 0;
    }

    /**
     * @return this object's tile number...
     */
    public int getTileNumber() {
        return object_num;
    }

    /**
     * @param num Amount to set tile object to.
     */
    public void setTileNumber(int num) {
        object_num = num;
    }
}
