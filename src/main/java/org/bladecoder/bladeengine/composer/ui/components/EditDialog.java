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
package org.bladecoder.bladeengine.composer.ui.components;

import org.bladecoder.bladeengine.composer.utils.EditorLogger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public abstract class EditDialog extends Dialog {

	private Cell<Widget> infoCell;
	private Label infoLbl;

	private Table centerPanel;

	boolean cancelled = false;

	private Skin skin;

	public EditDialog(String title, Skin skin) {
		super(title, skin);

		this.skin = skin;

		setResizable(false);

		infoLbl = new Label("", skin);
		infoLbl.setWrap(true);
		centerPanel = new Table(skin);
		infoCell = getContentTable().add((Widget) infoLbl).prefWidth(200);
		getContentTable().add(new ScrollPane(centerPanel, skin))
				.maxHeight(Gdx.graphics.getHeight() * 0.8f)
				.maxWidth(Gdx.graphics.getWidth() * 0.7f);
		
		centerPanel.addListener(new InputListener() {
			@Override
			public void enter(InputEvent event, float x, float y, int pointer,  com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
				EditorLogger.debug("ENTER - X: " + x + " Y: " + y);
				getStage().setScrollFocus(centerPanel);
			}
		});

		button("OK", true);
		button("Cancel", false);
		key(Keys.ENTER, true);
		key(Keys.ESCAPE, false);

		padBottom(10);
		padLeft(10);
		padRight(10);
	}
	
	public void addInputPanel(InputPanel i) {
		getCenterPanel().row().fill().expandX();
		getCenterPanel().add(i);
	}
	
	public void setVisible(InputPanel i, boolean v) {
		i.setVisible(v);
		Cell<InputPanel> c = getCenterPanel().getCell(i);	
		
		if(v) {
			c.height(i.getPrefHeight());
		} else {
			c.height(1);
		}
		
		i.invalidateHierarchy();
	}	

	public Skin getSkin() {
		return skin;
	}

	public Table getCenterPanel() {
		return centerPanel;
	}

	public void setInfo(String text) {
		infoLbl.setText(text);
	}

	public void setInfoWidget(Widget c) {
		infoCell.setActor(null);
		infoCell.setActor(c).fill();
	}

	public void setTitle(String title) {
		super.setTitle(title);
	}

	public boolean isCancel() {
		return cancelled;
	}

	protected void result(Object object) {
		if (((boolean) object) == true) {
			if (validateFields()) {
				ok();
			} else {
				cancel();
			}
		} else {
			cancelled = true;
		}
	}

	abstract protected boolean validateFields();

	abstract protected void ok();
}
