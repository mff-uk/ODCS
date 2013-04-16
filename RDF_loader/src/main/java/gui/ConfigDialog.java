package gui;

import module.Config;

import com.vaadin.ui.*;
import cz.cuni.xrg.intlib.commons.configuration.*;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Configuration dialog.
 * @author Petyr
 *
 */
public class ConfigDialog extends CustomComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * TODO Implement your own configuration component. You can use vaadin visual editor if you like.
	 * Just remember don't use vaddin classes the ere not located directly in package com.vaadi.ui;
	 */

	private GridLayout mainLayout;
//	private Button buttonCanc;
//	private Button buttonSave;
//	private Button buttonDevel;
	private TabSheet tabSheet;
	private VerticalLayout verticalLayoutDetails;
	private OptionGroup optionGroupDetail;
	private VerticalLayout verticalLayoutCore;
	private GridLayout gridLayoutAdm;
	private TextArea textAreaGraph; // Named Graph
	private Label labelGraph;
	private PasswordField passwordFieldPass; // Password
	private Label labelPass;
	private TextField textFieldNameAdm; // Host_name
	private Label labelNameAdm;
	private ComboBox comboBoxSparql; // SPARQL_endpoint
	private Label labelSparql;
	private GridLayout gridLayoutName;
	private TextArea textAreaDesc; // Description
	private Label labelDesc;
	private TextField textFieldName; // DPU_name
	private Label labelName;
	private HorizontalLayout horizontalLayout;

	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}
	private void mapData() {

		optionGroupDetail.addItem("Overwrite target graph");
		optionGroupDetail.addItem("Merge with target graph's content");
		optionGroupDetail.addItem("Fail if the target graph exists");
		optionGroupDetail.setValue("Overwrite target graph");
	}
	/**
	 * Return current configuration from dialog. Can return null, if
	 * current configuration is invalid.
	 * @return current configuration or null
	 */
	public Configuration getConfiguration() {
		Configuration config = new Configuration();
		/**
		 * TODO Gather data from you dialog and store them into configuration. You can use
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 * 	Also remember that you can return null in case of invalid configuration in dialog.
		 */

		config.setValue(Config.DPU_name.name(), textFieldName.getValue());
		config.setValue(Config.Description.name(), textAreaDesc.getValue());
		config.setValue(Config.SPARQL_endpoint.name(), comboBoxSparql.getValue());
		config.setValue(Config.Host_name.name(), textFieldNameAdm.getValue());
		config.setValue(Config.Password.name(), passwordFieldPass.getValue());
		config.setValue(Config.GraphsUri.name(), textAreaGraph.getValue());

		return config;
	}

	/**
	 * Load values from configuration into dialog.
	 * @throws ConfigurationException
	 * @param conf
	 */
	public void setConfiguration(Configuration conf) {
		/**
		 * TODO Load configuration from conf into dialog components. You can use
		 * 	enum Config to make sure that you don't miss spell the ids of values.
		 *  The ConfigurationException can be thrown in case of invalid configuration.
		 */

		try
		{
			textFieldName.setValue( (String) conf.getValue(Config.DPU_name.name()));
			textAreaDesc.setValue( (String) conf.getValue(Config.Description.name()));
			comboBoxSparql.setValue( (String) conf.getValue(Config.SPARQL_endpoint.name()));
			textFieldNameAdm.setValue( (String) conf.getValue(Config.Host_name.name()));
			passwordFieldPass.setValue( (String) conf.getValue(Config.Password.name()));
			textAreaGraph.setValue( (String) conf.getValue(Config.GraphsUri.name()));
		}
		catch(Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
	}

	private GridLayout buildMainLayout() {
		// common part: create layout

		mainLayout = new GridLayout(2, 2);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("360px");
		setHeight("450px");

		// tabSheet
		tabSheet = buildTabSheet();
		mainLayout.addComponent(tabSheet,  0, 0, 1, 0);
		mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);


		// buttonDevel
//		buttonDevel = new Button();
//		buttonDevel.setCaption("Develop");
//		buttonDevel.setImmediate(true);
//		buttonDevel.setWidth("80px");
//		buttonDevel.setHeight("-1px");
//		mainLayout.addComponent(buttonDevel, 0,1);
//		mainLayout.setComponentAlignment(buttonDevel, Alignment.TOP_LEFT);


		horizontalLayout = buildHorizontalLayout();
		mainLayout.addComponent(horizontalLayout,1,1);
		mainLayout.setComponentAlignment(horizontalLayout, Alignment.TOP_RIGHT);

		return mainLayout;
	}


	private TabSheet buildTabSheet() {
		// common part: create layout
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("360px");
		tabSheet.setHeight("340px");

		// verticalLayoutCore
		verticalLayoutCore = buildVerticalLayoutCore();
		tabSheet.addTab(verticalLayoutCore, "Core", null);

		// verticalLayoutDetails
		verticalLayoutDetails = buildVerticalLayoutDetails();
		tabSheet.addTab(verticalLayoutDetails, "Details", null);

		return tabSheet;
	}


	private VerticalLayout buildVerticalLayoutCore() {
		// common part: create layout
		verticalLayoutCore = new VerticalLayout();
		verticalLayoutCore.setImmediate(false);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("93.33%");
		verticalLayoutCore.setMargin(true);

		// gridLayoutName
		gridLayoutName = buildGridLayoutName();
		verticalLayoutCore.addComponent(gridLayoutName);

		// gridLayoutAdm
		gridLayoutAdm = buildGridLayoutAdm();
		verticalLayoutCore.addComponent(gridLayoutAdm);

		return verticalLayoutCore;
	}


	private GridLayout buildGridLayoutName() {
		// common part: create layout
		gridLayoutName = new GridLayout();
		gridLayoutName.setImmediate(false);
		gridLayoutName.setWidth("321px");
		gridLayoutName.setHeight("-1px");
		gridLayoutName.setMargin(false);
		gridLayoutName.setSpacing(true);
		gridLayoutName.setColumns(2);
		gridLayoutName.setRows(2);

		// labelName
		labelName = new Label();
		labelName.setImmediate(false);
		labelName.setWidth("-1px");
		labelName.setHeight("-1px");
		labelName.setValue("Name:");
		gridLayoutName.addComponent(labelName, 0, 0);

		// textFieldName
		textFieldName = new TextField();
		textFieldName.setImmediate(false);
		textFieldName.setWidth("256px");
		textFieldName.setHeight("-1px");
		gridLayoutName.addComponent(textFieldName, 1, 0);

		// labelDesc
		labelDesc = new Label();
		labelDesc.setImmediate(false);
		labelDesc.setWidth("-1px");
		labelDesc.setHeight("-1px");
		labelDesc.setValue("Description:");
		gridLayoutName.addComponent(labelDesc, 0, 1);

		// textAreaDesc
		textAreaDesc = new TextArea();
		textAreaDesc.setImmediate(false);
		textAreaDesc.setWidth("255px");
		textAreaDesc.setHeight("51px");
		gridLayoutName.addComponent(textAreaDesc, 1, 1);

		return gridLayoutName;
	}


	private GridLayout buildGridLayoutAdm() {
		// common part: create layout
		gridLayoutAdm = new GridLayout();
		gridLayoutAdm.setImmediate(false);
		gridLayoutAdm.setWidth("299px");
		gridLayoutAdm.setHeight("152px");
		gridLayoutAdm.setMargin(false);
		gridLayoutAdm.setColumns(2);
		gridLayoutAdm.setRows(4);

		// labelSparql
		labelSparql = new Label();
		labelSparql.setImmediate(false);
		labelSparql.setWidth("-1px");
		labelSparql.setHeight("-1px");
		labelSparql.setValue("SPARQL endpoint:");
		gridLayoutAdm.addComponent(labelSparql, 0, 0);

		// comboBoxSparql
		comboBoxSparql = new ComboBox();
		comboBoxSparql.setImmediate(false);
		comboBoxSparql.setWidth("197px");
		comboBoxSparql.setHeight("-1px");
		gridLayoutAdm.addComponent(comboBoxSparql, 1, 0);

		// labelNameAdm
		labelNameAdm = new Label();
		labelNameAdm.setImmediate(false);
		labelNameAdm.setWidth("-1px");
		labelNameAdm.setHeight("-1px");
		labelNameAdm.setValue("Name:");
		gridLayoutAdm.addComponent(labelNameAdm, 0, 1);

		// textFieldNameAdm
		textFieldNameAdm = new TextField();
		textFieldNameAdm.setImmediate(false);
		textFieldNameAdm.setWidth("197px");
		textFieldNameAdm.setHeight("-1px");
		gridLayoutAdm.addComponent(textFieldNameAdm, 1, 1);

		// labelPass
		labelPass = new Label();
		labelPass.setImmediate(false);
		labelPass.setWidth("-1px");
		labelPass.setHeight("-1px");
		labelPass.setValue("Password:");
		gridLayoutAdm.addComponent(labelPass, 0, 2);

		// passwordFieldPass
		passwordFieldPass = new PasswordField();
		passwordFieldPass.setImmediate(false);
		passwordFieldPass.setWidth("197px");
		passwordFieldPass.setHeight("-1px");
		gridLayoutAdm.addComponent(passwordFieldPass, 1, 2);

		// labelGraph
		labelGraph = new Label();
		labelGraph.setImmediate(false);
		labelGraph.setWidth("-1px");
		labelGraph.setHeight("-1px");
		labelGraph.setValue("Named Graph:");
		gridLayoutAdm.addComponent(labelGraph, 0, 3);

		// textAreaGraph
		textAreaGraph = new TextArea();
		textAreaGraph.setImmediate(false);
		textAreaGraph.setWidth("197px");
		textAreaGraph.setHeight("46px");
		gridLayoutAdm.addComponent(textAreaGraph, 1, 3);

		return gridLayoutAdm;
	}


	private VerticalLayout buildVerticalLayoutDetails() {
		// common part: create layout
		verticalLayoutDetails = new VerticalLayout();
		verticalLayoutDetails.setImmediate(false);
		verticalLayoutDetails.setWidth("100.0%");
		verticalLayoutDetails.setHeight("100.0%");
		verticalLayoutDetails.setMargin(true);
		verticalLayoutDetails.setSpacing(true);

		// optionGroup_1
		optionGroupDetail = new OptionGroup();
		optionGroupDetail.setCaption("Options:");
		optionGroupDetail.setImmediate(false);
		optionGroupDetail.setWidth("-1px");
		optionGroupDetail.setHeight("-1px");
		verticalLayoutDetails.addComponent(optionGroupDetail);

		return verticalLayoutDetails;
	}

	private HorizontalLayout buildHorizontalLayout() {
		// common part: create layout
		horizontalLayout = new HorizontalLayout();
		horizontalLayout.setImmediate(false);
		horizontalLayout.setWidth("240px");
		horizontalLayout.setHeight("1px");
		horizontalLayout.setMargin(false);
		horizontalLayout.setSpacing(true);


		// buttonSave
//		buttonSave = new Button();
//		buttonSave.setCaption("Save & Commit");
//		buttonSave.setImmediate(true);
//		buttonSave.setWidth("-1px");
//
//		buttonSave.setHeight("-1px");
//		horizontalLayout.addComponent(buttonSave);
//
//		// buttonCanc
//		buttonCanc = new Button();
//		buttonCanc.setCaption("Cancel");
//		buttonCanc.setImmediate(true);
//		buttonCanc.setWidth("-1px");
//		buttonCanc.setHeight("-1px");
//		horizontalLayout.addComponent(buttonCanc);

		return horizontalLayout;
	}

}
