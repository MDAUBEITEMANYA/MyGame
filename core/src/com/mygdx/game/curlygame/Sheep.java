package com.mygdx.game.curlygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.Const;
import com.mygdx.game.MusicManager;
import com.mygdx.game.MyAnimation;
import com.mygdx.game.TextureManager;
import com.mygdx.game.obj.House;
import com.mygdx.game.obj.Inventar;
import com.mygdx.game.obj.RoomSubject;
import com.mygdx.game.obj.Subject;
import com.mygdx.game.obj.panels.BottomPanel;
import com.mygdx.game.shit.BigListener;
import com.mygdx.game.shit.level.LevelManager;
import com.mygdx.game.utils.AnimationUtils;
import com.mygdx.game.utils.FillType;
import com.mygdx.game.utils.PhraseUtils;

import org.json.JSONObject;
import com.mygdx.game.obj.StatableHouseObject;

public class Sheep extends StatableHouseObject<Sheep.State> {

    private State currentState;
    private State previousState;
    private Animation sheepWalk;
    private Animation sheepSleep;
    private boolean turnRight;
    private boolean visible;
    private Inventar inventar;
    public MyAnimation walkAnimation;
    private MyAnimation sleepAnimation;
    private float stateTimer;
    public Animation faceAnimationh;
    private House house;
    public  GenerateAction generateAction;

    float actionTimer = GenerateTimeAction();

    final int maxSleepPoints = 22;
     private int SleepPoints;


    public Sheep(House house, JSONObject object) {
        super(house, object);

        JSONObject sheep = object.optJSONObject(FillType.SHEEP);
        String sheepStandingImage = sheep.optString(FillType.SHEEP_STANDING_IMAGE);
        TextureRegion standRegion = TextureManager.getInstance().region(sheepStandingImage);
        sheepWalk = AnimationUtils.createAnimation(sheep.optJSONObject(FillType.SHEEP_WALK_ANIMATION));
        sheepSleep = AnimationUtils.createAnimation(sheep.optJSONObject(FillType.SHEEP_SLEEP_ANIMATION));
        sleepAnimation = new MyAnimation();
        walkAnimation = new MyAnimation();

        SleepPoints =  sheep.optInt(FillType.SHEEP_SLEEP_POINTS);
        float x = (float) sheep.optDouble(FillType.X);
        float y = (float) sheep.optDouble(FillType.Y);
        setBounds(x * Const.W, y * Const.H, Const.SHEEP_W, Const.SHEEP_H);

        visible = true;
        turnRight = true;

        currentState = Sheep.State.STAND;
        previousState = Sheep.State.STAND;

        stateTimer = 0;
        if(sheep.has(FillType.ANIMATION_PARAMS))
            faceAnimationh = AnimationUtils.createAnimation(sheep.optJSONObject(FillType.ANIMATION_PARAMS));
        inventar = new Inventar(this);

        PhraseUtils.load(sheep.optJSONObject(FillType.PHRASES));

        this.generateAction = new GenerateAction(house);
    }


