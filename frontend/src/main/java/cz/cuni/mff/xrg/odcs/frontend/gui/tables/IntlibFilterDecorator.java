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
package cz.cuni.mff.xrg.odcs.frontend.gui.tables;

import java.util.Locale;

import org.tepi.filtertable.FilterDecorator;
import org.tepi.filtertable.numberfilter.NumberFilterPopupConfig;

import com.vaadin.server.Resource;
import com.vaadin.shared.ui.datefield.Resolution;

import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;

/**
 * Default {@link FilterDecorator} to be used in tables. Extend this class and
 * override needed methods for customizing.
 * 
 * @author Bogo
 */
public class IntlibFilterDecorator implements FilterDecorator {

    @Override
    public String getEnumFilterDisplayName(Object propertyId, Object value) {
        return value.toString();
    }

    @Override
    public Resource getEnumFilterIcon(Object propertyId, Object value) {
        return null;
    }

    @Override
    public String getBooleanFilterDisplayName(Object propertyId, boolean value) {
        if (value) {
            return Messages.getString("IntlibFilterDecorator.true");
        } else {
            return Messages.getString("IntlibFilterDecorator.false");
        }
    }

    @Override
    public Resource getBooleanFilterIcon(Object propertyId, boolean value) {
        return null;
    }

    @Override
    public boolean isTextFilterImmediate(Object propertyId) {
        return true;
    }

    @Override
    public int getTextChangeTimeout(Object propertyId) {
        return 500;
    }

    @Override
    public String getFromCaption() {
        return Messages.getString("IntlibFilterDecorator.from");
    }

    @Override
    public String getToCaption() {
        return Messages.getString("IntlibFilterDecorator.to");
    }

    @Override
    public String getSetCaption() {
        return Messages.getString("IntlibFilterDecorator.set");
    }

    @Override
    public String getClearCaption() {
        return Messages.getString("IntlibFilterDecorator.clear");
    }

    @Override
    public Resolution getDateFieldResolution(Object propertyId) {
        return Resolution.SECOND;
    }

    @Override
    public String getDateFormatPattern(Object propertyId) {
        return "dd.MM.yyyy HH:mm";
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    @Override
    public String getAllItemsVisibleString() {
        return "";
    }

    @Override
    public NumberFilterPopupConfig getNumberFilterPopupConfig() {
        NumberFilterPopupConfig config = new NumberFilterPopupConfig();
        config.setValueMarker("x");
        return config;
    }

    @Override
    public boolean usePopupForNumericProperty(Object propertyId) {
        return true;
    }
}
