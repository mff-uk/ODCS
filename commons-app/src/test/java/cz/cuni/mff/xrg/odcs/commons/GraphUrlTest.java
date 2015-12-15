/**
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.mff.xrg.odcs.commons;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cz.cuni.mff.xrg.odcs.rdf.repositories.GraphUrl;

/**
 * Test suite for {@link GraphUrl} class.
 * 
 * @author Petyr
 */
public class GraphUrlTest {

    /**
     * Execute translate test.
     */
    @Test
    public void translateTest() {
        final String input = "exec_3_dpu_4_du_0";
        final String expectedOutput =
                "http://unifiedviews.eu/resource/internal/dataunit/exec/3/dpu/4/du/0";

        assertEquals(expectedOutput, GraphUrl.translateDataUnitId(input));
    }
}
