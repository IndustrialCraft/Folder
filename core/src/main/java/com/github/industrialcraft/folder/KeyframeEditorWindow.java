package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class KeyframeEditorWindow {
    public static float SCREEN_SPACE = 0.4f;
    private Stage stage;
    private Table table;
    private Skin skin;
    private InputMultiplexer multiplexer;
    public KeyframeEditorWindow() {
        this.multiplexer = new InputMultiplexer();
        //this.multiplexer.addProcessor(MOUSE_COORD_CHECKING_INPUT_PROCESSOR);
        this.stage = new Stage();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage.setViewport(new CustomScreenViewport());

        table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextField textField = new TextField("aaa", skin);
        table.add(textField);
    }
    public void draw(){
        stage.getViewport().apply(true);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }
    public void resize(int width, int height){
        stage.getViewport().setScreenX((int) ((1-SCREEN_SPACE)*width));
        stage.getViewport().setScreenY((int) (AnimationEditor.SCREEN_SPACE*height));
        stage.getViewport().update((int) (width*SCREEN_SPACE), (int) (height*(1-AnimationEditor.SCREEN_SPACE)), true);
    }
    public void dispose(){
        this.stage.dispose();
    }
    public static final InputProcessor MOUSE_COORD_CHECKING_INPUT_PROCESSOR = new InputProcessor() {
        public static boolean checkMouse(){
            return !(Gdx.input.getX()>(1-SCREEN_SPACE)*Gdx.graphics.getWidth()&&Gdx.input.getY()<(1-AnimationEditor.SCREEN_SPACE)*Gdx.graphics.getHeight());
        }
        @Override
        public boolean keyDown(int keycode) {
            return checkMouse();
        }
        @Override
        public boolean keyUp(int keycode) {
            return checkMouse();
        }
        @Override
        public boolean keyTyped(char character) {
            return checkMouse();
        }
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return checkMouse();
        }
        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return checkMouse();
        }
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return checkMouse();
        }
        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return checkMouse();
        }
        @Override
        public boolean scrolled(float amountX, float amountY) {
            return checkMouse();
        }
    };
}
