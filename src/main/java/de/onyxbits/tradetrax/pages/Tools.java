package de.onyxbits.tradetrax.pages;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

/**
 * This is just a sub menu
 * 
 * @author patrick
 * 
 */
@Import(library = "context:js/mousetrap.min.js")
public class Tools {

	@Inject
	private JavaScriptSupport javaScriptSupport;

	@Inject
	private PageRenderLinkSource linkSource;

	public void afterRender() {
		Link link = linkSource.createPageRenderLink(Index.class);
		javaScriptSupport
				.addScript("Mousetrap.prototype.stopCallback = function(e, element) {return false;};");
		javaScriptSupport.addScript("Mousetrap.bind('esc', function() {window.location='"
				+ link.toAbsoluteURI() + "'; return false;});");
	}
}
