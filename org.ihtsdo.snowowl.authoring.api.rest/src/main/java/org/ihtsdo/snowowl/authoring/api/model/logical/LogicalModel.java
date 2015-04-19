package org.ihtsdo.snowowl.authoring.api.model.logical;

import java.util.ArrayList;
import java.util.List;

public class LogicalModel {

	private String name;
	private List<IsARestriction> isARestrictions;
	private List<List<AttributeRestriction>> attributeRestrictionGroups;

	public LogicalModel() {
		isARestrictions = new ArrayList<>();
		attributeRestrictionGroups = new ArrayList<>();
	}

	public LogicalModel(String name, IsARestriction isARestriction) {
		this();
		this.name = name;
		isARestrictions.add(isARestriction);
	}

	public LogicalModel(AttributeRestriction attributeRestriction) {
		this();
		List<AttributeRestriction> attributeRestrictions = newAttributeGroup();
		attributeRestrictions.add(attributeRestriction);
	}

	public LogicalModel addIsARestriction(IsARestriction isARestriction) {
		isARestrictions.add(isARestriction);
		return this;
	}

	public List<AttributeRestriction> newAttributeGroup() {
		List<AttributeRestriction> attributeRestrictions = new ArrayList<>();
		attributeRestrictionGroups.add(attributeRestrictions);
		return attributeRestrictions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IsARestriction> getIsARestrictions() {
		return isARestrictions;
	}

	public void setIsARestrictions(List<IsARestriction> isARestrictions) {
		this.isARestrictions = isARestrictions;
	}

	public List<List<AttributeRestriction>> getAttributeRestrictionGroups() {
		return attributeRestrictionGroups;
	}

	public void setAttributeRestrictionGroups(List<List<AttributeRestriction>> attributeRestrictionGroups) {
		this.attributeRestrictionGroups = attributeRestrictionGroups;
	}

	@Override
	public String toString() {
		return "LogicalModel{" +
				"name='" + name + '\'' +
				", isARestrictions=" + isARestrictions +
				", attributeRestrictionGroups=" + attributeRestrictionGroups +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LogicalModel that = (LogicalModel) o;

		if (isARestrictions != null ? !isARestrictions.equals(that.isARestrictions) : that.isARestrictions != null) return false;
		return !(attributeRestrictionGroups != null ? !attributeRestrictionGroups.equals(that.attributeRestrictionGroups) : that.attributeRestrictionGroups != null);

	}

	@Override
	public int hashCode() {
		int result = isARestrictions != null ? isARestrictions.hashCode() : 0;
		result = 31 * result + (attributeRestrictionGroups != null ? attributeRestrictionGroups.hashCode() : 0);
		return result;
	}
}