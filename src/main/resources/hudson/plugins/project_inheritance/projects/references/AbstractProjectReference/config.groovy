/**
 * Copyright (c) 2011-2013, Intel Mobile Communications GmbH
 *
 *
 * This file is part of the Inheritance plug-in for Jenkins.
 *
 * The Inheritance plug-in is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation in version 3
 * of the License
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.	If not, see <http://www.gnu.org/licenses/>.
 */

import hudson.plugins.project_inheritance.projects.references.AbstractProjectReference;

f = namespace(lib.FormTagLib);
l = namespace(lib.LayoutTagLib);
ct = namespace(lib.CustomTagLib);


helpRoot = "/plugin/project-inheritance/help/ProjectReference"

//Check if our parent wants some fields to be read-only
//Note: Only works when the page is loaded for the first time
try { isReadOnly = readOnly } catch (e) { isReadOnly = false }

f.invisibleEntry() {
	f.readOnlyTextbox(default: my.name, name: "projectName")
}

f.entry(field: "name", title: _("Name")) {
	f.select(
			default: my.name,
			disabled: (isReadOnly) ? "disabled" : "enabled"
	)
}
