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
package org.bladecoder.bladeengine.composer.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import org.bladecoder.bladeengine.actions.Param;
import org.bladecoder.bladeengine.composer.Ctx;
import org.bladecoder.bladeengine.composer.model.BaseDocument;
import org.bladecoder.bladeengine.composer.model.Project;
import org.bladecoder.bladeengine.composer.ui.components.EditElementDialog;
import org.bladecoder.bladeengine.composer.ui.components.InputPanel;
import org.w3c.dom.Element;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class EditSceneDialog extends EditElementDialog {

	public static final String INFO = "An adventure is composed of many scenes (screens).\n" +
			"Inside a scene there are actors and a 'player'.\nThe player/user can interact with the actors throught 'verbs'.";
	
	private String bgList[] = getBgList();
	private String musicList[] = getMusicList();
	
	private InputPanel[] inputs = new InputPanel[8];
	
	private Image bgImage;
					
	
	String attrs[] = {"id", "background", "lightmap", "depth_vector", "music", "loop_music", "initial_music_delay", "repeat_music_delay"};

	@SuppressWarnings("unchecked")
	public EditSceneDialog(Skin skin, BaseDocument doc, Element parent,
				Element e) {
		
		super(skin);
		
		inputs[0] = new InputPanel(skin, "Scene ID",
				"The ID is mandatory for scenes. \nIDs can not contain '.' or '_' characters.");
		inputs[1] = new InputPanel(skin, "Background",
				"The background for the scene", bgList);
		inputs[2] = new InputPanel(skin, "Lightmap",
						"The lightmap for the scene", bgList);					
		inputs[3] = new InputPanel(skin, "Depth Vector",
						"X: the actor scale when y=0, Y: the actor scale when y=scene height .", Param.Type.VECTOR2, false);					
		inputs[4] = new InputPanel(skin, "Music Filename",
				"The music for the scene", musicList);
		inputs[5] = new InputPanel(skin, "Loop Music",
				"If the music is playing in looping", Param.Type.BOOLEAN, false);
		inputs[6] = new InputPanel(skin, "Initial music delay",
				"The time to wait before playing", Param.Type.FLOAT, false);
		inputs[7] = new InputPanel(skin, "Repeat music delay",
				"The time to wait before repetitions", Param.Type.FLOAT, false);		
		
		bgImage = new Image();
		bgImage.setScaling(Scaling.fit);
		setInfo(INFO);
		
		inputs[0].setMandatory(true);

		init(inputs, attrs, doc, parent, "scene", e);
		
		((SelectBox<String>) inputs[1].getField()).addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				String bg = inputs[1].getText();

				if(!bg.isEmpty())
					bgImage.setDrawable(new TextureRegionDrawable(Ctx.project.getBgIcon(bg)));
				
				setInfoWidget(bgImage);
			}
		});		
	}

	private String[] getBgList() {
		String bgPath = Ctx.project.getProjectPath() + Project.BACKGROUNDS_PATH + "/"
				+ Ctx.project.getResDir();

		File f = new File(bgPath);

		String bgs[] = f.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if ((arg1.matches("_[1-9]\\.")))
					return false;

				return true;
			}
		});

		Arrays.sort(bgs);
		
		ArrayList<String> l = new ArrayList<String>(Arrays.asList(bgs));
		l.add(0,"");

		return l.toArray(new String[bgs.length + 1]);
	}
	
	private String[] getMusicList() {
		String path = Ctx.project.getProjectPath() + Project.MUSIC_PATH;

		File f = new File(path);

		String musicFiles[] = f.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.endsWith(".ogg") || arg1.endsWith(".mp3"))
					return true;

				return false;
			}
		});

		Arrays.sort(musicFiles);
		
		String musicFiles2[] = new String[musicFiles.length + 1];
		musicFiles2[0] = "";
		
		for(int i=0; i < musicFiles.length; i++)
			musicFiles2[i + 1] = musicFiles[i];

		return musicFiles2;
	}	
}
