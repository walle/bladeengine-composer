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

import org.bladecoder.bladeengine.actions.Param;
import org.bladecoder.bladeengine.composer.model.BaseDocument;
import org.bladecoder.bladeengine.composer.model.ChapterDocument;
import org.bladecoder.bladeengine.composer.ui.components.EditElementDialog;
import org.bladecoder.bladeengine.composer.ui.components.InputPanel;
import org.w3c.dom.Element;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class EditActorDialog extends EditElementDialog {

	public static final String TYPES_INFO[] = {
			"Background actors define an interactive area in the background",
			"Atlas actors allows 2d image and animations",
			"3d actors allows 3d models and animations",
			"Spine actors allows Spine 2d skeletal animations",
			"Actors always in the foreground. No interactive" };

	private InputPanel[] inputs = new InputPanel[11];
	InputPanel typePanel;

	String attrs[] = { "type", "id", "desc", "state", "interaction", "visible",
			"walking_speed", "depth_type", "sprite_size", "camera", "fov" };

	@SuppressWarnings("unchecked")
	public EditActorDialog(Skin skin, BaseDocument doc, Element parent,
			Element e) {
		super(skin);

		inputs[0] = new InputPanel(skin, "Actor Type",
				"Actors can be from different types",
				ChapterDocument.ACTOR_TYPES);

		inputs[1] = new InputPanel(skin, "Actor ID",
				"IDs can not contain '.' or '_' characters.", true);
		inputs[2] = new InputPanel(skin, "Description",
				"The text showed when the cursor is over the actor.");
		inputs[3] = new InputPanel(
				skin,
				"State",
				"Initial state of the actor. Actors can be in differentes states during the game.");
		inputs[4] = new InputPanel(skin, "Interaction",
				"True when the actor reacts to the user input.",
				Param.Type.BOOLEAN, false);
		inputs[5] = new InputPanel(skin, "Visible", "The actor visibility.",
				Param.Type.BOOLEAN, false);
		inputs[6] = new InputPanel(skin, "Walking Speed",
				"The walking speed in pix/sec. Default 700.", Param.Type.FLOAT,
				false);
		inputs[7] = new InputPanel(skin, "Depth Type",
				"Scene fake depth for scaling", new String[] { "none",
						"vector"});
		inputs[8] = new InputPanel(skin, "Sprite Dimensions",
				"The size of the 3d sprite", Param.Type.DIMENSION, true);
		inputs[9] = new InputPanel(skin, "Camera Name",
				"The name of the camera in the model", Param.Type.STRING, true,
				"Camera", null);
		inputs[10] = new InputPanel(skin, "Camera FOV",
				"The camera field of view", Param.Type.FLOAT, true, "49.3",
				null);

		setInfo(TYPES_INFO[0]);

		typePanel = inputs[0];

		((SelectBox<String>) typePanel.getField())
				.addListener(new ChangeListener() {

					@Override
					public void changed(ChangeEvent event, Actor actor) {
						typeChanged();
					}
				});

		init(inputs, attrs, doc, parent, "actor", e);

		typeChanged();

	}

	private void typeChanged() {
		int i = typePanel.getSelectedIndex();

		setInfo(TYPES_INFO[i]);

		setVisible(inputs[8],false);
		setVisible(inputs[9],false);
		setVisible(inputs[10],false);

		if (ChapterDocument.ACTOR_TYPES[i]
				.equals(ChapterDocument.SPRITE3D_ACTOR_TYPE)) {
			setVisible(inputs[8],true);
			setVisible(inputs[9],true);
			setVisible(inputs[10],true);
		}
	}

	@Override
	protected void fill() {
		int i = typePanel.getSelectedIndex();
		if (((ChapterDocument)doc).getBBox(e) == null && ChapterDocument.ACTOR_TYPES[i]
				.equals(ChapterDocument.BACKGROUND_ACTOR_TYPE)) {
			((ChapterDocument) doc).setBbox(e, null);
		}
		
		if(((ChapterDocument)doc).getPos(e) == null)
			((ChapterDocument) doc).setPos(e, new Vector2(0, 0));

		super.fill();
	}
}
