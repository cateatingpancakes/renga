package com.cateatingpancakes.engine;

import com.cateatingpancakes.state.RiichiState3P;

public class RiichiEngine3P extends RiichiEngine<RiichiState3P>
{
    /**
     * Constructs a new 4-player Riichi engine on a random state.
     */
    public RiichiEngine3P()
    {
        this.state = new RiichiState3P();
    }

    @Override
    public void advance() 
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'advance'");
    }
}
