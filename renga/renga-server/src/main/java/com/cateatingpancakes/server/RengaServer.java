package com.cateatingpancakes.server;

import com.cateatingpancakes.engine.EngineRequest;
import com.cateatingpancakes.engine.GameAction;
import com.cateatingpancakes.engine.PlayerRequest;
import com.cateatingpancakes.engine.RiichiEngine4P;

public class RengaServer 
{
    public static void main(String[] args)
    {
        for(int i = 0; i < 100000; i++)
        {
            RiichiEngine4P reng = new RiichiEngine4P();

            reng.buildRequest();

            EngineRequest er = reng.directRequest();
            boolean hasKans = false;

            for(PlayerRequest pr : er)
                for(GameAction ga : pr)
                    if(ga.type() == GameAction.ActionType.KAN_CLOSED)
                        hasKans = true;

            if(hasKans)
            {
                System.out.println(reng.directState().directHands().get(0));
                for(PlayerRequest pr : er)
                    for(GameAction ga : pr)
                        System.out.println(ga);
            }
        }
    }
}
