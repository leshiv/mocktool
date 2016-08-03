package com.leshiv.mocktool.agent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.leshiv.mocktool.aop.api.Aspect;
import com.leshiv.mocktool.aop.implement.AspectRegistry;
import com.leshiv.mocktool.config.AgentConfig;
import com.leshiv.mocktool.util.MockToolLogFactory;
import com.leshiv.mocktool.util.StylishLogging;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class AopClassFileTransformer implements ClassFileTransformer
{
	private static final Logger LOG = MockToolLogFactory.getDefaultLogger();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer)
	{
		byte[] byteCode = null;
		if (AspectRegistry.isIgnored(className))
		{
			return null;
		}

		String fullClassName = className.replace("/", ".");
		if (AspectRegistry.matchClass(loader, fullClassName))
		{
			ClassPool cp = ClassPool.getDefault();
			// append loader's class path
			cp.appendClassPath(new LoaderClassPath(loader));

			try
			{
				CtClass cc = cp.get(fullClassName);
				if (cc.isInterface())
				{
					LOG.info("Is interface, skip : " + fullClassName);
					cc.detach();
					return null;
				}

				LOG.info(StylishLogging.bannerString("Transform class : ".toUpperCase() + fullClassName));

				CtMethod[] methods = cc.getDeclaredMethods();
				for (CtMethod cm : methods)
				{
					Aspect ap = AspectRegistry.matchCtMethod(loader, cm);
					if (ap != null)
					{
						LOG.info(StylishLogging.simpleBannerString("Method matches : " + cm.getLongName()));
						try
						{
							AopCodeModifier.applyAspect(cm, ap, loader);
						}
						catch (Exception e)
						{
							//TODO: exception handler
							LOG.log(Level.SEVERE, "Failed to apply aspect to ： " + cm.getLongName(), e);
							// leave class file intact then
							return classfileBuffer;
						}
					}
				}

				byteCode = cc.toBytecode();
				cc.detach();

				if (AgentConfig.isExportTransformedClasses())
				{
					try
					{
						String homePath = (String) System.getProperties().get("user.home") + File.separator
								+ "mocktool-instrumented-classes";
						File newClassFile = new File(homePath + File.separator + className + ".class");
						LOG.info("Export transformed class file to : " + newClassFile.getPath());
						if (!newClassFile.exists())
						{
							newClassFile.getParentFile().mkdirs();
							newClassFile.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(newClassFile);
						fos.write(byteCode);
						fos.flush();
						fos.close();
					}
					catch (IOException ie)
					{
						LOG.log(Level.SEVERE, "Failed to export transformed class file.", ie);
					}
				}

				return byteCode;
			}
			catch (Exception e)
			{
				LOG.log(Level.SEVERE, "Failed to transform class : " + className, e);
				e.printStackTrace();
			}
		}

		return null;
	}

}
