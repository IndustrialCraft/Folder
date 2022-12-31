package com.github.industrialcraft.folder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FolderMain extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture head;
	private Texture face;
	private Texture body;
	private Node node;
	private AtomicReference<Float> time;
	private OrthographicCamera sceneCamera;
	private AnimationEditor animationEditor;
	private AtomicBoolean paused = new AtomicBoolean(true);
	private final NativeFileChooser fileChooser;
	private ShapeRenderer shapeRenderer;
	public FolderMain(NativeFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}
	@Override
	public void create () {
		this.sceneCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.sceneCamera.update();
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);
		head = new Texture("head.png");
		face = new Texture("face.png");
		body = new Texture("body.png");
		time = new AtomicReference<>(0f);
		node = new Node();
		node.nodeTexture.texture = new TextureRegion(head);
		Animation animation = new Animation();
		animation.transforms.add(new Animation.TransformWithLength(new Transform(0, 0, 0, 1, 1), 1));
		animation.transforms.add(new Animation.TransformWithLength(new Transform(100, 50, (float) Math.toRadians(90), 0.5f, 1), 2));
		animation.transforms.add(new Animation.TransformWithLength(new Transform(200, 50, 0, 1, 1), 1));
		node.animations.put("a", animation);
		this.animationEditor = new AnimationEditor(fileChooser, node, () -> {
			paused.set(!paused.get());
		}, (t) -> {
			time.set(t);
			paused.set(true);
		});
		this.animationEditor.animation = "a";
	}
	@Override
	public void resize (int width, int height) {
		this.sceneCamera.viewportWidth = width;
		this.sceneCamera.viewportHeight = height;
		this.sceneCamera.update();
		this.animationEditor.resize(width, height);
	}
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.batch.setProjectionMatrix(sceneCamera.combined);
		this.shapeRenderer.setProjectionMatrix(sceneCamera.combined);
		if(!paused.get())
			time.set(time.get()+Gdx.graphics.getDeltaTime());
		Gdx.gl.glViewport(0, (int) (AnimationEditor.SCREEN_SPACE*Gdx.graphics.getHeight()), (int) ((1-KeyframeEditorWindow.SCREEN_SPACE)*sceneCamera.viewportWidth), (int) (sceneCamera.viewportHeight*(1-AnimationEditor.SCREEN_SPACE)));
		batch.begin();
		node.drawRecursively(batch, "a", time.get());
		batch.end();
		Transform selectedTransform = animationEditor.getSelectedTransform(time.get());
		if(selectedTransform != null) {
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(0, 1, 0, 1);
			shapeRenderer.circle(selectedTransform.x, selectedTransform.y, 5);
			shapeRenderer.end();
		}
		this.animationEditor.draw(time.get());
	}
	@Override
	public void dispose() {
		head.dispose();
		face.dispose();
		body.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		this.animationEditor.dispose();
	}
}