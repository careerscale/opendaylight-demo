/*
 * Copyright © 2016 No rights reserved and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.company.sdn.impl;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.toaster.rev091120.MakeToastInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.toaster.rev091120.RestockToasterInput;
import org.opendaylight.yang.gen.v1.http.netconfcentral.org.ns.toaster.rev091120.ToasterService;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;

public class ToasterRpcService implements ToasterService{


	private final DataBroker dataBroker;
	private NotificationPublishService notificationPublishService;
	private RpcProviderRegistry rpcRegistry;
	RpcRegistration registration = null;
	
	  
	private static final Logger LOG = LoggerFactory.getLogger(ToasterRpcService.class);

	public ToasterRpcService(DataBroker dataBroker, NotificationPublishService notificationPublishService,
			RpcProviderRegistry rpcRegistry) {
		this.dataBroker = dataBroker;
		this.notificationPublishService = notificationPublishService;
		this.rpcRegistry = rpcRegistry;
		LOG.info("Toaster RPC server is initialized now {} ", dataBroker);
		registration = this.rpcRegistry.addRpcImplementation(ToasterService.class, this);

		 
	}
	
	@Override
	public Future<RpcResult<Void>> restockToaster(RestockToasterInput input) {
		
		input.getAmountOfBreadToStock();
		

		
		RpcResultBuilder<Void> response = RpcResultBuilder.success();
		
		
		return Futures.immediateFuture(response.build());

		
	}

	@Override
	public Future<RpcResult<Void>> makeToast(MakeToastInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<RpcResult<Void>> cancelToast() {
		// TODO Auto-generated method stub
		return null;
	}

}
