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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bladecoder.bladeengine.composer.Ctx;
import org.bladecoder.bladeengine.composer.model.Project;
import org.bladecoder.bladeengine.composer.ui.components.CellRenderer;
import org.bladecoder.bladeengine.composer.ui.components.EditList;
import org.bladecoder.bladeengine.composer.utils.DesktopUtils;

import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver.Resolution;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ResolutionList extends EditList<Resolution> {

	public ResolutionList(Skin skin) {
		super(skin);
		toolbar.hideCopyPaste();		

		list.setCellRenderer(listCellRenderer);
		
		list.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int pos = list.getSelectedIndex();

				toolbar.disableEdit(pos == -1);
			}
		});

		Ctx.project.addPropertyChangeListener(Project.NOTIFY_PROJECT_LOADED,
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent arg0) {
						toolbar.disableCreate(Ctx.project.getProjectDir() == null);
						addResolutions();
					}
				});
	}

	private void addResolutions() {
		if (Ctx.project.getProjectDir() != null) {

			list.getItems().clear();

			ArrayList<Resolution> tmp = new ArrayList<Resolution>();

			for (Resolution scn : Ctx.project.getResolutions()) {
				tmp.add(scn);
			}

			Collections.sort(tmp, new Comparator<Resolution>() {
				@Override
				public int compare(Resolution o1, Resolution o2) {
					return o2.portraitWidth - o1.portraitWidth;
				}
			});

			for (Resolution s : tmp)
				list.getItems().add(s);

			if (list.getItems().size > 0) {
				list.setSelectedIndex(0);
			}
		}

		toolbar.disableCreate(Ctx.project.getProjectDir() == null);
	}

	@Override
	public void create() {
		CreateResolutionDialog dialog = new CreateResolutionDialog(skin);
		dialog.show(getStage());
		dialog.setListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				addResolutions();
			}});
	}

	@Override
	public void edit() {

	}

	@Override
	public void delete() {
		int index = list.getSelectedIndex();
		Resolution r = list.getItems().get(index);

		removeDir(Ctx.project.getProjectDir() + "/" + Project.BACKGROUNDS_PATH
				+ "/" + r.suffix);
		removeDir(Ctx.project.getProjectDir() + "/" + Project.OVERLAYS_PATH
				+ "/" + r.suffix);
		removeDir(Ctx.project.getProjectDir() + "/" + Project.UI_PATH + "/"
				+ r.suffix);
		removeDir(Ctx.project.getProjectDir() + "/" + Project.ATLASES_PATH
				+ "/" + r.suffix);

		addResolutions();
	}

	private void removeDir(String dir) {
		try {
			DesktopUtils.removeDir(dir);
		} catch (IOException e) {
			String msg = "Something went wrong while deleting the resolution.\n\n"
					+ e.getClass().getSimpleName() + " - " + e.getMessage();
			Ctx.msg.show(getStage(),msg, 2);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void copy() {
	
	}

	@Override
	protected void paste() {
	
	}

	// -------------------------------------------------------------------------
	// ListCellRenderer
	// -------------------------------------------------------------------------
	private static final CellRenderer<Resolution> listCellRenderer = new CellRenderer<Resolution>() {
		@Override
		protected String getCellTitle(Resolution r) {
			return r.suffix;
		}
	};
}
