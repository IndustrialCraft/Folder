package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.IOException;
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
    private NativeFileChooserConfiguration textureFileChooser;
    private NativeFileChooser fileChooser;
    private Label animationNameLabel;
    public Node rootNode;
    private final AnimationEditor parent;
    private final GDXDialogs dialogs;
    public KeyframeEditorWindow(NativeFileChooser fileChooser, Runnable pauseButtonCallback, Runnable resetButtonCallback, Node rootNode, AnimationEditor parent, GDXDialogs dialogs) {
        this.fileChooser = fileChooser;
        this.rootNode = rootNode;
        this.parent = parent;
        this.dialogs = dialogs;
        this.textureFileChooser = new NativeFileChooserConfiguration();
        this.textureFileChooser.directory = Gdx.files.internal("");
        this.textureFileChooser.mimeFilter = "image/*";
        this.textureFileChooser.title = "Choose texture";
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
        TextButton changeAnimationButton = new TextButton("change animation", skin);
        changeAnimationButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GDXTextPrompt bDialog = dialogs.newDialog(GDXTextPrompt.class);
                bDialog.setTitle("Switch to animation");
                bDialog.setMessage("Animation name: ");

                bDialog.setCancelButtonLabel("Cancel");
                bDialog.setConfirmButtonLabel("Confirm");

                bDialog.setTextPromptListener(new TextPromptListener() {
                    @Override
                    public void confirm(String text) {
                        parent.setAnimation(text);
                    }
                    @Override
                    public void cancel() {}
                });
                bDialog.build().show();
            }
        });
        this.controlsTable.add(changeAnimationButton);
        TextButton exportButton = new TextButton("export", skin);
        exportButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    SaverLoader.exportZip(new File("exported.zip"), KeyframeEditorWindow.this.rootNode);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.controlsTable.add(exportButton);
        TextButton loadButton = new TextButton("load", skin);
        loadButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    parent.setRootNode(SaverLoader.loadZip(new File("exported.zip")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        this.controlsTable.add(loadButton);
        this.animationNameLabel = new Label("", skin);
        this.controlsTable.add(animationNameLabel);
        table = new VerticalGroup();
        table.addActor(controlsTable);
        table.setFillParent(true);
        stage.addActor(table);
    }
    public void setAnimationName(String name){
        animationNameLabel.setText("animation: " + name);
    }
    public void clear(){
        table.clear();
        table.addActor(controlsTable);
        controllingAnimation = null;
    }
    public void setEditing(Node node, String animation, int index){
        this.controllingNode = node;
        this.controllingAnimation = node.getOrCreateAnimation(animation);
        this.controllingTransformIndex = index;
        table.clear();
        table.addActor(controlsTable);
        addStringField("name", table, s -> node.name=s, () -> node.name);
        addNumberedField("originX", table, n -> node.nodeTexture.xOrigin=n, () -> node.nodeTexture.xOrigin);
        addNumberedField("originY", table, n -> node.nodeTexture.yOrigin=n, () -> node.nodeTexture.yOrigin);
        addButton("choose texture", table, () -> {
            fileChooser.chooseFile(textureFileChooser, new NativeFileChooserCallback() {
                @Override
                public void onFileChosen(FileHandle file) {
                    node.nodeTexture.setTexture(file);
                }
                @Override
                public void onCancellation() {}
                @Override
                public void onError(Exception exception) {}
            });
        });
        addNumberedField("duration", table, n -> controllingAnimation.transforms.get(index).length=n, () -> controllingAnimation.transforms.get(index).length);
        addNumberedField("x", table, n -> controllingAnimation.transforms.get(index).transform.x=n, () -> controllingAnimation.transforms.get(index).transform.x);
        addNumberedField("y", table, n -> controllingAnimation.transforms.get(index).transform.y=n, () -> controllingAnimation.transforms.get(index).transform.y);
        addNumberedField("rotation", table, n -> controllingAnimation.transforms.get(index).transform.rotation= (float) Math.toRadians(n), () -> (float) Math.toDegrees(controllingAnimation.transforms.get(index).transform.rotation));
        addNumberedField("size", table, n -> controllingAnimation.transforms.get(index).transform.size=n, () -> controllingAnimation.transforms.get(index).transform.size);
        addNumberedField("opacity", table, n -> controllingAnimation.transforms.get(index).transform.opacity=n, () -> controllingAnimation.transforms.get(index).transform.opacity);
        addNumberedField("lightBBx", table, n -> node.lightBoundingBox.x=n, () -> node.lightBoundingBox.x);
        addNumberedField("lightBBy", table, n -> node.lightBoundingBox.y=n, () -> node.lightBoundingBox.y);
        addNumberedField("lightBBw", table, n -> node.lightBoundingBox.width=n, () -> node.lightBoundingBox.width);
        addNumberedField("lightBBh", table, n -> node.lightBoundingBox.height=n, () -> node.lightBoundingBox.height);
        addButton("remove", table, () -> {
            if(index == 0)
                return;
            controllingAnimation.transforms.remove(index);
            table.clear();
            table.addActor(controlsTable);
            controllingAnimation = null;
        }).setDisabled(index==0);
        addButton("copy", table, () -> {
            controllingAnimation.transforms.add(index+1, new Animation.TransformWithLength(controllingAnimation.transforms.get(index).transform.copy(), controllingAnimation.transforms.get(index).length));
            table.clear();
            table.addActor(controlsTable);
            controllingAnimation = null;
        });
    }
    public Transform getSelectedTransform(float time){
        if(controllingAnimation == null)
            return null;
        return controllingAnimation.getLerpedTransformFor(time);
    }
    private TextButton addButton(String name, VerticalGroup table, Runnable runnable){
        TextButton button = new TextButton(name, skin);
        button.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                runnable.run();
            }
        });
        table.addActor(button);
        return button;
    }
    private void addStringField(String name, VerticalGroup table, Consumer<String> setter, Supplier<String> getter){
        HorizontalGroup wrapper = new HorizontalGroup();
        wrapper.addActor(new Label(name+":", skin));
        TextField btn = new TextField(getter.get(), skin);
        btn.setTextFieldListener((textField, c) -> {
            if(btn.getText().isEmpty())
                return;
            setter.accept(textField.getText());
        });
        wrapper.addActor(btn);
        table.addActor(wrapper);
    }
    private void addNumberedField(String name, VerticalGroup table, Consumer<Float> setter, Supplier<Float> getter){
        HorizontalGroup wrapper = new HorizontalGroup();
        wrapper.addActor(new Label(name+":", skin));
        TextField btn = new TextField(""+getter.get(), skin);
        btn.setTextFieldFilter((textField, c) -> Character.isDigit(c) || c == '-' || c == '.');
        btn.setTextFieldListener((textField, c) -> {
            if(btn.getText().isEmpty())
                return;
            try {
                setter.accept(Float.parseFloat(textField.getText()));
            } catch (NumberFormatException e){}
        });
        wrapper.addActor(btn);
        table.addActor(wrapper);
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
