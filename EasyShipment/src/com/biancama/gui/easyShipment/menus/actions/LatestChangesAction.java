//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.biancama.gui.easyShipment.menus.actions;

import java.awt.event.ActionEvent;

import com.biancama.gui.swing.actions.ToolBarAction;
import com.biancama.gui.swing.components.linkbutton.BiancaLink;

public class LatestChangesAction extends ToolBarAction {

    private static final long serialVersionUID = 2705114922279833817L;

    public LatestChangesAction() {
        super("action.changes", "gui.images.help");
    }

    @Override
    public void onAction(ActionEvent e) {
        try {
            BiancaLink.openURL("http://jdownloader.org/changes/index");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void initDefaults() {
    }

}
