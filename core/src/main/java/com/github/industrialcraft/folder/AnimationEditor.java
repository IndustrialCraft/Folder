package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import de.tomgrill.gdxdialogs.core.GDXDialogs;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.util.List;
import java.util.function.Consumer;

public class AnimationEditor {
    public static final Color BACKGROUND_COLOR = new Color(0.4f, 0.4f, 0.4f, 1);
    public static final float NODE_SETTING_WIDTH = 15;
    public static final float MARKER_RADIUS = 8;
    public static final float NAME_PADDING = 10;
    public static int ROWS_ON_SCREEN = 4;
    public static float SCREEN_SPACE = 0.4f;
    public Node rootNode;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout fontLayout;
    private String animation;
    private float timeToXMultiplier = 50;
    private KeyframeEditorWindow editorWindow;
    private Consumer<Float> timeSetter;
    public int nodeIndex;
    public final FolderMain main;
    public AnimationEditor(GDXDialogs dialogs, NativeFileChooser fileChooser, Node rootNode, Runnable pauseButtonCallback, Consumer<Float> timeSetter, FolderMain main) {
        this.timeSetter = timeSetter;
        this.rootNode = rootNode;
        this.main = main;
        this.spriteBatch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setAutoShapeType(true);
        this.camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.x = Gdx.graphics.getWidth()/2;
        this.camera.position.y = Gdx.graphics.getHeight()/2;
        this.camera.update();
        this.font = new BitmapFont();
        this.fontLayout = new GlyphLayout();
        this.animation = null;
        this.editorWindow = new KeyframeEditorWindow(fileChooser, pauseButtonCallback, () -> timeSetter.accept(0f), rootNode, this, dialogs);
        this.nodeIndex = 0;
    }
    public void setRootNode(Node rootNode){
        this.rootNode = rootNode;
        setAnimation("default");
        this.editorWindow.rootNode = rootNode;
        this.main.setRootNode(rootNode);
    }
    public String getAnimation() {
        return animation;
    }
    public void setAnimation(String animation) {
        this.animation = animation;
        this.editorWindow.clear();
        this.editorWindow.setAnimationName(animation);
    }
    public Transform getSelectedTransform(float time){
        return editorWindow.getSelectedTransform(time);
    }
    public void resize(int width, int height){
        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
        this.camera.position.x = width/2;
        this.camera.position.y = height/2;
        this.camera.update();
        this.editorWindow.resize(width, height);
    }
    public void draw(float playTime){
        Gdx.gl.glViewport(0, 0, (int) camera.viewportWidth, (int) camera.viewportHeight);
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()* SCREEN_SPACE);
        shapeRenderer.end();
        if(animation == null)
            return;
        List<Node> nodes = rootNode.getChildAndSelfRecursively();
        float maxTextSize = nodes.stream().map(node -> {fontLayout.setText(font, node.getRealName());return fontLayout.width;}).reduce(0f, Math::max);

        if(nodeIndex < 0)
            nodeIndex = Math.max(nodes.size()-ROWS_ON_SCREEN, 0);
        if(nodes.size() > ROWS_ON_SCREEN && nodeIndex > nodes.size()-ROWS_ON_SCREEN){
            nodeIndex = 0;
        }
        for(int i = 0;i < ROWS_ON_SCREEN && i+nodeIndex < nodes.size();i++){
            drawRow(nodes.get(i+nodeIndex), maxTextSize, i);
        }
        shapeRenderer.begin();
        float ending = nodes.stream().map(node -> {
            var anims = node.animations.get(animation).transforms;
            return anims.stream().map(t -> t.length).reduce(-anims.get(anims.size()-1).length, Float::sum);
        }).max(Float::compare).get();
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2)+(ending*timeToXMultiplier), 0, 1, SCREEN_SPACE*Gdx.graphics.getHeight());

        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2)+(playTime*timeToXMultiplier), 0, maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2)+(playTime*timeToXMultiplier), SCREEN_SPACE*Gdx.graphics.getHeight());
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect((1-SCREEN_SPACE)*camera.viewportWidth, AnimationEditor.SCREEN_SPACE*camera.viewportHeight,camera.viewportWidth*SCREEN_SPACE, camera.viewportHeight*(1-AnimationEditor.SCREEN_SPACE));
        shapeRenderer.end();
        this.editorWindow.draw();
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        if(mouse.y < SCREEN_SPACE*Gdx.graphics.getHeight() && mouse.x > maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            timeSetter.accept((mouse.x-(maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2)))/timeToXMultiplier);
        }
    }
    public void drawRow(Node node, float maxTextSize, int yIndex){
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float height = (SCREEN_SPACE / ROWS_ON_SCREEN)*Gdx.graphics.getHeight();
        float startPosY = height*(3-yIndex);
        float splitterX = maxTextSize+NODE_SETTING_WIDTH+(NAME_PADDING*2);
        shapeRenderer.begin();
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0, startPosY, Gdx.graphics.getWidth(), startPosY);
        shapeRenderer.line(splitterX, startPosY, splitterX, startPosY+height);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.line(splitterX, startPosY+(height/2), Gdx.graphics.getWidth(), startPosY+(height/2));
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float timeAccumulator = 0;
        int i = 0;
        for(Animation.TransformWithLength transform : node.getOrCreateAnimation(animation).transforms){
            float markerX = splitterX+(timeAccumulator*timeToXMultiplier);
            float markerY = startPosY+(height/2);
            if(MathUtils.distanceSquared((int) (mouse.x-markerX), (int) (mouse.y-markerY)) < MARKER_RADIUS*MARKER_RADIUS){
                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                    this.editorWindow.setEditing(node, animation, i);
                }
                shapeRenderer.setColor(0, 1, 0, 1);
            } else {
                shapeRenderer.setColor(1, 1, 1, 1);
            }
            shapeRenderer.circle(markerX, markerY, MARKER_RADIUS);
            timeAccumulator += transform.length;
            i++;
        }
        if(node.parent != null) {
            if (mouse.x >= 0 && mouse.y >= startPosY && mouse.x <= NODE_SETTING_WIDTH && mouse.y <= startPosY + height / 2) {
                shapeRenderer.setColor(1, 0, 0, 1);
                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                    node.parent.removeChild(node);
                }
            } else {
                shapeRenderer.setColor(0.5f, 0, 0, 1);
            }
            shapeRenderer.rect(0, startPosY, NODE_SETTING_WIDTH, height / 2);
        }
        if(mouse.x >= 0 && mouse.y >= startPosY+(height/2) && mouse.x <= NODE_SETTING_WIDTH && mouse.y <= startPosY + height){
            shapeRenderer.setColor(0, 1, 0, 1);
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                node.addChild(new Node(node));
            }
        } else {
            shapeRenderer.setColor(0, 0.5f, 0, 1);
        }
        shapeRenderer.rect(0, startPosY+(height/2), NODE_SETTING_WIDTH, height/2);
        shapeRenderer.end();
        spriteBatch.begin();
        fontLayout.setText(font, node.getRealName());
        font.draw(spriteBatch, node.getRealName(), NODE_SETTING_WIDTH + NAME_PADDING, startPosY+(height/2)+(fontLayout.height/2));
        spriteBatch.end();
    }
    public void dispose(){
        this.spriteBatch.dispose();
        this.shapeRenderer.dispose();
        this.editorWindow.dispose();
    }
}
