/*******************************************************************************
 * Copyright 2014 Rafael Garcia Moreno.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.bladecoder.bladeengine.composer;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.bladecoder.bladeengine.composer.model.Project;
import org.bladecoder.bladeengine.composer.scneditor.ScnEditor;
import org.bladecoder.bladeengine.composer.ui.ActorPanel;
import org.bladecoder.bladeengine.composer.ui.AssetPanel;
import org.bladecoder.bladeengine.composer.ui.ProjectPanel;
import org.bladecoder.bladeengine.composer.ui.ProjectToolbar;
import org.bladecoder.bladeengine.composer.ui.ScenePanel;
import org.bladecoder.bladeengine.composer.utils.EditorLogger;
import org.bladecoder.bladeengine.composer.utils.Message;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

//TODO: Show COPYRIGHT_STR + VERSION_STR
//TODO: Set TITLE in window bar. Set '*' in the title when modified
//TODO: Query if save project when exit

public class Editor implements ApplicationListener {
	public static final String VERSION_STR = "0.1.0 (Beta)";
	public static final String COPYRIGHT_STR = "2013 - Rafael García\nhttp://bladecoder.blogspot.com";
	public static final String TITLE = "Adventure Composer";	
	
//	public static final String SKIN = "res/skin/HoloSkin/Holo-dark-ldpi.json";
	// public static final String SKIN = "res/skin/uiskin.json";
	public static final String SKIN = "res/skin/BladeSkin/BladeSkin-ldpi.json";

	Stage stage;
	ScnEditor scnEditor;
	Skin skin;

	@Override
	public void create() {
		skin = new Skin(Gdx.files.internal(SKIN));

		EditorLogger.setDebug();
		EditorLogger.debug("CREATE");
		Ctx.project = new Project();
		Ctx.msg = new Message(skin);
		Ctx.assetManager = new EditorAssetManager();

		scnEditor = new ScnEditor(skin);

		/*** STAGE SETUP ***/
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		// RIGHT PANEL
		ScenePanel scenePanel = new ScenePanel(skin);
		ActorPanel actorPanel = new ActorPanel(skin);

		Table rightPanel = new Table();
		rightPanel.top().left();
		rightPanel.add(scenePanel).expand().fill();
		rightPanel.row();
		rightPanel.add(actorPanel).expand().fill();

		SplitPane splitPaneRight = new SplitPane(scnEditor, rightPanel,
				false, skin);

		// LEFT PANEL
		ProjectPanel projectPanel = new ProjectPanel(skin);
		AssetPanel assetPanel = new AssetPanel(skin);
		Image img = new Image(Ctx.assetManager.getIcon("title"));
		img.setScaling(Scaling.none);
		img.setAlign(Align.left);

		Table leftPanel = new Table();
		leftPanel.top().left().padLeft(10);
		leftPanel.add(img).expandX().fill().padBottom(20).padTop(20).padLeft(20);
		leftPanel.row();
		leftPanel.add(new ProjectToolbar(skin)).expandX().fill();
		leftPanel.row();
		leftPanel.add(projectPanel).expand().fill();
		leftPanel.row();
		leftPanel.add(assetPanel).expand().fill();

		SplitPane splitPaneLeft = new SplitPane(leftPanel, splitPaneRight,
				false, skin);
		splitPaneLeft.setFillParent(true);
		splitPaneLeft.setSplitAmount(0.3f);
		stage.addActor(splitPaneLeft);

		// LOAD LAST OPEN PROJECT
		String lastProject = Ctx.project.getConfig().getProperty(
				Project.LAST_PROJECT_PROP, "");

		if (!lastProject.isEmpty() && new File(lastProject).exists()) {
			try {
				EditorLogger.debug("Loading last project: " + lastProject);
				Ctx.project.loadProject(new File(lastProject));
			} catch (Exception e) {
				EditorLogger.debug("Error loading last project: "
						+ e.getMessage());
				Ctx.project.closeProject();
				e.printStackTrace();
			}
		}
		
		stage.setScrollFocus(scnEditor.getScnWidget());
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		Table.drawDebug(stage);
	}

	@Override
	public void resize(int width, int height) {
		EditorLogger.debug("RESIZE - w:" + width + " h:" + height);

		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		scnEditor.dispose();
		stage.dispose();
		
		Ctx.project.saveConfig();
		
//		try {
//			Ctx.project.saveProject();
//		} catch (Exception ex) {
//			System.out.println("Something went wrong while saving the project.\n");
//			ex.printStackTrace();
//		}
	}

	public void exit() {
		if (Ctx.project.getWorld().isModified()
				|| Ctx.project.getSelectedChapter().isModified()) {
			new Dialog("Save Project", skin) {
				protected void result(Object object) {
					if (((Boolean) object).booleanValue()) {
						try {
							Ctx.project.saveProject();
						} catch (TransformerException | IOException e1) {
							String msg = "Something went wrong while saving the actor.\n\n"
									+ e1.getClass().getSimpleName()
									+ " - "
									+ e1.getMessage();
							Ctx.msg.show(getStage(), msg, 2);

							e1.printStackTrace();
						}
					}

					((Main)Gdx.app).exitSaved();	
				}
			}.text("Save changes to project?").button("Yes", true)
					.button("No", false).key(Keys.ENTER, true)
					.key(Keys.ESCAPE, false).show(stage);
		} else {
			((Main)Gdx.app).exitSaved();	
		}
	
	}
}
