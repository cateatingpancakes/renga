package com.cateatingpancakes.tile;

/**
 * This interface's contract is fulfilled if the implementor offers a method returning a corresponding String object representing it in MPSZ notation.
 */
public interface Notable 
{
    /**
     * Returns the MPSZ notation of an object.
     * @return The MPSZ notation.
     */
    public String getMPSZNotation();
}
