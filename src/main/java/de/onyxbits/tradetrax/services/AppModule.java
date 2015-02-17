package de.onyxbits.tradetrax.services;

import java.io.File;
import java.io.IOException;

import org.apache.tapestry5.*;
import org.apache.tapestry5.hibernate.HibernateConfigurer;
import org.apache.tapestry5.hibernate.HibernateSessionSource;
import org.apache.tapestry5.hibernate.HibernateSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.services.ApplicationGlobals;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

import de.onyxbits.tradetrax.main.AppConstants;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
public class AppModule {
	public static void bind(ServiceBinder binder) {
		// binder.bind(MyServiceInterface.class, MyServiceImpl.class);

		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(SettingsStore.class);
		binder.bind(EventLogger.class);
		binder.bind(MoneyRepresentation.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
		// The application version number is incorprated into URLs for some
		// assets. Web browsers will cache assets because of the far future expires
		// header. If existing assets are changed, the version number should also
		// change, to force the browser to download new versions. This overrides
		// Tapesty's default
		// (a random hexadecimal number), but may be further overriden by
		// DevelopmentModule or
		// QaModule.
		configuration.override(SymbolConstants.APPLICATION_VERSION, "v"+AppConstants.VERSION);
		configuration.override(SymbolConstants.CLUSTERED_SESSIONS,false);
		configuration.override(SymbolConstants.OMIT_GENERATOR_META,true);
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, Object> configuration) {
		// Contributions to ApplicationDefaults will override any contributions to
		// FactoryDefaults (with the same key). Here we're restricting the supported
		// locales to just "en" (English). As you add localised message catalogs and
		// other assets,
		// you can extend this list of locales (it's a comma separated series of
		// locale names;
		// the first locale name is the default when there's no reasonable match).
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		configuration.add(HibernateSymbols.DEFAULT_CONFIGURATION, "false");
		configuration.add(ComponentParameterConstants.GRIDCOLUMNS_SORTABLE_ASSET,"context:/images/sort-sortable.png");
		configuration.add(ComponentParameterConstants.GRIDCOLUMNS_ASCENDING_ASSET,"context:/images/sort-asc.png");
		configuration.add(ComponentParameterConstants.GRIDCOLUMNS_DESCENDING_ASSET,"context:/images/sort-desc.png");
	}

	/**
	 * This is a service definition, the service will be named "TimingFilter". The
	 * interface, RequestFilter, is used within the RequestHandler service
	 * pipeline, which is built from the RequestHandler service configuration.
	 * Tapestry IoC is responsible for passing in an appropriate Logger instance.
	 * Requests for static resources are handled at a higher level, so this filter
	 * will only be invoked for Tapestry related requests.
	 * <p/>
	 * <p/>
	 * Service builder methods are useful when the implementation is inline as an
	 * inner class (as here) or require some other kind of special initialization.
	 * In most cases, use the static bind() method instead.
	 * <p/>
	 * <p/>
	 * If this method was named "build", then the service id would be taken from
	 * the service interface and would be "RequestFilter". Since Tapestry already
	 * defines a service named "RequestFilter" we use an explicit service id that
	 * we can reference inside the contribution method.
	 */
	public RequestFilter buildTimingFilter(final Logger log) {
		return new RequestFilter() {
			public boolean service(Request request, Response response, RequestHandler handler)
					throws IOException {
				long startTime = System.currentTimeMillis();

				try {
					// The responsibility of a filter is to invoke the corresponding
					// method
					// in the handler. When you chain multiple filters together, each
					// filter
					// received a handler that is a bridge to the next filter.

					return handler.service(request, response);
				}
				finally {
					long elapsed = System.currentTimeMillis() - startTime;

					log.info(String.format("Request time: %d ms", elapsed));
				}
			}
		};
	}

	/**
	 * This is a contribution to the RequestHandler service configuration. This is
	 * how we extend Tapestry using the timing filter. A common use for this kind
	 * of filter is transaction management or security. The @Local annotation
	 * selects the desired service by type, but only from the same module. Without
	 * 
	 * @Local, there would be an error due to the other service(s) that implement
	 *         RequestFilter (defined in other modules).
	 */
	public void contributeRequestHandler(OrderedConfiguration<RequestFilter> configuration,
			@Local RequestFilter filter) {
		// Each contribution to an ordered configuration has a name, When necessary,
		// you may
		// set constraints to precisely control the invocation order of the
		// contributed filter
		// within the pipeline.

		//configuration.add("Timing", filter);
	}

	@Inject
	private ApplicationGlobals globals;

	public void contributeHibernateSessionSource(OrderedConfiguration<HibernateConfigurer> configurer) {

		String path = globals.getServletContext().getInitParameter(AppConstants.IPNLEDGERPATH);
		// WARNING: This is here for the benefit of developers so they can switch
		// ledgers without having to use the StandaloneServer. WAR deployments
		// should be configured through the web.xml file and not a system property.
		// System properties apply to the whole servlet container, making it
		// impossible to serve multiple ledgers at once.
		path = System.getProperty("app.ledger", path);
		File dbdir = new File(System.getProperty("user.dir"));
		if (path != null) {
			dbdir = new File(path);
		}
		configurer.add("hibernate-session-source", new LedgerConfigurer(dbdir));
	}

	@Startup
	public static void scheduleJobs(HibernateSessionSource sessionSource) {

		// FIXME: the logpurger is suppose to run periodically. But for some reason,
		// the database query in it only finds and purges logentries that have been
		// expired on boot. Logentries that expire while the system is running are
		// ignored. It would be nice to purge logs in hte background, but we can
		// probably get away with only doing it once when starting up.

		// executor.addJob(new IntervalSchedule(1000), "Cleanup Job",
		// new LogPurger(sessionSource));
		new LogPurger(sessionSource).run();
	}
}
