package com.cateatingpancakes.hand;

import java.util.ArrayList;

import com.cateatingpancakes.tile.TileMeld;

/**
 * This interface's contract is fulfilled if the implementor provides the required methods for
 * a splittable object to be parsed by a HandSplitter, that is, a method toCountArray() to transform
 * the object into a frequency array, and a getter for the open melds already recorded with the object.
 */
public interface Splittable 
{
    /**
     * Transforms the non-melded part of the object into a frequency array of tile index numbers.
     * @return The array of counts.
     */
    public int[] toCountArray();
    
    /**
     * Returns the open/visible melds of the object.
     * @return An ArrayList of open melds.
     */
    public ArrayList<TileMeld> getOpenMelds();
}
