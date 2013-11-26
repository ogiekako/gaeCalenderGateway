package de.smilix.gaeCalenderGateway.model;

import javax.persistence.PrePersist;

public abstract class DatastoreObject
{
	private Integer version = 0;
	
	public abstract Long getId();
	
	/**
	 * Auto-increment version # whenever persisted
	 */
	@PrePersist
	void onPersist()
	{
		this.version++;
	}

	public Integer getVersion()
	{
		return version;
	}

	public void setVersion(Integer version)
	{
		this.version = version;
	}
}
