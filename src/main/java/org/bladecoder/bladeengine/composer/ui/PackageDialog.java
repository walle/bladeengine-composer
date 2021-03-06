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
import java.io.IOException;
import java.util.Properties;

import org.bladecoder.bladeengine.composer.Ctx;
import org.bladecoder.bladeengine.composer.ui.components.EditDialog;
import org.bladecoder.bladeengine.composer.ui.components.FileInputPanel;
import org.bladecoder.bladeengine.composer.ui.components.InputPanel;
import org.bladecoder.bladeengine.composer.utils.RunProccess;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PackageDialog extends EditDialog {
	private static final String ARCH_PROP = "package.arch";
	private static final String DIR_PROP = "package.dir";
	
	private static final String INFO = "Package the Adventure for distribution";
	private static final String[] ARCHS = { "desktop", "android", "html", "ios" };
	private static final String[] TYPES = { "Bundle JRE", "Runnable jar" };
	private static final String[] OSS = { "all", "windows", "linux64", "linux32", "macOSX" };

	private InputPanel arch;
	private InputPanel dir;
	private InputPanel type;
	private InputPanel os;
	private InputPanel linux64JRE;
	private InputPanel linux32JRE;
	private InputPanel winJRE;
	private InputPanel version;
	private InputPanel icon;
	private InputPanel androidSDK;
	private InputPanel androidKeyStore;
	private InputPanel androidKeyAlias;
	private InputPanel androidKeyStorePassword;
	private InputPanel androidKeyAliasPassword;	

	private InputPanel[] options = new InputPanel[10];

	@SuppressWarnings("unchecked")
	public PackageDialog(Skin skin) {
		super("PACKAGE ADVENTURE", skin);
		
		arch = new InputPanel(skin, "Architecture", "Select the target OS for the game",
				ARCHS);
		dir = new FileInputPanel(skin, "Output Directory",
				"Select the output directory to put the package", true);
		type = new InputPanel(skin, "Type", "Select the type of the package", TYPES);
		os = new InputPanel(skin, "OS", "Select the OS of the package", OSS);
		linux64JRE = new FileInputPanel(skin, "JRE.Linux64", "Select the 64 bits Linux JRE Location to bundle", true);
		linux32JRE = new FileInputPanel(skin, "JRE.Linux32", "Select the 32 bits Linux JRE Location to bundle", true);
		winJRE = new FileInputPanel(skin, "JRE.Windows", "Select the Windows JRE Location to bundle", true);
		version = new InputPanel(skin, "Version", "Select the version of the package");
		icon = new FileInputPanel(skin, "Icon", "The icon for the .exe file", false);
		androidSDK = new FileInputPanel(skin, "SDK", "Select the Android SDK Location", true);
		androidKeyStore = new FileInputPanel(skin, "KeyStore", "Select the Key Store Location", false);
		androidKeyAlias = new InputPanel(skin, "KeyAlias", "Select the Key Alias Location");
		androidKeyStorePassword = new InputPanel(skin, "KeyStorePasswd", "Key Store Password", false);
		androidKeyAliasPassword = new InputPanel(skin, "KeyAliasPasswd", "Key Alias Password", false);
		
		options[0] = type;
		options[1] = os;
		options[2] = linux64JRE;
		options[3] = linux32JRE;
		options[4] = winJRE;
		options[5] = version;
		options[6] = icon;
		options[7] = androidSDK;
		options[8] = androidKeyStore;
		options[9] = androidKeyAlias;
		
		addInputPanel(arch);
		addInputPanel(dir);
		
		for(InputPanel i: options) {
			addInputPanel(i);
			i.setMandatory(true);
		}
		
		addInputPanel(androidKeyStorePassword);
		addInputPanel(androidKeyAliasPassword);
		
		dir.setMandatory(true);
		
		arch.setText(Ctx.project.getConfig().getProperty(ARCH_PROP, ARCHS[0]));
		dir.setText(Ctx.project.getConfig().getProperty(DIR_PROP, ""));
		
		
		for(InputPanel i: options) {
			String prop = Ctx.project.getConfig().getProperty("package." + i.getTitle());
			
			if(prop != null && !prop.isEmpty())
				i.setText(prop);
		}
		
		if(linux64JRE.getText().isEmpty()) {
			if(new File("./jre-linux64").exists()) {
				linux64JRE.setText(new File("./jre-linux64").getAbsolutePath());
			} else if(new File("../engine-dist/jre-linux64").exists()) {
				linux64JRE.setText(new File("../engine-dist/jre-linux64").getAbsolutePath());
			}
		}
		
		if(linux32JRE.getText().isEmpty()) {
			if(new File("./jre-linux32").exists()) {
				linux32JRE.setText(new File("./jre-linux32").getAbsolutePath());
			} else if(new File("../engine-dist/jre-linux32").exists()) {
				linux32JRE.setText(new File("../engine-dist/jre-linux32").getAbsolutePath());
			}
		}
		
		if(winJRE.getText().isEmpty()) {
			if(new File("./jre-win").exists()) {
				winJRE.setText(new File("./jre-win").getAbsolutePath());
			} else if(new File("../engine-dist/jre-win").exists()) {
				winJRE.setText(new File("../engine-dist/jre-win").getAbsolutePath());
			}
		}

		setInfo(INFO);

		((SelectBox<String>) (arch.getField())).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				archChanged();
			}
		});

		((SelectBox<String>) (type.getField())).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				typeChanged();
			}
		});

		((SelectBox<String>) (os.getField())).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				osChanged();
			}
		});
		
		archChanged();
	}

	@Override
	protected void ok() {

		Ctx.msg.show(getStage(), "Generating package...");
	
		String msg = packageAdv();
		Ctx.msg.show(getStage(), msg, 2);

		Ctx.project.getConfig().setProperty(ARCH_PROP, arch.getText());
		Ctx.project.getConfig().setProperty(DIR_PROP, dir.getText());
		
		for(InputPanel i: options) {
			Ctx.project.getConfig().setProperty("package." + i.getTitle(), i.getText());
		}

	}

	private String packageAdv() {
		String antBuild = null;
		String antTarget = null;
		Properties props = new Properties();
		String msg = "Package generated SUCCESSFULLY";
		
		props.setProperty("title", Ctx.project.getTitle());
		props.setProperty("name", Ctx.project.getPackageTitle());
		props.setProperty("version", version.getText());
		

		if (arch.getText().equals("desktop")) {
			antBuild = "desktop.ant.xml";

			if (type.getText().equals(TYPES[0])) { // RUNNABLE JAR
				if (os.getText().equals("linux64")) {
					antTarget = "linux64";
					props.setProperty("linux64.jre", linux64JRE.getText());
				} else if (os.getText().equals("linux32")) {
					antTarget = "linux32";
					props.setProperty("linux32.jre", linux32JRE.getText());
				} else if (os.getText().equals("windows")) {
					antTarget = "win";
					props.setProperty("icon", icon.getText());
					props.setProperty("win.jre", winJRE.getText());
				} else if (os.getText().equals("all")) {
					antTarget = "dist";
					props.setProperty("icon", icon.getText());
					props.setProperty("linux64.jre", linux64JRE.getText());
					props.setProperty("linux32.jre", linux32JRE.getText());
					props.setProperty("win.jre", winJRE.getText());
				}
			} else {
				antTarget = "jar";
			}
		} else if (arch.getText().equals("android")) {
			antBuild = "android.ant.xml";
			antTarget = "dist";
			props.setProperty("sdk.dir", androidSDK.getText());
			props.setProperty("target", "android-15");
			props.setProperty("project.app.package", "org.bladecoder." + Ctx.project.getPackageTitle().toLowerCase()); // ANDROID PACKAGE
			props.setProperty("version.code", "10"); // TODO
			
			props.setProperty("key.store", androidKeyStore.getText());
			props.setProperty("key.alias", androidKeyAlias.getText());
			props.setProperty("key.store.password", androidKeyStorePassword.getText());
			props.setProperty("key.alias.password", androidKeyAliasPassword.getText());
//			props.setProperty("aapt.ignore.assets=1920_1080:tests");		
		}

		if (antTarget != null) {

			try {
				RunProccess.runAnt(antBuild, antTarget, dir.getText(),
						Ctx.project.getProjectPath(), props);
			} catch (IOException e) {
				msg = "Error Generating package\n\n" + e.getMessage();
			}
		} else {
			msg = "Packaging option NOT implemented yet.\n\n";
		}

		return msg;
	}

	private void archChanged() {
		for (InputPanel ip : options) {
			setVisible(ip, false);
		}
		
		setVisible(androidKeyStorePassword,false);
		setVisible(androidKeyAliasPassword,false);
		setVisible(version,true);

		String a = arch.getText();
		if (a.equals("desktop")) {
			setVisible(type,true);
			typeChanged();
		} else if (a.equals("android")) {
			setVisible(androidSDK,true);
			setVisible(androidKeyStore,true);
			setVisible(androidKeyAlias,true);
			setVisible(androidKeyStorePassword,true);
			setVisible(androidKeyAliasPassword,true);
		}
	}

	private void typeChanged() {
		if (type.getText().equals(TYPES[0])) {
			setVisible(os,true);
		} else {
			setVisible(os,false);
			setVisible(icon,false);
		}
		
		osChanged();
	}

	private void osChanged() {
		if (os.isVisible() && (os.getText().equals("windows") || os.getText().equals("all"))) {
			setVisible(icon,true);
			setVisible(winJRE,true);
		} else {
			setVisible(icon,false);
			setVisible(winJRE,false);
		}
		
		if (os.isVisible() && (os.getText().equals("linux32") || os.getText().equals("all"))) {
			setVisible(linux32JRE,true);
		} else {
			setVisible(linux32JRE,false);
		}
		
		if (os.isVisible() && (os.getText().equals("linux64") || os.getText().equals("all"))) {
			setVisible(linux64JRE,true);
		} else {
			setVisible(linux64JRE, false);
		}
	}

	@Override
	protected boolean validateFields() {
		boolean ok = true;

		if (!dir.validateField())
			ok = false;

		for(InputPanel i: options) {
			if (i.isVisible() && !i.validateField())
				ok = false;
		}
		
		if (androidKeyStorePassword.isVisible() && !androidKeyStorePassword.validateField())
			ok = false;

		if (androidKeyAliasPassword.isVisible() && !androidKeyAliasPassword.validateField())
			ok = false;		

		if(icon.isVisible() && !icon.getText().endsWith(".ico")) {
			icon.setError(true);
			ok = false;
		}
		
		if(linux32JRE.isVisible() && !new File(linux32JRE.getText() + "/bin/java").exists())
			ok = false;
		
		if(linux64JRE.isVisible() && !new File(linux64JRE.getText() + "/bin/java").exists())
			ok = false;

		if(winJRE.isVisible() && !new File(winJRE.getText() + "/bin/javaw.exe").exists())
			ok = false;
		
		return ok;
	}
}
