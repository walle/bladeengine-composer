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
import org.bladecoder.bladeengine.composer.model.Project;
import org.bladecoder.bladeengine.composer.ui.components.PropertyTable;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;


public class WorldProps extends PropertyTable {

	public static final String WIDTH_PROP = "width";
	public static final String HEIGHT_PROP = "height";
	
	public static final String TITLE_PROP = "title";
	
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

	public WorldProps(Skin skin) {
		super(skin);

		Ctx.project.addPropertyChangeListener(Project.NOTIFY_PROJECT_LOADED,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						setProject();
					}
				});
	}

	@Override
	protected void updateModel(String property, String value) {
		if (property.equals(WIDTH_PROP)) {
			Ctx.project.getWorld().setWidth(value);
		} else if (property.equals(TITLE_PROP)) {
			Ctx.project.setProjectProperty(TITLE_PROP, value);
		} else if (property.equals(HEIGHT_PROP)) {
			Ctx.project.getWorld().setHeight(value);
		}
	}

	private void setProject() {
		clear();
		addProperty(WIDTH_PROP, Ctx.project.getWorld().getWidth());
		addProperty(HEIGHT_PROP, Ctx.project.getWorld().getHeight());		
		addProperty(TITLE_PROP, Ctx.project.getProjectProperty(TITLE_PROP));
		
		invalidateHierarchy();
	}
}
