package cz.cuni.mff.xrg.odcs.commons.app.data;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

/**
 * Test suite for {@link EdgeCompiler} class.
 * 
 * @author Petyr
 *
 */
public class EdgeCompilerTest {

	private final EdgeCompiler edgeCompiler = new EdgeCompiler(); 
	
	@Test
	public void mappingToListAndBack() {
		List<DataUnitDescription> sources = new LinkedList<>();
		sources.add(DataUnitDescription.createOutput("out_A", "", ""));
		sources.add(DataUnitDescription.createOutput("out_B", "", ""));
		sources.add(DataUnitDescription.createOutput("out_C", "", ""));
		sources.add(DataUnitDescription.createOutput("out_D", "", ""));
		List<DataUnitDescription> targets = new LinkedList<>();
		targets.add(DataUnitDescription.createInput("in_A", "", "", false));
		targets.add(DataUnitDescription.createInput("in_B", "", "", false));
		targets.add(DataUnitDescription.createInput("in_C", "", "", false));
		targets.add(DataUnitDescription.createInput("in_D", "", "", false));
		// create mapping		
		List<MutablePair<Integer, Integer>> mappings = new LinkedList<>();
		// out_A -> in_B
		mappings.add(new MutablePair<>(0, 1));
		// out_B -> in_A
		mappings.add(new MutablePair<>(1, 0));
		// out_C -> in_C
		mappings.add(new MutablePair<>(2, 2));
		// convert into script ..
		String script = edgeCompiler.translate(mappings, sources, targets, null);
		// convert back from script
		List<MutablePair<Integer, Integer>> outputMappings =
				edgeCompiler.translate(script, sources, targets, null);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}
	
	@Test
	public void mappingToListAndBackManyToOne() {
		List<DataUnitDescription> sources = new LinkedList<>();
		sources.add(DataUnitDescription.createOutput("out_A", "", ""));
		sources.add(DataUnitDescription.createOutput("out_B", "", ""));
		sources.add(DataUnitDescription.createOutput("out_C", "", ""));
		List<DataUnitDescription> targets = new LinkedList<>();
		targets.add(DataUnitDescription.createInput("in_A", "", "", false));
		targets.add(DataUnitDescription.createInput("in_B", "", "", false));
		// create mapping 
		List<MutablePair<Integer, Integer>> mappings = new LinkedList<>();
		// out_A -> in_A
		mappings.add(new MutablePair<>(0, 0));
		// out_B -> in_A
		mappings.add(new MutablePair<>(1, 0));
		// out_C -> in_A
		mappings.add(new MutablePair<>(2, 0));
		// convert into script ..
		String script = edgeCompiler.translate(mappings, sources, targets, null);
		// convert back from script
		List<MutablePair<Integer, Integer>> outputMappings =
				edgeCompiler.translate(script, sources, targets, null);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}
	
	@Test
	public void backCompability() {
		List<DataUnitDescription> sources = new LinkedList<>();
		sources.add(DataUnitDescription.createOutput("out_A", "", ""));
		sources.add(DataUnitDescription.createOutput("out_B", "", ""));
		sources.add(DataUnitDescription.createOutput("out_C", "", ""));
		sources.add(DataUnitDescription.createOutput("out_D", "", ""));
		List<DataUnitDescription> targets = new LinkedList<>();
		targets.add(DataUnitDescription.createInput("in_A", "", "", false));
		targets.add(DataUnitDescription.createInput("in_B", "", "", false));
		targets.add(DataUnitDescription.createInput("in_C", "", "", false));
		targets.add(DataUnitDescription.createInput("in_D", "", "", false));
		// create mapping		
		List<MutablePair<Integer, Integer>> mappings = new LinkedList<>();
		// out_A -> in_B
		mappings.add(new MutablePair<>(0, 1));
		// out_B -> in_A
		mappings.add(new MutablePair<>(1, 0));
		// out_C -> in_C
		mappings.add(new MutablePair<>(2, 2));
		// convert into script ..
		String script = "out_A -> in_B;out_B -> in_A;out_C -> in_C;";
		// convert back from script
		List<MutablePair<Integer, Integer>> outputMappings =
				edgeCompiler.translate(script, sources, targets, null);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}	
	
}
