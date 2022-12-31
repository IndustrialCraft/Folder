package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.Set;

public class AnimationEditor {
    public static final Color BACKGROUND_COLOR = new Color(0.4f, 0.4f, 0.4f, 1);
    public static final float MARKER_RADIUS = 8;
    public static final float NAME_PADDING = 10;
    public static int ROWS_ON_SCREEN = 4;
    public static float SCREEN_SPACE = 0.4f;
    public final Node rootNode;
    private SpriteBatch spriteBatch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private BitmapFont font;
    private GlyphLayout fontLayout;
    public String animation;
    private float timeToXMultiplier = 50;
    private KeyframeEditorWindow editorWindow;
    public AnimationEditor(Node rootNode) {
        this.rootNode = rootNode;
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
        this.editorWindow = new KeyframeEditorWindow();
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
        Set<Node> nodes = rootNode.getChildAndSelfRecursively();
        float maxTextSize = nodes.stream().map(node -> {fontLayout.setText(font, node.name);return fontLayout.width;}).reduce(0f, Math::max);
        int yIndex = 0;
        for(Node node : nodes){
            drawRow(node, maxTextSize, yIndex);
            yIndex++;
        }
        shapeRenderer.begin();
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.line(maxTextSize+(NAME_PADDING*2)+(playTime*timeToXMultiplier), 0, maxTextSize+(NAME_PADDING*2)+(playTime*timeToXMultiplier), SCREEN_SPACE*Gdx.graphics.getHeight());
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BACKGROUND_COLOR);
        shapeRenderer.rect((1-SCREEN_SPACE)*camera.viewportWidth, AnimationEditor.SCREEN_SPACE* camera.viewportHeight,camera.viewportWidth*SCREEN_SPACE, camera.viewportHeight*(1-AnimationEditor.SCREEN_SPACE));
        shapeRenderer.end();
        this.editorWindow.draw();
    }
    public void drawRow(Node node, float maxTextSize, int yIndex){
        Vector3 mouse = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        float height = (SCREEN_SPACE / ROWS_ON_SCREEN)*Gdx.graphics.getHeight();
        float startPosY = height*(3-yIndex);
        float splitterX = maxTextSize+(NAME_PADDING*2);
        shapeRenderer.begin();
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.line(0, startPosY, Gdx.graphics.getWidth(), startPosY);
        shapeRenderer.line(splitterX, startPosY, splitterX, startPosY+height);
        shapeRenderer.setColor(1, 1, 1, 1);
        float totalAnimationLength = node.getOrCreateAnimation(animation).transforms.stream().map(Animation.TransformWithLength::length).reduce(0f, Float::sum);
        shapeRenderer.line(splitterX, startPosY+(height/2), splitterX+(totalAnimationLength*timeToXMultiplier), startPosY+(height/2));
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        float timeAccumulator = 0;

        for(Animation.TransformWithLength transform : node.getOrCreateAnimation(animation).transforms){
            float markerX = splitterX+(timeAccumulator*timeToXMultiplier);
            float markerY = startPosY+(height/2);
            if(MathUtils.distanceSquared((int) (mouse.x-markerX), (int) (mouse.y-markerY)) < MARKER_RADIUS*MARKER_RADIUS){
                if(Gdx.input.isButtonJustPressed(1)){
                    //todo: select node
                }
                shapeRenderer.setColor(0, 1, 0, 1);
            } else {
                shapeRenderer.setColor(1, 1, 1, 1);
            }
            shapeRenderer.circle(markerX, markerY, MARKER_RADIUS);
            timeAccumulator += transform.length();
        }
        shapeRenderer.end();
        spriteBatch.begin();
        fontLayout.setText(font, node.name);
        font.draw(spriteBatch, node.name, NAME_PADDING, startPosY+(height/2)+(fontLayout.height/2));
        spriteBatch.end();
    }
    public void dispose(){
        this.spriteBatch.dispose();
        this.shapeRenderer.dispose();
        this.editorWindow.dispose();
    }
}
