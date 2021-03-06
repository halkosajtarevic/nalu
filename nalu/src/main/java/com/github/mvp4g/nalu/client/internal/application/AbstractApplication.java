/*
 * Copyright (c) 2018 - Frank Hossfeld
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 *
 */

package com.github.mvp4g.nalu.client.internal.application;

import com.github.mvp4g.nalu.client.Nalu;
import com.github.mvp4g.nalu.client.Router;
import com.github.mvp4g.nalu.client.application.IsApplication;
import com.github.mvp4g.nalu.client.application.IsApplicationLoader;
import com.github.mvp4g.nalu.client.application.IsContext;
import com.github.mvp4g.nalu.client.component.IsShell;
import com.github.mvp4g.nalu.client.internal.ClientLogger;
import com.github.mvp4g.nalu.client.internal.annotation.NaluInternalUse;
import com.github.mvp4g.nalu.client.internal.route.HashResult;
import com.github.mvp4g.nalu.client.internal.route.RouterConfiguration;
import com.github.mvp4g.nalu.client.internal.route.RouterException;
import com.github.mvp4g.nalu.client.plugin.IsPlugin;
import org.gwtproject.event.shared.SimpleEventBus;

/**
 * generator of the eventBus
 */
@NaluInternalUse
public abstract class AbstractApplication<C extends IsContext>
  implements IsApplication {

  /* start route */
  protected String startRoute;
  /* route in case of route error */
  protected String routeErrorRoute;

  /* Shell */
  protected IsShell shell;

  /* Router Configuration */
  protected RouterConfiguration routerConfiguration;

  /* Router */
  protected Router         router;
  /* application context */
  protected C              context;
  /* the event bus of the application */
  protected SimpleEventBus eventBus;
  /* plugin */
  private   IsPlugin       plugin;

  public AbstractApplication() {
    super();
  }

  @Override
  public void run(IsPlugin plugin) {
    // save the plugin
    this.plugin = plugin;
    // first load the debug configuration
    this.loadDebugConfiguration();
    // debug message
    ClientLogger.get()
                .logSimple("AbstractApplication: application is started!",
                           0);
    // instantiate necessary classes
    this.eventBus = new SimpleEventBus();
    this.routerConfiguration = new RouterConfiguration();
    this.router = new Router(this.plugin,
                             this.routerConfiguration);
    // load everything you need to start
    ClientLogger.get()
                .logDetailed("AbstractApplication: load configurations",
                             1);
    this.loadRoutes();
    this.loadFilters();
    this.loadDefaultRoutes();
    this.router.setRouteErrorRoute(Nalu.NO_ROUTE.equals(this.routeErrorRoute) ? null : this.routeErrorRoute);
    // load the components of the application
    ClientLogger.get()
                .logDetailed("AbstractApplication: load components",
                             1);
    this.loadComponents();
    // load the handlers fo the application
    ClientLogger.get()
                .logDetailed("AbstractApplication: load handlers",
                             1);
    this.loadHandlers();
    // execute the loader (if one is present)
    ClientLogger.get()
                .logDetailed("AbstractApplication: execute loader",
                             1);
    // handling application loading
    IsApplicationLoader<C> applicationLoader = getApplicationLoader();
    if (getApplicationLoader() == null) {
      this.onFinishLaoding();
    } else {
      applicationLoader.setContext(this.context);
      applicationLoader.load(this::onFinishLaoding);
    }
  }

  protected abstract void loadDebugConfiguration();

  protected abstract void loadRoutes();

  protected abstract void loadFilters();

  protected abstract void loadDefaultRoutes();

  protected abstract void loadComponents();

  protected abstract void loadHandlers();

  protected abstract IsApplicationLoader<C> getApplicationLoader();

  /**
   * Once the loader did his job, we will continue
   */
  private void onFinishLaoding() {
    // save the current hash
    String hashOnStart = this.plugin.getStartRoute();
    //    String hashOnStart = DomGlobal.window.location.getHash();
    //    if (hashOnStart.startsWith("#")) {
    //      if (hashOnStart.length() > 1) {
    //        hashOnStart = hashOnStart.substring(1);
    //      }
    //    }
    // initialize shell ...
    ClientLogger.get()
                .logDetailed("AbstractApplication: onAttach shell",
                             1);
    this.attachShell();
    // start the application by calling url + '#'
    ClientLogger.get()
                .logDetailed("AbstractApplication: initialize application (route to '/')",
                             1);
    this.router.route("");
    // check if the url contains a hash.
    // in case it has a hash, use this to route otherwise
    // use the startRoute form the annoatation
    if (hashOnStart != null &&
        hashOnStart.trim()
                   .length() > 0) {
      ClientLogger.get()
                  .logDetailed("AbstractApplication: handle history (hash at start: >>" + hashOnStart + "<<",
                               1);
      HashResult hashResult = null;
      try {
        hashResult = this.router.parse(hashOnStart);
      } catch (RouterException e) {
        return;
      }
      this.router.route(hashResult.getRoute(),
                        hashResult.getParameterValues()
                                  .toArray(new String[0]));
    } else {
      ClientLogger.get()
                  .logDetailed("AbstractApplication: no history found -> use startRoute: >>" + this.startRoute + "<<",
                               1);
      this.router.route(this.startRoute);
    }
    ClientLogger.get()
                .logSimple("AbstractApplication: application started",
                           0);
  }

  protected abstract void attachShell();
}
