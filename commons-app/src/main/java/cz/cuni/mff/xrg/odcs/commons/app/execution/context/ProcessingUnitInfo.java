package cz.cuni.mff.xrg.odcs.commons.app.execution.context;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import java.util.LinkedList;

import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.execution.DPUExecutionState;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import java.util.List;

/**
 * Contains and manage information about execution for single
 * {@link DPUInstanceRecord}. The information class (this) is created at the
 * start of the DPU execution. So the information class in not accessible for
 * all the DPUs from the beginning of the execution.
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "exec_context_dpu")
public class ProcessingUnitInfo implements DataObject {

	/**
	 * Unique id of pipeline execution.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_exec_context_dpu")
	@SequenceGenerator(name = "seq_exec_context_dpu", allocationSize = 1)
	private Long id;

	/**
	 * Describe state of the DPU execution.
	 */
	@Enumerated(EnumType.ORDINAL)
	private DPUExecutionState state = DPUExecutionState.PREPROCESSING;

	/**
	 * Storage for dataUnits descriptors.
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@JoinColumn(name = "exec_context_dpu_id")
	@OrderBy("index")
	List<DataUnitInfo> dataUnits = new LinkedList<>();

	/**
	 * Empty constructor for JPA.
	 */	
	public ProcessingUnitInfo() { }

	/**
	 * Create information about new DataUnit.
	 * 
	 * @param name
	 * @param type
	 * @param isInput
	 * @return index of new DataUnitInfo.
	 * @deprecated use {@link DpuContextInfo} instead
	 */
	@Deprecated
	public Integer addDataUnit(String name, DataUnitType type, boolean isInput) {
		// add information
		Integer index = 0;
		if (dataUnits.isEmpty()) {
		} else {
			index = dataUnits.get(dataUnits.size() - 1).getIndex() + 1;
		}
		DataUnitInfo dataUnitInfo = new DataUnitInfo(index, name, type, isInput);
		dataUnits.add(dataUnitInfo);
		// return index
		return index;
	}

	/**
	 * @return list of data units
	 * @deprecated use DpuContextInfo instead
	 */
	@Deprecated
	public List<DataUnitInfo> getDataUnits() {
		return dataUnits;
	}
	
	public DPUExecutionState getState() {
		return state;
	}
	
	public void setState(DPUExecutionState state) {
		this.state = state;
	}

	@Override
	public Long getId() {
		return id;
	}
	
}
