/*
 * Copyright © 2016 No rights reserved and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.company.sdn.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.Inventory;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.InventoryBuilder;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.RestockInventoryInput;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.RestockedInventory;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.RestockedInventoryBuilder;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.ShopData;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.ShopService;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.inventory.ItemsKey;
import org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.restock.inventory.input.Items;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier.InstanceIdentifierBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class ShopRpcProvider implements ShopService,Closeable {

	private DataBroker dataBroker;
	private NotificationPublishService notificationPublishService;
	private RpcProviderRegistry rpcRegistry;
	private RpcRegistration<ShopService> registration = null;

	private static final Logger LOG = LoggerFactory.getLogger(ShopRpcProvider.class);
 
	
	public ShopRpcProvider(DataBroker dataBroker, NotificationPublishService notificationPublishService,
			RpcProviderRegistry rpcRegistry) {
		this.dataBroker = dataBroker;
		this.notificationPublishService = notificationPublishService;
		this.rpcRegistry = rpcRegistry;
		LOG.info("Toaster RPC server is initialized now {} ", dataBroker);
		registration = this.rpcRegistry.addRpcImplementation(ShopService.class, this);
	
		 
	}
	
	@Override
	public Future<RpcResult<Void>> restockInventory(RestockInventoryInput input) {

		RpcResultBuilder<Void> response = RpcResultBuilder.success(); 
		
		try{
			List<Items> items = input.getItems();
			
			InstanceIdentifier<Inventory> inventoryIID= InstanceIdentifier.builder(Inventory.class).build();
			
			InventoryBuilder inventoryBuilder = new InventoryBuilder().setItems(getItems(input.getItems()));
			
			final WriteTransaction tx = dataBroker.newWriteOnlyTransaction();

			tx.merge(LogicalDatastoreType.CONFIGURATION, inventoryIID, inventoryBuilder.build(), true);

			tx.submit();
			
			RestockedInventory notification = new RestockedInventoryBuilder().setSummary("new restock happened").build();
			
			try {
				notificationPublishService.putNotification(notification);
			} catch (InterruptedException e) {
				LOG.error("notification is not sent because of {}", e); 
			}
			
			return Futures.immediateFuture(response.build());
			
		}catch(Exception e){
			LOG.error("exception {}", e);
		}
		return null;
		
	}

	private List<org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.inventory.Items> getItems(
			List<Items> items) {
		List<org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.inventory.Items> inventoryItems = Lists.newArrayList();
		
		for (Items item : items) {
			
			inventoryItems.add( new org.opendaylight.yang.gen.v1.http.company.com.shop.rev160715.inventory.ItemsBuilder().setId(item.getId()).setName(item.getName()).setPrice(item.getPrice()).setQuantity(item.getQuantity()).setKey(new ItemsKey(item.getKey().getId())).build());
		}
		
		return inventoryItems;
	}

	@Override
	public void close() throws IOException {
		registration.close();
		
	}

}
