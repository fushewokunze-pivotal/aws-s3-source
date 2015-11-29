/*
 * Copyright 2015 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acme;

import static org.junit.Assert.assertTrue;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.chainConsumer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.xd.dirt.server.singlenode.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChainConsumer;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;

public class SourceModuleIntegrationTest {
	private static SingleNodeApplication application;

	private static int RECEIVE_TIMEOUT = 6000;

	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.source, "s3Source"));
	}

	@Test
	public void test() {
		String accesKey = "AKIAJ7XQCW7P5FHBGYAQ";
		String secretKey = "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";
		String bucket = "testboom";
		String localDirectory = "/tmp/nec";
		String remoteDirectory = "/";
		
		SingleNodeProcessingChainConsumer chain = chainConsumer(application, "s3SourceStream", String.format("s3Source --accessKey=%s --secretKey=%s --bucket=%s --remoteDirectory=%s --localDirectory=%s", accesKey, secretKey, bucket, remoteDirectory, localDirectory));

		Object payload = chain.receivePayload(RECEIVE_TIMEOUT);
		
		System.out.println("payload: "+payload);
		
		//assertTrue(payload instanceof String);

		chain.destroy();
	}

}
