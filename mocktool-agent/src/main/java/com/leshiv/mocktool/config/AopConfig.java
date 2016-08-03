package com.leshiv.mocktool.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.leshiv.mocktool.aop.api.ExpressionEdition;
import com.leshiv.mocktool.aop.implement.AspectImpl;
import com.leshiv.mocktool.aop.implement.AspectRegistry;
import com.leshiv.mocktool.aop.implement.ExpressionEditionImpl;
import com.leshiv.mocktool.util.ConfigFileHelper;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.StylishLogging;

public class AopConfig
{
	private static Logger LOG = MockToolLogFactory.getDefaultLogger();
	private static final String AOP_CONFIG_FILE = AgentConfig.getAopConfigFileName();

	public void init()
	{
		boolean success = true;
		try
		{
			InputStream in = ConfigFileHelper.getConfigFile(AOP_CONFIG_FILE);
			if (in == null)
			{
				LOG.log(Level.WARNING, "Aop config file not found." + AOP_CONFIG_FILE);
				return;
			}
			SAXParserFactory spFactory = SAXParserFactory.newInstance();
			AopXmlHandler axh = new AopXmlHandler();
			spFactory.newSAXParser().parse(in, axh);
		}
		catch (IOException e)
		{
			LOG.log(Level.WARNING, "Failed to read aop config file .");
			e.printStackTrace();
			success = false;
		}
		catch (Exception e)
		{
			LOG.log(Level.WARNING, "Failed to parse aop config file.");
			e.printStackTrace();
			success = false;
		}
		String successStr = success ? "SUCCESS" : "FAILED";
		LOG.info(StylishLogging.bannerString("initialize aspects : ".toUpperCase() + successStr));

	}

	public class AopXmlHandler extends DefaultHandler
	{
		String pointcut;
		String adviceClass;
		Map<String, String> params = new HashMap<String, String>();
		List<ExpressionEdition> expressionEditions = new ArrayList<ExpressionEdition>();
		ExpressionEditionImpl exprEdition = new ExpressionEditionImpl();

		//		private Stack<String> elementStack = new Stack<String>();
		//		private Stack<Object> objectStack = new Stack<Object>();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
		{
			if (qName.equalsIgnoreCase("aspect"))
			{
				pointcut = attributes.getValue("pointcut");
				adviceClass = attributes.getValue("adviceClass");
			}
			else if (qName.equalsIgnoreCase("param"))
			{
				if (attributes.getValue("name") != null && attributes.getValue("value") != null)
					params.put(attributes.getValue("name"), attributes.getValue("value"));
			}
			//			else if (qName.equalsIgnoreCase("expression"))
			//			{
			//				exprEdition.setExpressionType(ExpressionType.valueOf(attributes.getValue("expressionType")));
			//				exprEdition.setReplaceStatement(attributes.getValue("replaceStatement"));
			//				exprEdition.setClassName(attributes.getValue("className"));
			//				exprEdition.setMethodName(attributes.getValue("methodName"));
			//			}
			else if (qName.equalsIgnoreCase("ignore"))
			{
				AspectRegistry.addIgnore(attributes.getValue("package"));
			}
			else if (qName.equalsIgnoreCase("unignore"))
			{
				AspectRegistry.addUnIgnore(attributes.getValue("package"));
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			//			if (qName.equalsIgnoreCase("expression"))
			//			{
			//				expressionEditions.add(exprEdition.clone());
			//				exprEdition.clear();
			//			}

			if (qName.equalsIgnoreCase("aspect") && (pointcut != null && !pointcut.isEmpty())
					&& (adviceClass != null && !adviceClass.isEmpty()))
			{
				AspectRegistry.registerAspect(new AspectImpl(pointcut, adviceClass,
						new HashMap<String, String>(params), new ArrayList<ExpressionEdition>(expressionEditions)));
				pointcut = "";
				adviceClass = "";
				params.clear();
				expressionEditions.clear();
			}
		}

		//		@Override
		//		public void characters(char ch[], int start, int length) throws SAXException
		//		{
		//			String value = new String(ch, start, length).trim();
		//			if (value.length() == 0)
		//				return;
		//		}

		//		private String currentElement()
		//		{
		//			return this.elementStack.peek();
		//		}
		//
		//		private String currentElementParent()
		//		{
		//			if (this.elementStack.size() < 2)
		//				return null;
		//			return this.elementStack.get(this.elementStack.size() - 2);
		//		}

	}

	//test purpose
	public static void main(String[] args)
	{
		AopConfig aopConfig = new AopConfig();
		aopConfig.init();
	}
}
