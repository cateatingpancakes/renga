package com.cateatingpancakes.hand;

import java.util.ArrayList;

import com.cateatingpancakes.tile.Tile;

/**
 * This interface's contract is fulfilled if the implementor provides the required methods
 * to determine the n-away number and to recognize all possible interpretations of a given
 * hand.
 */
public interface Splitter 
{
    /**
     * Finds the n-away number of a splittable.
     * @param hand The splittable.
     * @return The n-away number.
     */
    public int tilesAway(Splittable hand);

    /**
     * Interprets a splittable in standard form.
     * @param hand The splittable.
     * @return An ArrayList of all its interpretations.
     */
    public ArrayList<HandInterpretation> interpret(Splittable hand);

    /**
     * Interprets a splittable as thirteen orphans.
     * @param hand The splittable.
     * @return True, if the splittable is thirteen orphans.
     */
    public boolean interpretOrphans(Splittable hand);

    /**
     * Interprets a splittable as seven pairs.
     * @param hand The splittable.
     * @return True, if the splittable is seven pairs.
     */
    public boolean interpretPairs(Splittable hand);

    /**
     * Determines the index numbers of the waits of a splittable.
     * @param hand The splittable.
     * @return An ArrayList of index numbers of the tiles the splittable is waiting on.
     * If the splittable isn't waiting on anything, returns an empty ArrayList.
     */
    public default ArrayList<Integer> waits(Splittable hand)
    {
        ArrayList<Integer> waitTiles = new ArrayList<>();

        // If not tenpai, empty wait set.
        if(tilesAway(hand) == 0)
        {
            for(int i = 0; i < Tile.INDEX_NUMBER_ALG_MAX; i++)
                if(tilesAway(hand.addNew(Tile.fromIndex(i))) == -1)
                    waitTiles.add(i);
        }

        return waitTiles;
    }
}
