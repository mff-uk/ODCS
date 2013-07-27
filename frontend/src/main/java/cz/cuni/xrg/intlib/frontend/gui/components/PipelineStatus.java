package cz.cuni.xrg.intlib.frontend.gui.components;

/**
 * Dialog for pipeline status information. 
 * Opens from DPU Template Details Instances tab in {@link DPU}.
 * Contains information about pipeline status and last run of pipeline
 * 
 * @author Maria Kukhar
 *
 */
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.scheduling.PeriodUnit;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

public class PipelineStatus extends Window {
	
	private String lastRunTimeStr;
	private Label lastRunTime; 
	private Label runsNumber;
	private int i;
	
	/** 
	 * Get date of the last execution and number of run for specific pipeline.
	 * 
	 * @param selectedPipeline. Pipeline that locate in the row of Instances table 
	 * in which has been pressed the button Status.
	 */
	public void setSelectedPipeline(Pipeline selectedPipeline){
		
		Date maxDate = null;  
		
		List<PipelineExecution> executions = App.getApp().getPipelines().getAllExecutions();
		i=0;
		
		//getting number of pipeline run and date of the last pipeline execution
		for (PipelineExecution item : executions) {
			if (item.getPipeline().getId().equals(selectedPipeline.getId()) ){
				if (maxDate==null || maxDate.getTime()<item.getStart().getTime()){
					maxDate=item.getStart();
				}
				i++;
			}
		}
		if(maxDate!=null){
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,  Locale.getDefault());
			lastRunTimeStr = df.format(maxDate);
			}
		
		else
			lastRunTimeStr="";
		
		runsNumber.setCaption(Integer.toString(i));	
		lastRunTime.setCaption(lastRunTimeStr);	
	}
	
	/**
	 * Basic constructor
	 */
	public PipelineStatus(){
		
		this.setResizable(false);
		this.setDraggable(false);
		this.setModal(true);
		this.setCaption("Pipeline status");
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setWidth("200px");
		mainLayout.setHeight("100px");
		
		HorizontalLayout lastRunLayout = new HorizontalLayout();
		lastRunLayout.setSpacing(true);
		lastRunLayout.addComponent(new Label("Last run:"));
		
		lastRunTime = new Label();
		
		if(lastRunTimeStr==null)
			lastRunTimeStr ="";
		
		lastRunTime.setCaption(lastRunTimeStr);		
		lastRunLayout.addComponent(lastRunTime);
		
		HorizontalLayout runsNumberLayout = new HorizontalLayout();
		runsNumberLayout.setSpacing(true);
		runsNumberLayout.addComponent(new Label("Number of runs:"));
		
		runsNumber = new Label();

		runsNumberLayout.addComponent(runsNumber);
		
		mainLayout.addComponent(lastRunLayout);
		mainLayout.addComponent(runsNumberLayout);
		
		this.setContent(mainLayout);
		setSizeUndefined();
		
		
	}

}
