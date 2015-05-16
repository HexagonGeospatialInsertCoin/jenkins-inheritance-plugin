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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
*/



import hudson.plugins.project_inheritance.projects.InheritanceProject;
import hudson.plugins.project_inheritance.projects.InheritanceProject.Relationship;
import hudson.plugins.project_inheritance.projects.creation.ProjectCreationEngine;
import hudson.plugins.project_inheritance.projects.view.InheritanceViewAction;


f = namespace(lib.FormTagLib);
l = namespace(lib.LayoutTagLib);
ct = namespace(lib.CustomTagLib);


//Fetching variables from different sources; depending on what 'my' is
if (my instanceof InheritanceViewAction) {
	//We're on a build page
	build = my.getBuild();
	project = build.getParent();
	showDownload = true;
	descriptor = my.getDescriptor();
} else if (my instanceof InheritanceProject) {
	//We're on a project's view page
	build = null;
	project = my;
	showDownload = false;
	descriptor = InheritanceViewAction.getDescriptorStatic();
} else {
	return;
}


if (build != null) {
	h1("Read-only view for build: " + project.displayName + " #" + build.getNumber())
} else {
	h1("Read-only view for project: " + project.displayName)
}

h3(style: "color:red") {
	span("Please note that this view is read only.")
}
	
script(
		type:"text/javascript",
		src: resURL + "/plugin/project-inheritance/scripts/markAllReadOnly.js"
)
ct.form(
				name: "config", 
		) {
	descriptor = project.getDescriptor()
	instance = project
	
	if (project.pronoun != null && !project.pronoun.isEmpty()) {
		f.entry(title: project.getPronoun() + " " + _("name")) {
			f.textbox(name: "name", value: project.name)
		}
	} else {
		f.entry(title: _("Project name")) {
			f.textbox(name: "name", value: project.name)
		}
	}
	
	
	f.entry(title: _("Description"), help: app.markupFormatter.helpUrl) {
		f.textarea(
				"codemirror-config": app.markupFormatter.codeMirrorConfig,
				"codemirror-mode": app.markupFormatter.codeMirrorMode,
				name: "description",
				value: project.description,
				previewEndpoint: "/markupFormatter/previewDescription"
		)
	}
	
	if (project.supportsLogRotator()) {
		f.optionalBlock(
				help: "/help/project-config/log-rotation.html",
				title: _("Discard Old Builds"),
				name: "logrotate", inline: "true",
				checked: project.buildDiscarder!=null) {
			f.dropdownDescriptorSelector(field: "buildDiscarder", title: _("Strategy"))
		}
	}
	ct.colored_block(backCol: "LightGoldenRodYellow ", borderCol: "navy") {
		f.section(title: _("Properties")) {}
		ct.blankEntry();
		f.descriptorList(
				field: "properties",
				forceRowSet: "true",
				descriptors: h.getJobPropertyDescriptors(project.getClass())
		)
	}
	
	include (project, "configure-entries")	
	
		//<f:descriptorList field="properties" descriptors="${h.getJobPropertyDescriptors(it.getClass())}" forceRowSet="true" />
	//include(project, "transient-job-fields")
	//include(project, "/hudson/model/Project/configure-entries")
	//include(project, "configure-properties")
}

// Printing a humongous warning if the project has a cyclic dependency
if (project.hasCyclicDependency()) {
	h2(style: "color:red") {
		span("This project has a")
		a(style: "color:red", href: "http://en.wikipedia.org/wiki/Cycle_detection", "cyclic")
		a(style: "color:red", href: "http://en.wikipedia.org/wiki/Diamond_problem", "diamond")
		span("or repeated dependency!")
	}
}

