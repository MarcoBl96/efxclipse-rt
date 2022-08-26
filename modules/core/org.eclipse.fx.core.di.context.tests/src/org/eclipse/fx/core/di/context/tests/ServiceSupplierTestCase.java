/*******************************************************************************
 * Copyright (c) 2022 BestSolution.at and others.
 *  
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *  
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *     BestSolution.at - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.core.di.context.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.fx.core.di.Service;
import org.eclipse.fx.core.observable.BaseValueObservable;
import org.eclipse.fx.core.observable.ValueObservable.OString;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class ServiceSupplierTestCase {
	public static class TestBean {
		TestService service;
		List<TestService> serviceList;
		int serviceInjectionCount;
		int serviceListInjectionCount;

		@Inject
		public void setService(@Service TestService service) {
			this.service = service;
			this.serviceInjectionCount++;
		}

		@Inject
		public void setServiceList(@Service List<TestService> serviceList) {
			this.serviceList = serviceList;
			this.serviceListInjectionCount++;
		}
	}

	public static class TestStaticFilterBean {
		TestService service;
		List<TestService> serviceList;
		int serviceInjectionCount;
		int serviceListInjectionCount;

		@Inject
		public void setService(@Service(filterExpression="(filtervalue=Test)") TestService service) {
			this.service = service;
			this.serviceInjectionCount++;
		}

		@Inject
		public void setServiceList(@Service(filterExpression="(filtervalue=Test)") List<TestService> serviceList) {
			this.serviceList = serviceList;
			this.serviceListInjectionCount++;
		}
	}

	public static class TestDynamicFilterBean {
		TestService service;
		List<TestService> serviceList;
		int serviceInjectionCount;
		int serviceListInjectionCount;

		@Inject
		public void setService(@Optional @Service(dynamicFilterExpression=StringFilterObs.class) TestService service) {
			this.service = service;
			this.serviceInjectionCount++;
		}

		@Inject
		public void setServiceList(@Service(dynamicFilterExpression=StringFilterObs.class) List<TestService> serviceList) {
			this.serviceList = serviceList;
			this.serviceListInjectionCount++;
		}
	}

	public static class TestDisabledBean {
		@Inject
		@Optional
		@Service(filterExpression = "(component=disabled)")
		TestService disabledService;

		@Inject
		@Service(filterExpression = "(component=disabled)")
		List<TestService> services;
	}

	static class StringFilterObs extends BaseValueObservable<String> implements OString {
		static List<StringFilterObs> LIST = new ArrayList<>();

		public StringFilterObs() {
			super("(filtervalue=Test)"); //$NON-NLS-1$
			LIST.add(this);
		}

		public static void updateFilter(String filter) {
			LIST.stream().forEach( o -> o.setValue(filter));
		}
	}

	private List<ServiceRegistration<?>> registrations = new ArrayList<>();

	@After
	public void cleanup() {
		this.registrations.forEach( r -> r.unregister());
	}

	@Test
	public void testInitialInject() {
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext());
		TestBean bean = ContextInjectionFactory.make(TestBean.class, serviceContext);
		Assert.assertNotNull(bean.service);
		Assert.assertNotNull(bean.serviceList);
		Assert.assertSame(SampleServiceA.class,bean.service.getClass());
		Assert.assertEquals(1, bean.serviceInjectionCount);

		Assert.assertEquals(4, bean.serviceList.size());
		Assert.assertEquals(1, bean.serviceListInjectionCount);
		Assert.assertSame(SampleServiceA.class,bean.serviceList.get(0).getClass());
		Assert.assertSame(SampleServiceB.class,bean.serviceList.get(1).getClass());
		Assert.assertSame(FilterServiceA.class,bean.serviceList.get(2).getClass());
		Assert.assertSame(FilterServiceB.class,bean.serviceList.get(3).getClass());

	}

	@Test
	public void testStaticFilter() {
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext());
		TestStaticFilterBean bean = ContextInjectionFactory.make(TestStaticFilterBean.class, serviceContext);

		Assert.assertNotNull(bean.service);
		Assert.assertNotNull(bean.serviceList);

		Assert.assertSame(FilterServiceA.class,bean.service.getClass());
		Assert.assertEquals(1, bean.serviceInjectionCount);

		Assert.assertEquals(2, bean.serviceList.size());
		Assert.assertEquals(1, bean.serviceListInjectionCount);
		Assert.assertSame(FilterServiceA.class,bean.serviceList.get(0).getClass());
		Assert.assertSame(FilterServiceB.class,bean.serviceList.get(1).getClass());
	}

	@Test
	public void testDynamicFilter() {
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(getClass()).getBundleContext());
		TestDynamicFilterBean bean = ContextInjectionFactory.make(TestDynamicFilterBean.class, serviceContext);

		Assert.assertNotNull(bean.service);
		Assert.assertNotNull(bean.serviceList);

		Assert.assertSame(FilterServiceA.class,bean.service.getClass());
		Assert.assertEquals(1, bean.serviceInjectionCount);

		Assert.assertEquals(2, bean.serviceList.size());
		Assert.assertEquals(1, bean.serviceListInjectionCount);
		Assert.assertSame(FilterServiceA.class,bean.serviceList.get(0).getClass());
		Assert.assertSame(FilterServiceB.class,bean.serviceList.get(1).getClass());

		StringFilterObs.updateFilter("(filtervalue=NixDa)");
		Assert.assertNull(bean.service);
		Assert.assertEquals(2, bean.serviceInjectionCount);

		Assert.assertTrue(bean.serviceList.isEmpty());
		Assert.assertEquals(2, bean.serviceListInjectionCount);

		StringFilterObs.updateFilter("(filtervalue=Test)");
		Assert.assertNotNull(bean.service);
		Assert.assertNotNull(bean.serviceList);

		Assert.assertSame(FilterServiceA.class,bean.service.getClass());
		Assert.assertEquals(3, bean.serviceInjectionCount);
		Assert.assertEquals(2, bean.serviceList.size());
		Assert.assertEquals(3, bean.serviceListInjectionCount);
		Assert.assertSame(FilterServiceA.class,bean.serviceList.get(0).getClass());
		Assert.assertSame(FilterServiceB.class,bean.serviceList.get(1).getClass());
	}

	@Test
	public void testDynamicAdd() {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(context);
		TestBean bean = ContextInjectionFactory.make(TestBean.class, serviceContext);

		Assert.assertEquals(1, bean.serviceInjectionCount);
		Assert.assertEquals(1, bean.serviceListInjectionCount);

		TestService t = new TestService() {
			// nothing todo
		};

		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put("service.ranking", 100); //$NON-NLS-1$
		this.registrations.add(context.registerService(TestService.class, t, properties));

		Assert.assertSame(t,bean.service);
		Assert.assertEquals(2, bean.serviceInjectionCount);

		Assert.assertEquals(2, bean.serviceListInjectionCount);
		Assert.assertEquals(5, bean.serviceList.size());
		Assert.assertSame(t,bean.serviceList.get(0));

		TestService t2 = new TestService() {
			// nothing todo
		};

		properties = new Hashtable<>();
		properties.put("service.ranking", Integer.valueOf(-1)); //$NON-NLS-1$
		this.registrations.add(context.registerService(TestService.class, t2, properties));

		Assert.assertSame(t,bean.service);
		Assert.assertEquals(3, bean.serviceInjectionCount);

		Assert.assertEquals(3, bean.serviceListInjectionCount);

		Assert.assertEquals(6, bean.serviceList.size());
		Assert.assertSame(t,bean.serviceList.get(0));
	}

	@Test
	public void testDynamicAddRemove() {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(context);
		TestBean bean = ContextInjectionFactory.make(TestBean.class, serviceContext);

		Assert.assertEquals(1, bean.serviceInjectionCount);
		Assert.assertEquals(1, bean.serviceListInjectionCount);

		TestService t = new TestService() {
			// nothing todo
		};

		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put("service.ranking", 52); //$NON-NLS-1$
		this.registrations.add(context.registerService(TestService.class, t, properties));

		Assert.assertSame(t,bean.service);
		Assert.assertEquals(2, bean.serviceInjectionCount);

		Assert.assertEquals(2, bean.serviceListInjectionCount);
		Assert.assertEquals(5, bean.serviceList.size());
		Assert.assertSame(t,bean.serviceList.get(0));

		ServiceRegistration<?> registration = this.registrations.get(0);
		registration.unregister();
		this.registrations.remove(registration);

		Assert.assertEquals(3, bean.serviceInjectionCount);
		Assert.assertEquals(3, bean.serviceListInjectionCount);

		Assert.assertSame(SampleServiceA.class,bean.service.getClass());
		Assert.assertEquals(4, bean.serviceList.size());
		Assert.assertSame(SampleServiceA.class,bean.serviceList.get(0).getClass());
		Assert.assertSame(SampleServiceB.class,bean.serviceList.get(1).getClass());
		Assert.assertSame(FilterServiceA.class,bean.serviceList.get(2).getClass());
		Assert.assertSame(FilterServiceB.class,bean.serviceList.get(3).getClass());
	}

	@Test
	public void testCleanup() {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext iec = EclipseContextFactory.getServiceContext(context).createChild();
		TestBean bean = ContextInjectionFactory.make(TestBean.class, iec);
		iec.dispose();

		TestService t = new TestService() {
			// nothing todo
		};

		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put("service.ranking", 2); //$NON-NLS-1$
		this.registrations.add(context.registerService(TestService.class, t, properties));

		Assert.assertSame(SampleServiceA.class,bean.service.getClass());
	}

	@Test
	public void testOptionalReferences() throws InterruptedException {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(context);
		TestDisabledBean bean = ContextInjectionFactory.make(TestDisabledBean.class, serviceContext);

		assertNull(bean.disabledService);
		assertEquals(0, bean.services.size());

		ServiceReference<ComponentEnabler> ref = context.getServiceReference(ComponentEnabler.class);
		ComponentEnabler enabler = context.getService(ref);
		try {
			enabler.enableDisabledServiceA();
			// give the service registry and the injection some time
			Thread.sleep(100);
			assertNotNull(bean.disabledService);
			assertEquals(1, bean.services.size());
			assertSame(DisabledServiceA.class, bean.disabledService.getClass());

			enabler.enableDisabledServiceB();
			// give the service registry and the injection some time
			Thread.sleep(100);
			assertNotNull(bean.disabledService);
			assertEquals(2, bean.services.size());
			assertSame(DisabledServiceB.class, bean.disabledService.getClass());

			enabler.disableDisabledServiceB();
			// give the service registry and the injection some time
			Thread.sleep(100);
			assertNotNull(bean.disabledService);
			assertEquals(1, bean.services.size());
			assertSame(DisabledServiceA.class, bean.disabledService.getClass());

			enabler.disableDisabledServiceA();
			// give the service registry and the injection some time
			Thread.sleep(100);
			assertNull(bean.disabledService);
			assertEquals(0, bean.services.size());
		} finally {
			enabler.disableDisabledServiceA();
			enabler.disableDisabledServiceB();
			// give the service registry and the injection some time to ensure
			// clear state after this test
			Thread.sleep(100);
		}
	}
}
