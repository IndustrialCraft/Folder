package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeyframeEditorWindow {
    public static float SCREEN_SPACE = 0.4f;
    private Stage stage;
    private VerticalGroup table;
    private Skin skin;
    private InputMultiplexer multiplexer;
    private Node controllingNode;
    private Animation controllingAnimation;
    private int controllingTransformIndex;
    private Table controlsTable;
    public KeyframeEditorWindow(Runnable pauseButtonCallback, Runnable resetButtonCallback) {
        this.multiplexer = new InputMultiplexer();
        //this.multiplexer.addProcessor(MOUSE_COORD_CHECKING_INPUT_PROCESSOR);
        this.stage = new Stage();
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage.setViewport(new CustomScreenViewport());
        this.controlsTable = new Table();
        TextButton pauseButton = new TextButton("pause/resume", skin);
        pauseButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseButtonCallback.run();
            }
        });
        this.controlsTable.add(pauseButton);
        TextButton resetButton = new TextButton("reset", skin);
        resetButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetButtonCallback.run();
            }
        });
        this.controlsTable.add(resetButton);
        table = new VerticalGroup();
        table.addActor(controlsTable);
        table.setFillParent(true);
        stage.addActor(table);
    }
    public void setEditing(Node node, String animation, int index){
        this.controllingNode = node;
        this.controllingAnimation = node.getOrCreateAnimation(animation);
        this.controllingTransformIndex = index;
        table.clear();
        table.addActor(controlsTable);
        addNumberedField("duration", table, n -> controllingAnimation.transforms.get(index).length=n, () -> controllingAnimation.transforms.get(index).length);
        addNumberedField("x", table, n -> controllingAnimation.transforms.get(index).transform.x=n, () -> controllingAnimation.transforms.get(index).transform.x);
        addNumberedField("y", table, n -> controllingAnimation.transforms.get(index).transform.y=n, () -> controllingAnimation.transforms.get(index).transform.y);
        addNumberedField("rotation", table, n -> controllingAnimation.transforms.get(index).transform.rotation=n, () -> controllingAnimation.transforms.get(index).transform.rotation);
        addNumberedField("size", table, n -> controllingAnimation.transforms.get(index).transform.size=n, () -> controllingAnimation.transforms.get(index).transform.size);
        addNumberedField("opacity", table, n -> controllingAnimation.transforms.get(index).transform.opacity=n, () -> controllingAnimation.transforms.get(index).transform.opacity);
    }
    public void addNumberedField(String name, VerticalGroup table, Consumer<Float> setter, Supplier<Float> getter){
        table.addActor(new Label(name+":", skin));
        TextField btn = new TextField(""+getter.get(), skin);
        btn.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        btn.setTextFieldListener((textField, c) -> {
            if(btn.getText().isEmpty())
                return;
            setter.accept(Float.parseFloat(textField.getText()));
        });
        table.addActor(btn);
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
}
