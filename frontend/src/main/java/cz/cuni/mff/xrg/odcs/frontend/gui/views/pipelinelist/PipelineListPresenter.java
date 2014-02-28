package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;

/**
 * Interface for presenter that take care about presenting information about
 * pipelines.
 *
 * @author Bogo
 */
public interface PipelineListPresenter extends Presenter {

	/**
	 * Refresh data from data sources.
	 */
	public void refreshEventHandler();

	/**
	 * Copy pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void copyEventHandler(long id);

	/**
	 * Delete pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void deleteEventHandler(long id);
	
	/**
	 * Tells whether user has permission to delete pipeline with given id, so
	 * we can decide whether to hide delete button.
	 * 
	 * @param pipelineId id of pipeline to be deleted
	 * @return true if user has permission to delete, false otherwise
	 */
	public boolean canDeletePipeline(long pipelineId);

	/**
	 * Schedule pipeline with given id.
	 *
	 * @param id Pipeline id.
	 */
	public void scheduleEventHandler(long id);

	/**
	 * Run pipeline with given id.
	 *
	 * @param id Pipeline id.
	 * @param inDebugMode Run in debug mode.
	 */
	public void runEventHandler(long id, boolean inDebugMode);

	/**
	 * Navigate to other view.
	 *
	 * @param where View class.
	 * @param param Parameter for new view or null.
	 */
	public void navigateToEventHandler(Class where, Object param);

	/**
	 * Select given page.
	 * 
	 * @param newPageNumber Page to select.
	 */
	public void pageChangedHandler(Integer newPageNumber);

	/**
	 * Informs about filter.
	 * 
	 * @param name Name of the filter.
	 * @param filterValue Value of the filter.
	 */
	public void filterParameterEventHander(String name, Object filterValue);

	/**
	 * View interface for pipeline list.
	 */
	public interface PipelineListView {

		/**
		 * Generate view, that interact with given presenter.
		 *
		 * @param presenter
		 * @return view
		 */
		public Object enter(final PipelineListPresenter presenter);

		/**
		 * Set data for view.
		 *
		 * @param dataObject
		 */
		public void setDisplay(PipelineListData dataObject);

		/**
		 * Select given page.
		 * 
		 * @param pageNumber Page to select.
		 */
		public void setPage(int pageNumber);

		/**
		 * Set filter.
		 * 
		 * @param key Name of the filter.
		 * @param value Value of the filter.
		 * 
		 */
		public void setFilter(String key, Object value);
		
		/**
		 * Refresh paging controls of the table.
		 */
		public void refreshTableControls();
	}

	/**
	 * Data object for handling informations between view and presenter.
	 */
	public final class PipelineListData {

		private final ReadOnlyContainer<Pipeline> container;

		/**
		 * Gets the container.
		 * @return Container.
		 */
		public ReadOnlyContainer<Pipeline> getContainer() {
			return container;
		}

		/**
		 * Constructor
		 * 
		 * @param container Container to hold.
		 */
		public PipelineListData(ReadOnlyContainer<Pipeline> container) {
			this.container = container;
		}
	}
}
