package cz.cuni.xrg.intlib.backend.context.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.backend.AppEntry;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;

/**
 * Very primitive DataUnits merger.
 * 
 * @author Petyr
 *
 */
public class PrimitiveDataUniteMerger implements DataUnitMerger {

	@Override
	public void merger(List<DataUnit> left, List<DataUnit> right, DataUnitFactory factory) throws ContextException {
		Iterator<DataUnit> iterLeft = left.iterator();
		Iterator<DataUnit> iterRight = right.iterator();
		
		if (left.size() >= right.size()) {
			// the left is larger .. we do only merge 
			while (iterRight.hasNext()) {
				// merge
				try {
					iterLeft.next().merge( iterRight.next() );
				} catch(IllegalArgumentException  e) {
					throw new ContextException("Can't merge data units.", e);
				}				
			}
		} else {
			// the right is larger ..
			while (iterLeft.hasNext()) {
				// merge
				try {
					iterLeft.next().merge( iterRight.next() );
				} catch(IllegalArgumentException  e) {
					throw new ContextException("Can't merge data units.", e);
				}				
			}
			// add the rest from right			
			int addStart = left.size();
			while (iterRight.hasNext()) {
				DataUnit rightDataUnit = iterRight.next();
				// create new data unit (in context into which we merge)
				DataUnit newDataUnit = factory.create(rightDataUnit.getType(), true);
				// and copy the data
				newDataUnit.merge(rightDataUnit);
				left.add(newDataUnit);
			}
		}
	}

}
