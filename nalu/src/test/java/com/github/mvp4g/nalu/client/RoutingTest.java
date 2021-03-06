package com.github.mvp4g.nalu.client;

import com.github.mvp4g.nalu.client.plugin.IsPlugin;
import de.gishmo.mvp4g.nalu.simpleapplication.client.Application;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Router Tester.
 *
 * @author Frank Hossfeld
 * @version 1.0
 * @since <pre>Aug 26, 2018</pre>
 */
public class RoutingTest {

  private Application   application;
  private IsPluginJUnit plugin;

  @Before
  public void before() {
    // create plugin
    this.plugin = new IsPluginJUnit() {

      private boolean attached = true;
      private boolean confirm = true;
      private CompareHandler compareHandler;
      private HashHandler hashHandler;
      private RouteHandler routeHandler;

      @Override
      public void alert(String message) {
        System.out.println("alert-message: >>" + message + "<<");
      }

      @Override
      public boolean attach(String selector,
                            Object asElement) {
        Assert.assertTrue("attach-method: Selector or object mismatch!",
                          compareHandler.compare(selector,
                                                 (String) asElement));
        return this.attached;
      }

      @Override
      public boolean confirm(String message) {
        return this.confirm;
      }

      @Override
      public String getStartRoute() {
        return "/search";
      }

      @Override
      public void register(HashHandler handler) {
        this.hashHandler = handler;
      }

      @Override
      public void remove(String selector) {
        // nothing to do
      }

      @Override
      public void route(String newRoute,
                        boolean replace) {
        Assert.assertTrue("Route mismatch!",
                          routeHandler.compare(newRoute,
                                               replace));
      }

      public void addCompareHandler(CompareHandler compareHandler) {
        this.compareHandler = compareHandler;
      }

      public void addRouteHandler(RouteHandler routeHandler) {
        this.routeHandler = routeHandler;
      }

      public void setAttached(boolean attached) {
        this.attached = attached;
      }

      public void setConfirm(boolean confirm) {
        this.confirm = confirm;
      }
    };
  }

  @After
  public void after() {
    this.application = null;
    this.plugin = null;
  }

  /**
   * Test applicaiton start with no book mark
   */
  @Test
  public void testStartWithoutBookmark() {
    this.plugin.addCompareHandler(this::compare);
    this.plugin.addRouteHandler((newRoute, replace) -> {
      switch (newRoute) {
        case "":
          return !replace;
        case "footer":
          return !replace;
        case "search":
          return !replace;
        default:
          return false;
      }
    });
    this.application = new Application();
    this.application.run(this.plugin);
  }

  private boolean compare(String selector,
                          String object) {
    switch (selector) {
      case "content":
        return "DetailForm".equals(object) || "ListView".equals(object) || "SearchForm".equals(object);
      case "navigation":
        return "navigation".equals(object);
      case "footer":
        return "footer".equals(object);
      default:
        return false;
    }
  }

  interface IsPluginJUnit
    extends IsPlugin {

    void addCompareHandler(CompareHandler compareHandler);

    void addRouteHandler(RouteHandler routeHandler);

    void setAttached(boolean attached);

    void setConfirm(boolean confirm);

  }

  interface CompareHandler {

    boolean compare(String selector,
                    String object);

  }

  interface RouteHandler {

    boolean compare(String newRoute,
                    boolean replace);

  }
}
