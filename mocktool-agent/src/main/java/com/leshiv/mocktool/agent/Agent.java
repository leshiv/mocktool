package com.leshiv.mocktool.agent;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

import com.leshiv.mocktool.classloader.ExtensionManager;
import com.leshiv.mocktool.config.AgentConfig;
import com.leshiv.mocktool.config.AopConfig;
import com.leshiv.mocktool.util.MockToolLogFactory;

public class Agent
{

	public static Logger LOG = MockToolLogFactory.getDefaultLogger();
	static Instrumentation instrumentation;

	public static void premain(String agentArgs, Instrumentation inst) throws Exception
	{
		AgentConfig.init();
		instrumentation = inst;
		
		if (AgentConfig.isAgentEnabled())
		{
			LOG.info("Agent enabled, version: " + AgentConfig.getVersion());
			ExtensionManager.init(inst);
			AopConfig aopConfig = new AopConfig();
			aopConfig.init();
			inst.addTransformer(new AopClassFileTransformer());
		}
	}

}