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
package org.bladecoder.bladeengine.composer.scneditor;

import org.bladecoder.bladeengine.anim.AtlasFrameAnimation;
import org.bladecoder.bladeengine.anim.FrameAnimation;
import org.bladecoder.bladeengine.anim.Tween;
import org.bladecoder.bladeengine.assets.EngineAssetManager;
import org.bladecoder.bladeengine.model.AtlasRenderer;
import org.bladecoder.bladeengine.model.SpineRenderer;
import org.bladecoder.bladeengine.model.Sprite3DRenderer;
import org.bladecoder.bladeengine.model.SpriteRenderer;
import org.bladecoder.bladeengine.util.RectangleRenderer;
import org.bladecoder.bladeengine.composer.model.ChapterDocument;
import org.bladecoder.bladeengine.composer.ui.EditSpriteDialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class SpriteWidget extends Widget {
	private String source;
	private FrameAnimation fa;
	private SpriteRenderer renderer;
	EditSpriteDialog editFADialog;
	

	public SpriteWidget(EditSpriteDialog createEditFADialog) {
		this.editFADialog = createEditFADialog;
	}

	
	public void setSource(String type, String source) {
		this.source = source;
		
		if(renderer != null) {
			renderer.dispose();
			renderer = null;
		}
		
		
		if(type.equals(ChapterDocument.SPRITE3D_ACTOR_TYPE)) {
			renderer = new Sprite3DRenderer();
			((Sprite3DRenderer)renderer).setSpriteSize(new Vector2( Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		} else if(type.equals(ChapterDocument.SPINE_ACTOR_TYPE)) {
			renderer = new SpineRenderer();
		} else {
			renderer = new AtlasRenderer();
		}
		
		renderer.loadAssets();
		EngineAssetManager.getInstance().finishLoading();
		renderer.retrieveAssets();
	}
	
	public String[] getAnimations() {
		return renderer.getInternalAnimations(source);
	}
	
	public void setFrameAnimation(String id, String speedStr, String typeStr) {
		if (source!=null && id != null && !source.isEmpty() && !id.isEmpty()) {
			
			int type = Tween.REPEAT;
			float speed = 2.0f;
			
			if(!speedStr.isEmpty())
				speed = Float.parseFloat(speedStr);
			
			if(typeStr.equals("yoyo"))
				type = Tween.PINGPONG;
			
			if(renderer instanceof AtlasRenderer)
				fa = new AtlasFrameAnimation();
			else 
				fa = new FrameAnimation();
			
			fa.set(id, source, speed, 0.0f, Tween.INFINITY, type,
					null, null, null, false, true);
			
			renderer.addFrameAnimation(fa);
			
			renderer.startFrameAnimation(fa.id, Tween.FROM_FA, 1, null);
		}
	}
	
	public void draw (Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		if(renderer == null || renderer.getCurrentFrameAnimation() == null)
			return;
		
		Color tmp = batch.getColor();
		batch.setColor(Color.WHITE);
		
		renderer.update(Gdx.graphics.getDeltaTime());
		
		RectangleRenderer.draw((SpriteBatch)batch, getX(), getY(), getWidth(), getHeight(), Color.MAGENTA);
		
		float scalew =   getWidth() /  renderer.getWidth();
		float scaleh =   getHeight() /  renderer.getHeight();
		float scale = scalew>scaleh?scaleh:scalew;
		renderer.draw((SpriteBatch)batch, getX() + renderer.getWidth() * scale /2, getY(), scale);
		batch.setColor(tmp);
	}

	public void dispose() {
		renderer.dispose();
	}
}
