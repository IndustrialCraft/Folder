package com.github.industrialcraft.folder;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FolderMain extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture head;
	private Texture face;
	private Texture body;
	private Node node;
	private float time;
	private OrthographicCamera sceneCamera;
	private AnimationEditor animationEditor;
	@Override
	public void create () {
		this.sceneCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.sceneCamera.update();
		batch = new SpriteBatch();
		head = new Texture("head.png");
		face = new Texture("face.png");
		body = new Texture("body.png");
		time = 0;
		node = new Node();
		node.nodeTexture.texture = new TextureRegion(head);
		Animation animation = new Animation();
		animation.transforms.add(new Animation.TransformWithLength(new Transform(0, 0, 0, 1, 1), 1));
		animation.transforms.add(new Animation.TransformWithLength(new Transform(100, 50, (float) Math.toRadians(90), 0.5f, 1), 1));
		animation.transforms.add(new Animation.TransformWithLength(new Transform(200, 50, 0, 1, 1), 1));
		node.animations.put("a", animation);
		this.animationEditor = new AnimationEditor(node);
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
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.batch.setProjectionMatrix(sceneCamera.combined);
		time += Gdx.graphics.getDeltaTime();
		batch.begin();
		node.drawRecursively(batch, "a", time);
		batch.end();
		this.animationEditor.draw(time);
	}
	@Override
	public void dispose() {
		head.dispose();
		face.dispose();
		body.dispose();
		batch.dispose();
		this.animationEditor.dispose();
	}
}