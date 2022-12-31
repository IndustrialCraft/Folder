package com.github.industrialcraft.folder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Set;

public class AnimationEditor {
    public static final float MARKER_RADIUS = 4;
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
    }
    public void resize(int width, int height){
        this.camera.viewportWidth = width;
        this.camera.viewportHeight = height;
        this.camera.position.x = width/2;
        this.camera.position.y = height/2;
        this.camera.update();
    }
    public void draw(float playTime){
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
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
    }
    public void drawRow(Node node, float maxTextSize, int yIndex){
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
            shapeRenderer.circle(splitterX+(timeAccumulator*timeToXMultiplier), startPosY+(height/2), MARKER_RADIUS);
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
    }
}
