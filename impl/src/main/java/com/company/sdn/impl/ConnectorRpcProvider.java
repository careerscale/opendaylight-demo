/*
 * Copyright © 2016 Harinath.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.company.sdn.impl;

import java.util.Calendar;
import java.util.concurrent.Future;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AbstractAuthenticator;
import org.apache.shiro.mgt.AuthenticatingSecurityManager;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.ConnectorId;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.SystemConnectors;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.connectors.Connectors;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.connectors.connectors.Connector;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.connectors.connectors.ConnectorBuilder;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.rev161010.connectors.connectors.ConnectorKey;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.notification.rev161010.ConnectorLifecycleEventBuilder;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.AddConnectorInput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.AddConnectorOutput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.AddConnectorOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.AddDiscoveryNodeInput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.AddDiscoveryNodeOutput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.DeleteConnectorInput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.DeleteDiscoveryNodeInput;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.SystemConnectorXyzRpcService;
import org.opendaylight.yang.gen.v1.urn.abc.system.connector.xyz.rpc.rev161010.UpdateConnectorInput;

import org.opendaylight.yang.gen.v1.urn.abc.system.rev161010.System;
import org.opendaylight.yang.gen.v1.urn.abc.system.rev161010.SystemEventType;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import com.company.sdn.impl.audit.CustomAuthenticationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectorRpcProvider implements SystemConnectorXyzRpcService {


	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private static final String CONNECTOR_PREFIX="connector-id=";
	
	private DataBroker dataBroker;
	private NotificationPublishService notificationPublishService;
	private RpcProviderRegistry rpcRegistry;
	
	public ConnectorRpcProvider(DataBroker dataBroker, NotificationPublishService notificationPublishService,
			RpcProviderRegistry rpcRegistry) {
		this.dataBroker=dataBroker;
		this.notificationPublishService = notificationPublishService;
		this.rpcRegistry = rpcRegistry;
		this.rpcRegistry.addRpcImplementation(SystemConnectorXyzRpcService.class, this);
		/**
		 * This block never worked as it needs static security manager  which needs staticSecurityManagerEnabled to be set.
		 */
		try {
			AuthenticatingSecurityManager securityMgr =
					(AuthenticatingSecurityManager) SecurityUtils.getSecurityManager();

			LOG.info("security mgr {}",securityMgr);

			AbstractAuthenticator authentication = (AbstractAuthenticator) securityMgr.getAuthenticator();

			authentication.getAuthenticationListeners().add(new CustomAuthenticationListener());

		} catch (Exception e) {
			LOG.error("error {}", e);
		}
		
	}

	@Override
	public Future<RpcResult<AddConnectorOutput>> addConnector(AddConnectorInput input) {

		/**
		 * Temporary code here.
		 * */
		try {
			AuthenticatingSecurityManager securityMgr =
					(AuthenticatingSecurityManager) SecurityUtils.getSecurityManager();

			LOG.info("security mgr {}",securityMgr);

			AbstractAuthenticator authentication = (AbstractAuthenticator) securityMgr.getAuthenticator();

			authentication.getAuthenticationListeners().add(new CustomAuthenticationListener());

		} catch (Exception e) {
			LOG.error("error {}", e);
		}

		RpcResultBuilder<AddConnectorOutput> rpcResultBuilder = RpcResultBuilder.success();
		String connectorIdString = CONNECTOR_PREFIX +Long.toString(Calendar.getInstance().getTimeInMillis());
		ConnectorId connectorId = new ConnectorId(connectorIdString);
		ConnectorKey connectorKey = new ConnectorKey(connectorId);
		InstanceIdentifier<Connector> connectorIID =InstanceIdentifier.builder(System.class)
        .augmentation(SystemConnectors.class)
        .child(Connectors.class)
        .child(Connector.class, connectorKey)
        .build();
		
		input.getConfig();

		Connector connector = new ConnectorBuilder().setConfig(input.getConfig()).setId(connectorId).setKey(connectorKey).build();
		ReadWriteTransaction transaction =dataBroker.newReadWriteTransaction();
		transaction.merge(LogicalDatastoreType.CONFIGURATION, connectorIID, connector,true);
		
		transaction.submit();
		
		ConnectorLifecycleEventBuilder  connectorEventBuilder = new ConnectorLifecycleEventBuilder(connector).setEventType(SystemEventType.Add).setId(connectorId);
		
		try {
			notificationPublishService.putNotification(connectorEventBuilder.build());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		
		return rpcResultBuilder.withResult(new AddConnectorOutputBuilder().setId(connectorId).build()).buildFuture();
	}

	@Override
	public Future<RpcResult<Void>> updateConnector(UpdateConnectorInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<RpcResult<Void>> deleteConnector(DeleteConnectorInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<RpcResult<AddDiscoveryNodeOutput>> addDiscoveryNode(AddDiscoveryNodeInput input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Future<RpcResult<Void>> deleteDiscoveryNode(DeleteDiscoveryNodeInput input) {
		// TODO Auto-generated method stub
		return null;
	}

}
