package com.mygdx.game.curlygame;

import com.mygdx.game.Const;
import com.mygdx.game.obj.House;

import java.util.TimerTask;

public class GenerateAction extends TimerTask {
    public House house;

    public GenerateAction(House house) {
        this.house = house;
    }

   // final  Sheep sheep = house.getSheep();

        public void run()
        {
            int action = (int) (1 + ( Math.random() * 1 ));
            switch(action)
            {
                case 1:
                    float x = (float) (Const.W*5.2f + Math.random() * Const.W * 5.55f);//пойти по координате х
                    house.getSheep().go(x);
                    break;
            }
        }
}

