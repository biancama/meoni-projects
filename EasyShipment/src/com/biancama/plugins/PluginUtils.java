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

package com.biancama.plugins;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.biancama.gui.UserIO;
import com.biancama.gui.swing.components.Balloon;
import com.biancama.http.Browser;
import com.biancama.utils.locale.BiancaL;

/**
 * Little Helper class for often used Plugin issues
 * 
 * @author Coalado
 */
public class PluginUtils {
    /**
     * Asks the user to entere a password for plugin
     */
    public static String askPassword(Plugin plg) {
        return UserIO.getInstance().requestInputDialog(0, BiancaL.LF("jd.plugins.PluginUtils.askPassword", "Please enter the password for %s", plg.getHost()), "");
    }

    /**
     * Informs the user that the password has been wrong
     * 
     * @param plg
     * @param password
     */
    public static void informPasswordWrong(Plugin plg, String password) {
        Balloon.show(BiancaL.LF("jd.plugins.PluginUtils.informPasswordWrong.title", "Password wrong: %s", password), UserIO.getInstance().getIcon(UserIO.ICON_ERROR), BiancaL.LF("jd.plugins.PluginUtils.informPasswordWrong.message", "The password you entered for %s has been wrong.", plg.getHost()));

    }

    public static void evalJSPacker(Browser br) {
        String regex = "eval\\((.*?\\,\\{\\}\\))\\)";
        String[] containers = br.getRegex(regex).getColumn(0);

        String htmlcode = br.getRequest().getHtmlCode();
        for (String c : containers) {
            Context cx = Context.enter();
            Scriptable scope = cx.initStandardObjects();
            c = c.replaceAll("return p\\}\\(", " return p}  f(").replaceAll("function\\s*\\(p\\,a\\,c\\,k\\,e\\,d\\)", "function f(p,a,c,k,e,d)");
            Object result = cx.evaluateString(scope, c, "<cmd>", 1, null);
            String code = Context.toString(result);
            htmlcode = htmlcode.replaceFirst(regex, code);

        }
        br.getRequest().setHtmlCode(htmlcode);
    }

}
