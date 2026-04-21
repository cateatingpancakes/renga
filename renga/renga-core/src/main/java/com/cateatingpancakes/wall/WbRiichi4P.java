package com.cateatingpancakes.wall;

import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;

public class WbRiichi4P extends WbRiichi 
{
    public static final int[] DEFAULT_RED_COPIES  = {1, 2, 1, 0};
    public static final int[] DEFAULT_RED_NUMBERS = {4, 4, 4, 4};

    /**
     * 4-player Riichi wall builder strategy. Defaults to the most common yonma rules for red tiles.
     */
    public WbRiichi4P()
    {
        this(DEFAULT_RED_COPIES, DEFAULT_RED_NUMBERS);
    }

    /**
     * 4-player Riichi wall builder strategy with an arbitrary red tiles setting.
     * @param redCopies An array in MPSZ suit order counting how many red copies each suit has.
     * @param redNumbers An array in MPSZ suit order specifying which tile is the unique red tile in each suit.
     */
    public WbRiichi4P(int[] redCopies, int[] redNumbers)
    {
        super(redCopies, redNumbers);
    }

    /**
     * Builds a 4-player Riichi wall based on the red tiles setting this object was constructed with.
     * @return The wall.
     */
    @Override
    public ArrayList<Tile> buildWall() 
    {
        // Wall to be returned
        ArrayList<Tile> wall = new ArrayList<>();

        for(Tile.TileType tileType : new Tile.TileType[]{Tile.TileType.MANZU, 
                                                         Tile.TileType.PINZU, 
                                                         Tile.TileType.SOUZU,
                                                         Tile.TileType.HONOR})
        {
            int[] numbers;
            int   suitIndex;

            switch(tileType)
            {
                // Set the suit index and define a set of tile numbers for each suit
                case Tile.TileType.MANZU -> {
                    suitIndex = 0;
                    numbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
                }
                case Tile.TileType.PINZU -> {
                    suitIndex = 1;
                    numbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
                }
                case Tile.TileType.SOUZU -> {
                    suitIndex = 2;
                    numbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
                }
                case Tile.TileType.HONOR -> {
                    suitIndex = 3;
                    numbers = new int[]{0, 1, 2, 3, 4, 5, 6};
                }
                default -> throw new AssertionError("Could not resolve tile type " + tileType);
            }

            addCopies(wall, tileType, suitIndex, numbers);
        }

        return wall;
    }
}
