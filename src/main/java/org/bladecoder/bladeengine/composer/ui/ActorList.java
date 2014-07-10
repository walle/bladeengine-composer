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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.bladecoder.bladeengine.composer.Ctx;
import org.bladecoder.bladeengine.composer.model.ChapterDocument;
import org.bladecoder.bladeengine.composer.model.Project;
import org.bladecoder.bladeengine.composer.ui.components.CellRenderer;
import org.bladecoder.bladeengine.composer.ui.components.EditElementDialog;
import org.bladecoder.bladeengine.composer.ui.components.ElementList;
import org.w3c.dom.Element;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ActorList extends ElementList {

	private ImageButton playerBtn;

	public ActorList(Skin skin) {
		super(skin, true);

		playerBtn = new ImageButton(skin);
		toolbar.addToolBarButton(playerBtn, "ic_check",
				"Set player", "Set player");
		playerBtn.setDisabled(true);

		list.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
//				EditorLogger.debug("ACTOR LIST ELEMENT SELECTED");
				int pos = list.getSelectedIndex();

				if (pos == -1) {
					Ctx.project.setSelectedActor(null);
				} else {
					Element a = list.getItems().get(pos);
					Ctx.project.setSelectedActor(a);
				}

				toolbar.disableEdit(pos == -1);
				playerBtn.setDisabled(pos== -1);
			}
		});

		playerBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setPlayer();
			}
		});

		list.setCellRenderer(listCellRenderer);

		Ctx.project.addPropertyChangeListener(Project.NOTIFY_ACTOR_SELECTED,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						int pos = list.getSelectedIndex();

						// Element newActor = (Element) e.getNewValue();
						Element newActor = Ctx.project.getSelectedActor();

						if (newActor == null)
							return;

						if (pos != -1) {
							Element oldActor = list.getItems().get(pos);

							if (oldActor == newActor) {
								return;
							}
						}

						int i = list.getItems().indexOf(newActor, true);
						if(i >= 0)
							list.setSelectedIndex(i);
					}
				});
	}

	@Override
	protected EditElementDialog getEditElementDialogInstance(
			Element e) {
		return new EditActorDialog(skin, doc, parent, e);
	}

	private void setPlayer() {
		ChapterDocument scn = (ChapterDocument) doc;

		int pos = list.getSelectedIndex();

		if (pos == -1)
			return;

		Element e = list.getItems().get(pos);

		if (e.getAttribute("type").equals(ChapterDocument.SPRITE3D_ACTOR_TYPE)
				|| e.getAttribute("type").equals(ChapterDocument.ATLAS_ACTOR_TYPE)
				|| e.getAttribute("type").equals(ChapterDocument.SPINE_ACTOR_TYPE)
				) {
			String id = e.getAttribute("id");

			scn.setRootAttr(parent, "player", id);
		}
	}

	// -------------------------------------------------------------------------
	// ListCellRenderer
	// -------------------------------------------------------------------------
	private final CellRenderer<Element> listCellRenderer = new CellRenderer<Element>() {

		@Override
		protected String getCellTitle(Element e) {
			return e.getAttribute("id");
		}

		@Override
		protected String getCellSubTitle(Element e) {
			return doc.getTranslation(e.getAttribute("desc"));
		}

		@Override
		public TextureRegion getCellImage(Element e) {
			String type = e.getAttribute("type");

			boolean isPlayer = ((Element)e.getParentNode()).getAttribute("player").equals(
					e.getAttribute("id"));

			String u = null;
			
			if (isPlayer) {
				u = "ic_character_actor";
			} else if (type.equals(ChapterDocument.FOREGROUND_ACTOR_TYPE)) {
				u = "ic_fg_actor";
			} else if (type.equals(ChapterDocument.ATLAS_ACTOR_TYPE)) {
				u = "ic_sprite_actor";
			} else if (type.equals(ChapterDocument.BACKGROUND_ACTOR_TYPE)) {
				u = "ic_base_actor";
			} else if (type.equals(ChapterDocument.SPINE_ACTOR_TYPE)) {
				u = "ic_spine";			
			} else if (type.equals(ChapterDocument.SPRITE3D_ACTOR_TYPE)) {
				u = "ic_3d";
			}

			return Ctx.assetManager.getIcon(u);
		}
		
		@Override
		protected boolean hasSubtitle() {
			return true;
		}
		
		@Override
		protected boolean hasImage() {
			return true;
		}
	};
}
