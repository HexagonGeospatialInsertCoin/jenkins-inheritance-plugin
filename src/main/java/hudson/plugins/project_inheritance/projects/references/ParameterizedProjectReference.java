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

package hudson.plugins.project_inheritance.projects.references;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.plugins.project_inheritance.projects.InheritanceProject;
import hudson.plugins.project_inheritance.projects.parameters.InheritableStringParameterDefinition;
import hudson.plugins.project_inheritance.projects.parameters.InheritanceParametersDefinitionProperty;

import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;


/**
 * This class is an implementation of {@link AbstractProjectReference} with
 * with the added option of specifying addition parameters to be passed to
 * the referenced Project.
 * 
 * @author Martin Schroeder
 */
public class ParameterizedProjectReference extends SimpleProjectReference {

	protected List<ParameterDefinition> parameters;
	protected String variance = null;
	protected String assignedLabelString = null;
	
	public static class OverridingMap {
		public final boolean parameterIsOverriding;
		public final boolean buildWrapperIsOverriding;
		public final boolean builderIsOverriding;
		public final boolean publisherIsOverriding;
		public final boolean miscIsOverriding;
		public final boolean propertiesIsOverriding;
		
		public OverridingMap(boolean... overriding) {
			if (overriding.length != 6) {
				throw new IllegalArgumentException("OverridingMap expected 6 values");
			}
			parameterIsOverriding = overriding[0];
			buildWrapperIsOverriding = overriding[1];
			builderIsOverriding = overriding[2];
			publisherIsOverriding = overriding[3];
			miscIsOverriding = overriding[4];
			propertiesIsOverriding  = overriding[5];
		}
	}
	
	//Is this project reference overriding the other parent?
	private final OverridingMap overridingMap;
	
	@DataBoundConstructor
	public ParameterizedProjectReference(String name, String variance, String assignedLabelString,
			List<ParameterDefinition> parameters, boolean parameterIsOverriding, boolean buildWrapperIsOverriding,
			boolean builderIsOverriding, boolean publisherIsOverriding, boolean miscIsOverriding, boolean propertiesIsOverriding) {
		super(name);
		InheritanceProject project = this.getProject();
		if (project != null && parameters != null) {
			for (ParameterDefinition pd : parameters) {
				if (pd instanceof InheritableStringParameterDefinition) {
					InheritableStringParameterDefinition ispd =
							(InheritableStringParameterDefinition) pd;
					ispd.setRootProperty(project.getProperty(
							InheritanceParametersDefinitionProperty.class
					));
				}
			}
		}
		if (parameters == null) {
			this.parameters = new LinkedList<ParameterDefinition>();
		} else {
			this.parameters = parameters;
		}
		
		this.variance = variance;
		this.assignedLabelString = assignedLabelString;
		this.overridingMap  = new OverridingMap(parameterIsOverriding,  buildWrapperIsOverriding,builderIsOverriding, 
				  publisherIsOverriding,  miscIsOverriding, propertiesIsOverriding);
	}
	
	
	// === FIELD ACCESS FUNCTIONS ===
	
	public List<ParameterDefinition> getParameters() {
		if (this.parameters == null) {
			this.parameters = new LinkedList<ParameterDefinition>();
		}
		return this.parameters;
	}
	
	public String getVariance() {
		if (this.variance == null || this.variance.isEmpty()) {
			return null;
		}
		return this.variance;
	}
	
	public String getAssignedLabelString(){
		if (this.assignedLabelString == null || this.assignedLabelString.isEmpty()) {
			return null;
		}
		return this.assignedLabelString;
	
	}

	public OverridingMap getOverridingMap(){
		return this.overridingMap;
	}
	
	// === GUI ACCESS METHODS ===
	
	public boolean getParameterIsOverriding() {
		return this.overridingMap.parameterIsOverriding;
	}
	
	public boolean getBuildWrapperIsOverriding() {
		return this.overridingMap.buildWrapperIsOverriding;
	}
	
	public boolean getBuilderIsOverriding() {
		return this.overridingMap.builderIsOverriding;
	}
	
	public boolean getPublisherIsOverriding() {
		return this.overridingMap.publisherIsOverriding;
	}
	
	public boolean getMiscIsOverriding() {
		return this.overridingMap.miscIsOverriding;
	}
	public boolean getPropertiesIsOverriding() {
		return this.overridingMap.propertiesIsOverriding;
	}
		
	
	// === DESCRIPTOR DEFINITION ===
	
	@Extension
	public static class DescriptorImpl extends ProjectReferenceDescriptor {
		@Override
		public String getDisplayName() {
			return "Parameterized Project Reference";
			//return Messages.StringParameterDefinition_DisplayName();
		}
		
		@Override
		public AbstractProjectReference newInstance(
				StaplerRequest req, JSONObject formData) throws FormException {
			return req.bindJSON(ParameterizedProjectReference.class, formData);
		}
	}
}
