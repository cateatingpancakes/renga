package com.cateatingpancakes.server;

import java.util.Scanner;

import com.cateatingpancakes.engine.GameAction;
import com.cateatingpancakes.engine.PlayerRequest;
import com.cateatingpancakes.engine.RiichiEngine4P;
import com.cateatingpancakes.hand.BasicHand;

public class RengaServer 
{
    public static void main(String[] args)
    {
        try (Scanner sc = new Scanner(System.in)) {
            RiichiEngine4P reng = new RiichiEngine4P();
            
            for(int i = 0; i < 100; i++)
            {
                System.out.println(reng.getMoment());
                
                int hno = 1;
                for(BasicHand hand : reng.directState().directHands())
                {
                    hand.sort();
                    reng.buildRequest();
                    System.out.print("#" + hno + ": ");
                    for(int j = 0; j < hand.size(); j++)
                        System.out.print("[" + j + "=" + hand.get(j) + "]");
                    System.out.println();
                    hno++;
                }
                
                for(PlayerRequest pr : reng.directRequest())
                {
                    int idx = 0;
                    System.out.println(pr);
                    for(GameAction la : pr.getLegalActions())
                    {
                        System.out.println("idx=" + idx + ": " + la);
                        idx++;
                    }
                }
                
                int a1, a2, a3, a4, cs1, cs2, cs3, cs4;
                a1 = sc.nextInt();
                a2 = sc.nextInt();
                a3 = sc.nextInt();
                a4 = sc.nextInt();
                cs1 = sc.nextInt();
                cs2 = sc.nextInt();
                cs3 = sc.nextInt();
                cs4 = sc.nextInt();
                
                reng.selectCall(cs1, 0);
                reng.selectCall(cs2, 1);
                reng.selectCall(cs3, 2);
                reng.selectCall(cs4, 3);
                
                if(reng.directRequest().directRequest(0).size() > 0)
                    reng.directRequest().directRequest(0).chooseAction(a1);
                
                if(reng.directRequest().directRequest(1).size() > 0)
                    reng.directRequest().directRequest(1).chooseAction(a2);
                
                if(reng.directRequest().directRequest(2).size() > 0)
                    reng.directRequest().directRequest(2).chooseAction(a3);
                
                if(reng.directRequest().directRequest(3).size() > 0)
                    reng.directRequest().directRequest(3).chooseAction(a4);
                
                reng.advance();
            }
        }
    }
}
