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
import org.bladecoder.bladeengine.composer.utils.EditorLogger;
import org.w3c.dom.Element;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ActorProps extends PropertyTable {

	public static final String DESC_PROP = "Description";
	public static final String POS_X_PROP = "pos X";
	public static final String POS_Y_PROP = "pos Y";
	public static final String VISIBLE_PROP = "visible";
	public static final String ACTIVE_PROP = "active";
	public static final String STATE_PROP = "state";
	public static final String WALKING_SPEED_PROP = "Walking Speed";

	private ChapterDocument doc;
	private Element actor;

//	TableModelListener tableModelListener = new TableModelListener() {
//		@Override
//		public void tableChanged(TableModelEvent e) {
//			if (e.getType() == TableModelEvent.UPDATE) {
//				int row = e.getFirstRow();
//				updateModel((String) propertyTable.getModel().getValueAt(row, 0),
//						(String) propertyTable.getModel().getValueAt(row, 1));
//			}
//		}
//	};
	
	PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			EditorLogger.debug("Property Listener: " + evt.getPropertyName());
			setActorDocument(doc, actor);			
		}
	};

	public ActorProps(Skin skin) {
		super(skin);
	}

	public void setActorDocument(ChapterDocument doc, Element a) {		
		this.doc = doc;
		this.actor = a;
		clear();

		if (a != null) {

			if (!a.getAttribute("type").equals("background")) {
				Vector2 pos = doc.getPos(a);
				addProperty(POS_X_PROP, Float.toString(pos.x), Types.FLOAT);
				addProperty(POS_Y_PROP, Float.toString(pos.y), Types.FLOAT);
			}

			addProperty(DESC_PROP, doc.getRootAttr(a, "desc"));

			addProperty(VISIBLE_PROP, doc.getRootAttr(a, "visible"), Types.BOOLEAN);

			addProperty(ACTIVE_PROP, doc.getRootAttr(a, "active"), Types.BOOLEAN);
			addProperty(STATE_PROP, doc.getRootAttr(a, "state"));
			
			
			if (!a.getAttribute("type").equals("background") && 
				!a.getAttribute("type").equals("foreground")) {
				addProperty(WALKING_SPEED_PROP, doc.getRootAttr(a, "walking_speed"));
			}
			
			doc.addPropertyChangeListener(propertyChangeListener);
			
			invalidateHierarchy();
		}
	}

	@Override
	protected void updateModel(String property, String value) {
		if (property.equals(DESC_PROP)) {
			doc.setRootAttr(actor, "desc", value);
		} else if (property.equals(POS_X_PROP)) {
			Vector2 pos = doc.getPos(actor);
			pos.x = Float.parseFloat(value);
			doc.setPos(actor, pos);
		} else if (property.equals(POS_Y_PROP)) {
			Vector2 pos = doc.getPos(actor);
			pos.y = Float.parseFloat(value);
			doc.setPos(actor, pos);
		} else if (property.equals(VISIBLE_PROP)) {
			doc.setRootAttr(actor, "visible", value);
		} else if (property.equals(ACTIVE_PROP)) {
			doc.setRootAttr(actor, "active", value);
		} else if (property.equals(STATE_PROP)) {
			doc.setRootAttr(actor, "state", value);
		} else if (property.equals(WALKING_SPEED_PROP)) {
			doc.setRootAttr(actor, "walking_speed", value);			
		}

	}
}