    public float GenerateTimeAction()
    {
        float time = (float) (3 + (Math.random() * 5));
        return  time;
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Inventar getInventar() {
        return inventar;
    }

    public int getSleepPoints() {
        return SleepPoints;
    }
    public void setSleepPoints(int sleepPoints) {
        SleepPoints = sleepPoints;
    }

    @Override
    public Sheep.State getState() {

        if (walkAnimation.isEnabled())
            return Sheep.State.WALK;
        if (sleepAnimation.isEnabled())
            return Sheep.State.SLEEP;
        return Sheep.State.STAND;
    }

    @Override
    public void draw(Batch batch) {
        if (visible)
            super.draw(batch);
    }



    @Override
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region = null;
        switch (currentState) {
            case STAND:
                region = (TextureRegion) sheepWalk.getKeyFrame(0);
                break;
            case WALK:
                region = (TextureRegion) sheepWalk.getKeyFrame(stateTimer, true);
                break;
            case SLEEP:
                region = (TextureRegion) sheepSleep.getKeyFrame(stateTimer, true);
                break;
        }

        if (!turnRight && region.isFlipX()) {
            region.flip(true, false);
        } else if (turnRight && !region.isFlipX()) {
            region.flip(true, false);
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;


        return region;
    }

    public void go(float x, float y) {
        go(x, y, null);
    }
    public void go(float x) {
        go(x, null);
    }

    public void go(RoomSubject subject, final MyAnimation.OnAnimationEndListener onAnimationEndListener) {
        go(subject.getX() + subject.getWidth() / 2f, subject.getY() + subject.getHeight() / 2f,onAnimationEndListener);
    }

    void ActionManager(){
        float x;
        float y;
        float zone = 30f; //рамочка, чтобы овечка не уходила за границы

        int action = (int) (1 + ( Math.random() *2  ));

        switch(action)
        {
            case 1:
                x = (float) (Const.FLOOR_X+zone + Math.random() * Const.W * 5.55f-zone);//пойти по координате х
                go(x);
                break;

            case 2:
                int xDir, yDir;
                float xDist, yDist;
                float realX = getX();
                float realY = getY();

                if ((realX - Const.FLOOR_X) > (Const.FLOOR_X + Const.FLOOR_W - realX))
                {
                    xDir = -1;
                    xDist = realX - Const.FLOOR_X - zone;
                }else {

                    xDir = 1;
                    xDist = Const.FLOOR_X + Const.FLOOR_W - realX - zone;

                }

                if ((realY - Const.FLOOR_Y) > (Const.FLOOR_Y + Const.FLOOR_H - realY))
                {
                    yDir = -1;
                    yDist = realY - Const.FLOOR_Y - zone;
                }else {

                    yDir = 1;
                    yDist = Const.FLOOR_Y + Const.FLOOR_H - realY - zone;

                }

                float minDist = (xDist>yDist)? yDist:xDist;
                x = ( xDir == 1)? realX + minDist:realX - minDist;
                y = ( yDir == 1)? realY + minDist:realY - minDist;
                System.out.println("КООРДИНАТЫ ЕБУЧЕЙ ОЦВЫ" + x+"  "+ y +"  ");
                go(x,y);
                break;
        }
    }

    void conditionCheck() {
        if (SleepPoints < 21) {
            goToSleep();
        }else {  sleepAnimation.interrupt();      }
            if (getState() != State.SLEEP) {
                actionTimer -= Gdx.graphics.getDeltaTime();
            }
            if (actionTimer < 0.5) {
                ActionManager();
                actionTimer = GenerateTimeAction();
            }

    }
    public void goToSleep()
    {
        goToSleep(null);
    }
    public void goToSleep(final MyAnimation.OnAnimationEndListener onAnimationEndListener)
    {
        sleepAnimation.start();
        walkAnimation.setOnAnimationEndListener(new MyAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                MusicManager.stopSound();
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
                stateTimer = 0f;
            }
        });
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        conditionCheck();
        if (walkAnimation.isEnabled()) {
            walkAnimation.onAnimatorTick(dt);
        }

    }



    public void go(float x, float y, final MyAnimation.OnAnimationEndListener onAnimationEndListener) {
        float xdist = x - getX() - getWidth() / 2f;
        float ydist = y - getY();

        //вычисление ветора передвижения овцы
        float dist = (float) Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2))/ 2f;

        if (Math.abs(dist) < 0.1f * Const.W) {
            if (onAnimationEndListener != null) {
                onAnimationEndListener.onAnimationEnd();
            }
            return;
        }
        final float k = xdist < 0 ? -1 : 1;
        final float j = ydist < 0 ? -1 : 1;
        if (k < 0 && turnRight)
            turn(false);
        else if (k > 0 && !turnRight)
            turn(true);

        //скорость овцы

        float maxvel = Math.abs(dist) / Const.VEL_X < Math.abs(dist) / Const.VEL_Y ?  Math.abs(dist) / Const.VEL_Y  :  Math.abs(dist) / Const.VEL_X;

        walkAnimation.interrupt();
        walkAnimation.setMaxTime(maxvel);
        walkAnimation.setOnAnimationEndListener(new MyAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                MusicManager.stopSound();
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
                stateTimer = 0f;
            }
        });
        walkAnimation.setOnTick(new MyAnimation.OnTick() {
            @Override
            public void onTick(float delta) {
                if(delta<0.02f ){
                    translateX(delta * Const.VEL_X * k);
                    translateY(delta *Const.VEL_Y * j );
                }
            }
        });
        walkAnimation.start();
        MusicManager.playSound(MusicManager.SoundType.STEPS, false);

    }


    public void go(float x, final MyAnimation.OnAnimationEndListener onAnimationEndListener) {

        float xdist = x - getX() - getWidth() / 2f;

        if (Math.abs(xdist) < 0.1f * Const.W) {
            if (onAnimationEndListener != null) {
                onAnimationEndListener.onAnimationEnd();
            }
            return;
        }
        final float k = xdist < 0 ? -1 : 1;
        if (k < 0 && turnRight)
            turn(false);
        else if (k > 0 && !turnRight)
            turn(true);

        walkAnimation.interrupt();
        walkAnimation.setMaxTime(Math.abs(xdist) / Const.VEL_X);
        walkAnimation.setOnAnimationEndListener(new MyAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                MusicManager.stopSound();
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
                stateTimer = 0f;
            }
        });
        walkAnimation.setOnTick(new MyAnimation.OnTick() {
            @Override
            public void onTick(float delta) {
                if(delta<0.02f ){
                    translateX(delta * Const.VEL_X * k);
                }

            }
        });
        walkAnimation.start();
        MusicManager.playSound(MusicManager.SoundType.STEPS, false);
    }



    public void turn(boolean turnR) {
        if (turnRight != turnR) {
            turnRight = turnR;
            setFlip(turnR, false);
        }
    }

    public boolean isWalking() {
        return getState().equals(Sheep.State.WALK);
    }

    public void stopWalking() {
        if (isWalking()) {
            walkAnimation.interrupt();
            MusicManager.stopSound();
        }
    }

    public void hide(RoomSubject subject) {
        visible = false;
        BigListener.getInstance().hideRoomSubject(subject);
        LevelManager.getInstance().hideRoomSubject(subject);
    }


    public void search(final RoomSubject subject) {
        go(subject, new MyAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                int count = 0;
                if (subject.getSubject().size() != 0) {
                    for (Subject sub : subject.getSubject()) {
                        if (sub.isVisible()) {
                            inventar.add(sub);
                            BigListener.getInstance().findSubject(sub);
                            LevelManager.getInstance().findSubject(sub);
                            count++;
                        }
                    }
                    subject.clearSubjects();
                    House.ChangeLogItem item = new House.ChangeLogItem(subject.getRoomId(), subject.getId());
                    for (Subject sub : subject.getSubject()) {
                        item.list.add(sub.getId());
                    }

                    getHouse().addChangeLog(item);
                }
                BigListener.getInstance().searchRoomSubject(subject);
                LevelManager.getInstance().searchRoomSubject(subject);
                if (count != 0) {
                    MusicManager.playSound(MusicManager.SoundType.SUCSESS, false);
                    if(subject.getSpecialSearchDescription() == null) {
                        BottomPanel.getInstance().showPanel(PhraseUtils.getRandomPhrase(PhraseUtils.PhraseType.FOUND));
                    } else {
                        BottomPanel.getInstance().showPanel(subject.getSpecialSearchDescription());}
                } else {
                    if(subject.getSpecialSearchDescription() == null) {
                        BottomPanel.getInstance().showPanel(PhraseUtils.getRandomPhrase(PhraseUtils.PhraseType.NOT_FOUND));
                    }else{
                        BottomPanel.getInstance().showPanel(subject.getSpecialSearchDescription());}
                }
            }
        });
    }

    enum State {
        STAND, WALK, SLEEP, BESAD,
    }

}