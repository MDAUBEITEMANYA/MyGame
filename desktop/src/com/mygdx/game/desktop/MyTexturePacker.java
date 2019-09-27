package com.mygdx.game.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class MyTexturePacker {

    public static void main(String[] args) throws Exception {
        Properties local = new Properties();
        local.load(new FileReader(new File("local.properties")));

        String texturesDir = local.getProperty("texture.dir");
        String atlas = local.getProperty("texture.atlas");
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMag = Texture.TextureFilter.Linear;
        settings.filterMin = Texture.TextureFilter.Linear;
		settings.maxWidth = 4096;
		settings.maxHeight = 2048;
        File file = new File(atlas);
        if(file.exists()) {
            if (file.delete())
                System.out.println("Folder has been deleted");
            else
                System.out.println("Folder has not been deleted");
        }

        TexturePacker.process(settings,  texturesDir, atlas, "new");
    }
}
