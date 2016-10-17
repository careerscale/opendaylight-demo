/*
 * Copyright © 2016 No rights reserved and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.company.sdn.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoProvider {

	private static final Logger LOG = LoggerFactory.getLogger(DemoProvider.class);

	private final DataBroker dataBroker;

	private ToasterRpcService toasterService;
	private ShopRpcProvider shopRpcProvider;
	private NotificationPublishService notificationPublishService;
	private RpcProviderRegistry rpcRegistry;
	private ConnectorRpcProvider connectorRpcProvider;

	public DemoProvider(final DataBroker dataBroker, final NotificationPublishService notificationPublishService,
			final RpcProviderRegistry rpcRegistry) {
		this.dataBroker = dataBroker;
		this.notificationPublishService = notificationPublishService;
		this.rpcRegistry = rpcRegistry;

	}

	/**
	 * Method called when the blueprint container is created.
	 */
	public void init() {
		LOG.info("DemoProvider Session Initiated");
		toasterService = new ToasterRpcService(dataBroker, notificationPublishService, rpcRegistry);
		shopRpcProvider = new ShopRpcProvider(dataBroker, notificationPublishService, rpcRegistry);
		this.connectorRpcProvider = new ConnectorRpcProvider(dataBroker, notificationPublishService, rpcRegistry);
		

	}

	/**
	 * Method called when the blueprint container is destroyed.
	 */
	public void close() {
		LOG.info("DemoProvider Closed");
	}
}