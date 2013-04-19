package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

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
	private VerticalLayout verticalLayoutCore;
	private HorizontalLayout horizontalLayoutFormat;
	private ComboBox comboBoxFormat; //RDFformat
	private Label labelFormat;
	private CheckBox checkBoxDiffName;
	private TextField textFieldFileName; // FileName
	private TextField textFieldDir;	//Directory
//	private GridLayout gridLayoutName;
//	private TextArea textAreaDescr; //Description
//	private Label labelDescr;
//	private TextField textFieldName; // NameDPU
//	private Label labelName;
//	private HorizontalLayout horizontalLayoutButtons;

	public ConfigDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}
	
	private void mapData() {
		
		comboBoxFormat.addItem("TTL");
		comboBoxFormat.addItem("RDF/XML");
		comboBoxFormat.addItem("N3");
		comboBoxFormat.addItem("TriG");
		comboBoxFormat.setValue("TTL");
		
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


		//config.setValue(Config.NameDPU.name(), textFieldName.getValue());
		//config.setValue(Config.Description.name(), textAreaDescr.getValue());
		config.setValue(Config.DirectoryPath.name(), textFieldDir.getValue());
		config.setValue(Config.FileName.name(), textFieldFileName.getValue());
		config.setValue(Config.RDFFileFormat.name(), comboBoxFormat.getValue());

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
			//textFieldName.setValue( (String) conf.getValue(Config.NameDPU.name()));
			//textAreaDescr.setValue( (String) conf.getValue(Config.Description.name()));
			textFieldDir.setValue( (String) conf.getValue(Config.DirectoryPath.name()));
			textFieldFileName.setValue( (String) conf.getValue(Config.FileName.name()));
			comboBoxFormat.setValue( (String) conf.getValue(Config.RDFFileFormat.name()));
		}
		catch(Exception ex) {
			// throw setting exception
			throw new ConfigurationException();
		}
	}

	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(1, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
		// tabSheet
		tabSheet = buildTabSheet();
		mainLayout.addComponent(tabSheet,  0, 0);
		mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);
		
		return mainLayout;
	}


	private TabSheet buildTabSheet() {
		// common part: create layout
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("100%");
		tabSheet.setHeight("100%");
		
		// verticalLayoutCore
		verticalLayoutCore = buildVerticalLayoutCore();
		verticalLayoutCore.setImmediate(false);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100.0%");
		tabSheet.addTab(verticalLayoutCore, "Core", null);
		
		// verticalLayoutDetails
		verticalLayoutDetails = new VerticalLayout();
		verticalLayoutDetails.setImmediate(false);
		verticalLayoutDetails.setWidth("100.0%");
		verticalLayoutDetails.setHeight("100.0%");
		verticalLayoutDetails.setMargin(false);
		tabSheet.addTab(verticalLayoutDetails, "Details", null);
		
		return tabSheet;
	}



	private VerticalLayout buildVerticalLayoutCore() {
		// common part: create layout
		verticalLayoutCore = new VerticalLayout();
		verticalLayoutCore.setImmediate(false);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100.0%");
		verticalLayoutCore.setMargin(true);
		verticalLayoutCore.setSpacing(true);
		
		
		// textFieldDir
		textFieldDir = new TextField();
		textFieldDir.setCaption("Directory:");
		textFieldDir.setImmediate(false);
		textFieldDir.setWidth("90%");
		textFieldDir.setHeight("-1px");
		verticalLayoutCore.addComponent(textFieldDir);
		
		// textFieldFileName
		textFieldFileName = new TextField();
		textFieldFileName.setCaption("File name (no extension):");
		textFieldFileName.setImmediate(false);
		textFieldFileName.setWidth("90%");
		textFieldFileName.setHeight("-1px");
		verticalLayoutCore.addComponent(textFieldFileName);
		
		// checkBoxDiffName
		checkBoxDiffName = new CheckBox();
		checkBoxDiffName
				.setCaption("Each pipeline execution generates a different name");
		checkBoxDiffName.setImmediate(false);
		checkBoxDiffName.setWidth("-1px");
		checkBoxDiffName.setHeight("-1px");
		verticalLayoutCore.addComponent(checkBoxDiffName);
		
		// horizontalLayoutFormat
		horizontalLayoutFormat = buildHorizontalLayoutFormat();
		verticalLayoutCore.addComponent(horizontalLayoutFormat);
		
		return verticalLayoutCore;
	}


	private HorizontalLayout buildHorizontalLayoutFormat() {
		// common part: create layout
		horizontalLayoutFormat = new HorizontalLayout();
		horizontalLayoutFormat.setImmediate(false);
		horizontalLayoutFormat.setWidth("-1px");
		horizontalLayoutFormat.setHeight("-1px");
		horizontalLayoutFormat.setMargin(false);
		horizontalLayoutFormat.setSpacing(true);
		
		// labelFormat
		labelFormat = new Label();
		labelFormat.setImmediate(false);
		labelFormat.setWidth("79px");
		labelFormat.setHeight("-1px");
		labelFormat.setValue("RDF Format:");
		horizontalLayoutFormat.addComponent(labelFormat);
		
		// comboBoxFormat
		comboBoxFormat = new ComboBox();
		comboBoxFormat.setImmediate(false);
		comboBoxFormat.setWidth("30%");
		comboBoxFormat.setHeight("-1px");
		comboBoxFormat.setNewItemsAllowed(false);
		comboBoxFormat.setNullSelectionAllowed(false);
		horizontalLayoutFormat.addComponent(comboBoxFormat);
		
		return horizontalLayoutFormat;
	}

}
