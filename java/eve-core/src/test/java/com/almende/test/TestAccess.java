package com.almende.test;

import junit.framework.TestCase;

import org.junit.Test;

import com.almende.eve.agent.Agent;
import com.almende.eve.agent.AgentFactory;
import com.almende.eve.config.Config;
import com.almende.test.agents.TestAccessAgent;

public class TestAccess extends TestCase {
	static final String TEST1 = "TestAccessAgent";
	static final String TEST2 = "trustedSender";

	@Test
	public void testAccess() throws Exception {
		AgentFactory agentFactory = AgentFactory.getInstance();
		if (agentFactory == null) {
			String filename = "eve.yaml";
			String fullname = "src/test/webapp/WEB-INF/" + filename;
			Config config = new Config(fullname);

			// TODO: create the agentFactory in a synchronized way
			agentFactory = AgentFactory.createInstance();
			agentFactory.setStateFactory(config);
			agentFactory.addTransportServices(config);
			agentFactory.setConfig(config);
			agentFactory.setSchedulerFactory(config);
			agentFactory.addAgents(config);
		}
		Agent testAgent;
		if (agentFactory.hasAgent(TEST1)) {
			testAgent = agentFactory.getAgent(TEST1);
		} else {
			testAgent = agentFactory.createAgent(TestAccessAgent.class, TEST1);
		}
		TestAccessAgent agent;
		if (agentFactory.hasAgent(TEST2)) {
			agent = (TestAccessAgent) agentFactory.getAgent(TEST2);
		} else {
			agent = (TestAccessAgent) agentFactory.createAgent(
					TestAccessAgent.class, TEST2);
		}
		boolean[] result = agent.run(testAgent.getUrls().get(0));
		assertEquals(7, result.length);
		assertEquals(true, result[0]);// allowed
		assertEquals(false, result[1]);// forbidden
		assertEquals(true, result[2]);// depends
		assertEquals(true, result[3]);// dependsTag
		assertEquals(false, result[4]);// dependsUnTag
		assertEquals(true, result[5]);// unmodified
		assertEquals(false, result[6]);// param

		// retry though non-local URL (https in this case);
		result = agent
				.run("https://localhost:8443/agents/" + testAgent.getId());
		assertEquals(7, result.length);
		assertEquals(true, result[0]);// allowed
		assertEquals(false, result[1]);// forbidden
		assertEquals(true, result[2]);// depends
		assertEquals(true, result[3]);// dependsTag
		assertEquals(false, result[4]);// dependsUnTag
		assertEquals(true, result[5]);// unmodified
		assertEquals(false, result[6]);// param
	}
}
