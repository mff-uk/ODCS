/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.dpu.InstanceConfiguration;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ModuleDialogGetter;

/**
 *
 * @author Bogo
 */
public class DPUDetail extends Window {

	private DPUInstance dpu;
	private DPUExecutive dpuExec;
	private TextField dpuName;
	private TextArea dpuDescription;


	/**
	 * Basic constructor, takes DPUInstance which detail should be showed.
	 * @param dpu
	 */
	public DPUDetail(DPUInstance dpu) {

		this.setResizable(false);
		this.setModal(true);
		this.dpu = dpu;
		this.setCaption(String.format("%s detail", dpu.getName()));

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		GridLayout dpuGeneralSettingsLayout = new GridLayout(2, 2);
		dpuGeneralSettingsLayout.setSpacing(true);

		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(nameLabel, 0, 0);

		dpuName = new TextField();
		dpuName.setImmediate(false);
		dpuName.setWidth("200px");
		dpuName.setHeight("-1px");
		dpuName.setValue(dpu.getName());
		dpuName.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				setCaption(dpuName.getValue());
			}
		});
		dpuGeneralSettingsLayout.addComponent(dpuName, 1, 0);

		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(descriptionLabel, 0, 1);

		dpuDescription = new TextArea();
		dpuDescription.setImmediate(false);
		dpuDescription.setWidth("400px");
		dpuDescription.setHeight("60px");
		dpuDescription.setValue(dpu.getDescription());
		dpuGeneralSettingsLayout.addComponent(dpuDescription, 1, 1);

		dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true, false));
		mainLayout.addComponent(dpuGeneralSettingsLayout);

		String jarPath = dpu.getDpu().getJarPath();
		//String jarPath = "file:///C:/Users/Bogo/intlib/intlib/module/target/module-0.0.1.jar";

		try {
			dpuExec = App.getApp().getModules().getInstance(jarPath);

			// get configuration from dpu
			Configuration conf = dpu.getInstanceConfig();

			if (dpuExec != null) {

				if (conf == null) {
					// create new default configuration
					conf = new InstanceConfiguration();
					dpuExec.saveConfigurationDefault(conf);
				}

				CustomComponent dpuConfigurationDialog = ModuleDialogGetter.getDialog(dpuExec, conf);
				dpuConfigurationDialog.setWidth("100%");
				mainLayout.addComponent(dpuConfigurationDialog);
			}

		} catch (ModuleException me) {
			//TODO: Show info about failed load of custom part of dialog
			Notification.show("ModuleException:Failed to load configuration dialog.", me.getTraceMessage(), Type.ERROR_MESSAGE);
		} catch (ConfigurationException ce) {
			//TODO: Show info about invalid saved config(should not happen -> validity check on save)
			Notification.show("ConfigurationException: Failed to set configuration for dialog.",
					ce.getMessage(), Type.ERROR_MESSAGE);
		}

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

//		Button saveButton = new Button("Develop", new Button.ClickListener() {
//
//			@Override
//			public void buttonClick(Button.ClickEvent event) {
//				if(saveDPUInstance()) {
//					close();
//				}
//			}
//		});
//		buttonBar.addComponent(saveButton);

		Button saveAndCommitButton = new Button("Save", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if(saveDPUInstance()) {
					close();
				}
			}
		});
		buttonBar.addComponent(saveAndCommitButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		buttonBar.addComponent(cancelButton);

		Button saveAsNewButton = new Button("Save to DPU tree", new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if(saveDpuAsNew()) {
					close();
				}
			}
		});
		buttonBar.addComponent(saveAsNewButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	/**
	 * Saves configuration of DPU Instance which was set in detail dialog.
	 * @return
	 */
	protected boolean saveDPUInstance() {

		try {
			if(dpuExec != null) {
				// TODO: Used configuration from DPUInstance instead creating new one?
				InstanceConfiguration conf = new InstanceConfiguration();
				dpuExec.saveConfiguration(conf);
				dpu.setInstanceConfig(conf);
			}
			dpu.setName(dpuName.getValue());
			dpu.setDescription(dpuDescription.getValue());
		} catch (ConfigurationException ce) {
			Notification.show("ConfigurationException:", ce.getMessage(), Type.ERROR_MESSAGE);
			//TODO: Inform about invalid settings and do not close detail dialog
			return false;
		}
		return true;
	}

	protected boolean saveDpuAsNew() {
		if(saveDPUInstance()) {
			DPU newDPU = App.getDPUs().createDpuFromInstance(dpu);
			App.getDPUs().save(newDPU);
			return true;
		}
		return false;
	}
}
