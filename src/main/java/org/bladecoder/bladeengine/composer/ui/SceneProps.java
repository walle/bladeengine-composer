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

import org.bladecoder.bladeengine.composer.model.ChapterDocument;
import org.bladecoder.bladeengine.composer.ui.components.PropertyTable;
import org.w3c.dom.Element;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class SceneProps extends PropertyTable {
	public static final String MUSIC_PROP = "Music filename";
	public static final String LOOP_MUSIC_PROP = "Loop Music";
	public static final String BACKGROUND_PROP = "Background";
	public static final String LIGHTMAP_PROP = "Light Map";
	public static final String INITIAL_MUSIC_DELAY_PROP = "Initial Music Delay";
	public static final String REPEAT_MUSIC_DELAY_PROP = "Repeat Music Delay";
	
	ChapterDocument doc;
	Element scn;
	
//	TableModelListener tableModelListener = new TableModelListener() {
//		@Override
//		public void tableChanged(TableModelEvent e) {
//			if(e.getType() == TableModelEvent.UPDATE) {
//				int row = e.getFirstRow();
//				updateModel((String) propertyTable.getModel()
//					.getValueAt(row, 0), (String) propertyTable
//					.getModel().getValueAt(row, 1));
//			}
//		}
//	};
	
	PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {			
			setSceneDocument(doc, scn);			
		}
	};

	public SceneProps(Skin skin) {
		super(skin);
	}

	public void setSceneDocument(ChapterDocument doc, Element scn) {

		this.scn = scn;
		this.doc = doc;
		
		clear();

		if (scn != null) {
			addProperty(BACKGROUND_PROP, doc.getBackground(scn));
			addProperty(LIGHTMAP_PROP, doc.getLightmap(scn));
			addProperty(MUSIC_PROP, doc.getMusic(scn));
			addProperty(LOOP_MUSIC_PROP, doc.getRootAttr(scn,"loop_music"), Types.BOOLEAN);
			addProperty(INITIAL_MUSIC_DELAY_PROP, doc.getRootAttr(scn,"initial_music_delay"), Types.FLOAT);
			addProperty(REPEAT_MUSIC_DELAY_PROP, doc.getRootAttr(scn,"repeat_music_delay"), Types.FLOAT);
			
			this.doc.addPropertyChangeListener("scene", propertyChangeListener);
			
			invalidateHierarchy();
		}	
	}

	@Override
	protected void updateModel(String property, String value) {
		if (property.equals(MUSIC_PROP)) {
			doc.setRootAttr(scn,"music", value);
		} else if (property.equals(LOOP_MUSIC_PROP)) {
			doc.setRootAttr(scn,"loop_music", value);
		} else if (property.equals(BACKGROUND_PROP)) {
			doc.setBackground(scn,value);
		} else if (property.equals(LIGHTMAP_PROP)) {
			doc.setLightmap(scn,value);			
		} else if (property.equals(INITIAL_MUSIC_DELAY_PROP)) {
			doc.setRootAttr(scn,"initial_music_delay", value);
		} else if (property.equals(REPEAT_MUSIC_DELAY_PROP)) {
			doc.setRootAttr(scn,"repeat_music_delay", value);
		}
	}
}
