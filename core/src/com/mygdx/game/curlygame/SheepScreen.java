package com.mygdx.game.curlygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Const;
import com.mygdx.game.InputListener;
import com.mygdx.game.InventarScreen;
import com.mygdx.game.MusicManager;
import com.mygdx.game.MyGame;
import com.mygdx.game.TextureManager;
import com.mygdx.game.obj.House;
import com.mygdx.game.utils.FileUtils;

import org.json.JSONObject;

public class SheepScreen implements Screen
{
    private final MyGame myGame;
    private SpriteBatch mainBatch;
    private SpriteBatch menuBatch;
    public OrthographicCamera camera;
    private House sheepHouse;
    private Sprite inventarSprite;
    public boolean reloadTextures = true;

    public SheepScreen(MyGame myGame) {
        this.myGame = myGame;
    }

    public JSONObject getJSONObjectFromFile(String name) {
        String body = FileUtils.getBody("json/" + name + ".json", true);
        return new JSONObject(body);
    }

    private JSONObject getJSONObjectFromLocalFile(String filepath) {
        FileHandle handle = Gdx.files.local(filepath);
        String body = handle.readString();
        return new JSONObject(body);
    }

    @Override
    public void show()
    {
        if (reloadTextures) {
           TextureManager.getInstance().init("all/new.atlas");

            menuBatch = new SpriteBatch();
            mainBatch = new SpriteBatch();
            inventarSprite = TextureManager.getInstance().sprite(Const.INVENTAR_IMAGE_FILEPATH);
            inventarSprite.setBounds(0 * Const.W, 7f * Const.H, 2.22f * Const.H, 1.99f * Const.H);

            camera = new OrthographicCamera(Const.SCREEN_WIDTH, Const.SCREEN_HEIGHT);
            camera.translate(Const.SCREEN_WIDTH / 2f, Const.SCREEN_HEIGHT / 2f);
            camera.update();

            MusicManager.playMusic(Const.MENU_MUSIC_FILEPATH, true);

            sheepHouse = new House(getJSONObjectFromFile(Const.JSON_SHEEP_WORLD), myGame, 1, new House.OnEndGame() {
                @Override
                public void onEnd() {
                    dispose();
                    myGame.goToMenuScreen();
                    FileHandle handle = Gdx.files.local(Const.SAVE);
                    if(handle.exists())
                        handle.delete();
                }
            });

        }
        Gdx.input.setInputProcessor(new SheepInputListener(this, sheepHouse));
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        if (!isOnPause){
        sheepHouse.update(delta);
        mainBatch.begin();
        sheepHouse.draw(mainBatch, delta);
        mainBatch.end();

        menuBatch.begin();
        inventarSprite.draw(menuBatch);
        menuBatch.end();
        }

     //   public boolean menuBatchClick(float x, float y) { //кликаем на меню инвентаря
    //        if (inventarSprite.getBoundingRectangle().contains(x, y)) {
      //          MusicManager.pauseSound();
       //         myGame.setScreen(new InventarScreen(myGame, house));
        //        return true;
        //    }
         //   return false;
    }

    @Override
    public void resize(int width, int height) {

    }
    private  boolean isOnPause = false;
    @Override
    public void pause() {
        System.out.println("ПАУЗА");
        isOnPause = true;
        sheepHouse.getSheep().walkAnimation.interrupt();
    }

    @Override
    public void resume() {
        System.out.println("ПРОДОЛЖАЕМ ЧИЛЛИТЬ");
        isOnPause = false;

    }

    @Override
    public void hide() {
        System.out.println("ПРЯЧЕМ");
    }

    @Override
    public void dispose() {
        mainBatch.dispose();
    }
}
