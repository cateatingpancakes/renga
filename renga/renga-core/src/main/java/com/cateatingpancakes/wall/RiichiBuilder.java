package com.cateatingpancakes.wall;

import java.util.ArrayList;
import java.util.EnumSet;

import com.cateatingpancakes.tile.Tile;
import com.cateatingpancakes.tile.TileTrait;

public abstract class RiichiBuilder implements WallBuilder 
{
    // This is never parametrizable, and subclasses must never use a different tile copy value
    // The number of tile copies in all Riichi and Riichi-variant games, ever, is always exactly 4
    public static final int DEFAULT_TILE_COPIES = 4;

    /**
     * The maximum index number of Riichi tiles.
     */
    public static final int INDEX_NUMBER_RIICHI_MAX = 34;

    
    protected final int[] redCopies;
    protected final int[] redNumbers;

    /**
     * Constructor defining the red tiles that must be added by the Riichi wall builder.
     * @param redCopies An array in MPSZ suit order counting how many red copies each suit has.
     * @param redNumbers An array in MPSZ suit order specifying which tile is the unique red tile in each suit.
     */
    protected RiichiBuilder(int[] redCopies, int[] redNumbers)
    {
        this.redCopies  = redCopies;
        this.redNumbers = redNumbers;
    }

    /**
     * Internal helper method to add non-red and red copies to a passed ArrayList of tiles.
     * The operation of adding tiles is performed DEFAULT_TILE_COPIES times for each tile number, 
     * among an array numbers[] of tile numbers within a suit. The suit is given by a TileType and 
     * its suitIndex in the redCopies[] and redNumbers[] arrays stored in the WBRiichi object.
     * @param wall The ArrayList to be modified with the added tiles.
     * @param tileType The type/suit of the tiles.
     * @param suitIndex The index of the suit within the numbers[] array.
     * @param numbers An array of tile numbers for which to add tiles.
     */
    protected void addCopies(ArrayList<Tile> wall, Tile.TileType tileType, int suitIndex, int[] numbers)
    {
        // Loop variables, will count how many non-red and red 
        // tiles are to be added for each iteration/tile number.
        int black, red;

        for(int number : numbers)
        {
            // For most tiles, all non-red.
            black = DEFAULT_TILE_COPIES;
            red   = 0;

            // There is exactly one exception, for the red tile of the suit.
            if(number == redNumbers[suitIndex])
            {
                // If we fall in that case:
                // Switch out the needed non-red tiles;
                // add that many red tiles in exchange.
                black -= redCopies[suitIndex];
                red   += redCopies[suitIndex];
            }

            // Add non-red tiles.
            for(int i = 0; i < black; i++)
                wall.add(new Tile(tileType, number));

            // Then add red tiles, if any.
            // Loop just doesn't run if red == 0.
            for(int i = 0; i < red; i++)
                wall.add(new Tile(tileType, number, EnumSet.of(TileTrait.RED)));
        }
    }
}
