package com.cateatingpancakes.tile;

public class IllegalMeldUpgrade extends IllegalStateException 
{
    /**
     * Constructs an IllegalMeldUpgrade exception object to be thrown if an attempt is made to modify a TileMeld into
     * a shouminkan with the wrong added tile or if the tile meld is not a pon.
     * @param message The exception message.
     */
    public IllegalMeldUpgrade(String message) 
    {
        super(message);
    }
}