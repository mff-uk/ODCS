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
package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.xstream;

import java.util.LinkedList;

/**
 * Filter members whose class contains given strings.
 * 
 * @author Škoda Petr
 */
public class ClassFilter implements MemberFilter {

    private final LinkedList<String> banList = new LinkedList<>();

    @Override
    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        final String className = definedIn.getCanonicalName();
        for (String str : banList) {
            if (className.contains(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add given name into class black list.
     * 
     * @param value
     */
    public void add(String value) {
        banList.add(value);
    }

}
