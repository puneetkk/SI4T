/**
 * Copyright 2011-2013 Radagio & SDL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tridion.storage.si4t.dao;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.broker.StorageException;
import com.tridion.storage.ComponentPresentation;
import com.tridion.storage.filesystem.FSComponentPresentationDAO;
import com.tridion.storage.filesystem.FSEntityManager;
import com.tridion.storage.si4t.FactoryAction;
import com.tridion.storage.si4t.IndexType;
import com.tridion.storage.si4t.TridionBaseItemProcessor;
import com.tridion.storage.si4t.TridionPublishableItemProcessor;
import com.tridion.storage.si4t.Utils;
import com.tridion.storage.util.ComponentPresentationTypeEnum;

/**
 * FSSearchComponentPresentationDAO.
 * 
 * @author R.S. Kempees
 * @version 1.20
 * @since 1.00
 */

public class FSSearchComponentPresentationDAO extends FSComponentPresentationDAO
{
	private Logger log = LoggerFactory.getLogger(FSSearchComponentPresentationDAO.class);
	private String storageId = "";
	
	public FSSearchComponentPresentationDAO(String storageId, String storageName, File storageLocation)
	{
		super(storageId, storageName, storageLocation);
		this.storageId = storageId;
		log.debug("FSSearchComponentPresentationDAO Init.");
	}

	public FSSearchComponentPresentationDAO(String storageId, String storageName, File storageLocation, FSEntityManager entityManager)
	{
		super(storageId, storageName, storageLocation, entityManager);
		this.storageId = storageId;
		log.debug("FSSearchComponentPresentationDAO Init. (EM)");
	}
	
	/* (non-Javadoc)
	 * @see com.tridion.storage.filesystem.FSComponentPresentationDAO#create(com.tridion.storage.ComponentPresentation, com.tridion.storage.util.ComponentPresentationTypeEnum)
	 */
	@Override
	public void create(ComponentPresentation itemToCreate, ComponentPresentationTypeEnum componentPresentationType) throws StorageException
	{
		log.debug("Create.");
		TridionPublishableItemProcessor tp = new TridionPublishableItemProcessor(
				// TODO: 2013 getContent() does not return a string!
				new String(itemToCreate.getContent()),
				FactoryAction.PERSIST,
				IndexType.COMPONENT_PRESENTATION,
				Integer.toString(itemToCreate.getPublicationId()),
				"dcp:" + itemToCreate.getPublicationId() + "-" + itemToCreate.getComponentId() + "-" + itemToCreate.getTemplateId()
				, this.storageId);
		
		String strippedItem = tp.processComponentPresentationSource();
		if (!Utils.StringIsNullOrEmpty(strippedItem))
		{
			itemToCreate.setContent(strippedItem.getBytes());
		}

		super.create(itemToCreate, componentPresentationType);
	}

	/* 
	 * * TODO: set the proper compound ID
	 * (non-Javadoc)
	 * @see com.tridion.storage.filesystem.FSComponentPresentationDAO#remove(com.tridion.storage.ComponentPresentation, com.tridion.storage.util.ComponentPresentationTypeEnum)
	 */
	@Override
	public void remove(ComponentPresentation itemToRemove, ComponentPresentationTypeEnum componentPresentationType) throws StorageException
	{
		// dcp:{pubid}-{compid}-{tempid}
		super.remove(itemToRemove, componentPresentationType);
		log.debug("Removal method 1");
		TridionBaseItemProcessor.registerItemRemoval(
				"dcp:"+itemToRemove.getPublicationId()+"-"+itemToRemove.getComponentId()+"-"+itemToRemove.getTemplateId(),
				IndexType.COMPONENT_PRESENTATION,
				log,
				Integer.toString(itemToRemove.getPublicationId()), this.storageId);
	}


	/*
	 * (non-Javadoc)
	 * @see com.tridion.storage.filesystem.FSComponentPresentationDAO#remove(int, int, int, com.tridion.storage.util.ComponentPresentationTypeEnum)
	 */
	@Override
	public void remove(int publicationId, int componentId, int templateId, ComponentPresentationTypeEnum componentPresentationType) throws StorageException
	{
		super.remove(publicationId, componentId, templateId, componentPresentationType);
		log.debug("Removal method 2");
		TridionBaseItemProcessor.registerItemRemoval(
				"dcp:"+publicationId+"-"+componentId+"-"+templateId,
				IndexType.COMPONENT_PRESENTATION,
				log,
				Integer.toString(publicationId), this.storageId);
	}

	/* (non-Javadoc)
	 * @see com.tridion.storage.filesystem.FSComponentPresentationDAO#update(com.tridion.storage.ComponentPresentation, com.tridion.storage.util.ComponentPresentationTypeEnum)
	 */
	@Override
	public void update(ComponentPresentation itemToUpdate, ComponentPresentationTypeEnum componentPresentationType) throws StorageException
	{
		log.debug("Update.");
		TridionPublishableItemProcessor tp = new TridionPublishableItemProcessor(
				new String(itemToUpdate.getContent()),
				FactoryAction.UPDATE,
				IndexType.COMPONENT_PRESENTATION,
				Integer.toString(itemToUpdate.getPublicationId()),
				"dcp:" + itemToUpdate.getPublicationId() + "-" + itemToUpdate.getComponentId() + "-" + itemToUpdate.getTemplateId()
				, this.storageId);
		
		String strippedItem = tp.processComponentPresentationSource();
		if (!Utils.StringIsNullOrEmpty(strippedItem))
		{
			itemToUpdate.setContent(strippedItem.getBytes());
		}

		super.update(itemToUpdate, componentPresentationType);
	}
}
