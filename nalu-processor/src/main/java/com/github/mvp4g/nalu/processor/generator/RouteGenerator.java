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
package com.github.mvp4g.nalu.processor.generator;

import com.github.mvp4g.nalu.client.internal.route.RouteConfig;
import com.github.mvp4g.nalu.processor.model.ApplicationMetaModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class RouteGenerator {

  private ApplicationMetaModel applicationMetaModel;
  private TypeSpec.Builder     typeSpec;

  @SuppressWarnings("unused")
  private RouteGenerator() {
  }

  private RouteGenerator(Builder builder) {
    this.applicationMetaModel = builder.applicationMetaModel;
    this.typeSpec = builder.typeSpec;
  }

  public static Builder builder() {
    return new Builder();
  }

  void generate() {
    // method must always be created!
    MethodSpec.Builder loadSelectorsMethod = MethodSpec.methodBuilder("loadRoutes")
                                                       .addAnnotation(Override.class)
                                                       .addModifiers(Modifier.PUBLIC);

    this.applicationMetaModel.getRoutes()
                             .forEach(route -> loadSelectorsMethod.addStatement("super.routerConfiguration.getRouters().add(new $T($S, $S, $S))",
                                                                                ClassName.get(RouteConfig.class),
                                                                                createRoute(route.getRoute()),
                                                                                route.getSelector(),
                                                                                route.getProvider()
                                                                                     .getClassName()));

    typeSpec.addMethod(loadSelectorsMethod.build());
  }

  private String createRoute(String route) {
    if (route.startsWith("/")) {
      return route;
    } else {
      return "/" + route;
    }
  }

  public static final class Builder {

    ApplicationMetaModel applicationMetaModel;
    TypeSpec.Builder     typeSpec;

    /**
     * Set the EventBusMetaModel of the currently generated eventBus
     *
     * @param applicationMetaModel meta data model of the eventbus
     * @return the Builder
     */
    public Builder applicationMetaModel(ApplicationMetaModel applicationMetaModel) {
      this.applicationMetaModel = applicationMetaModel;
      return this;
    }

    /**
     * Set the typeSpec of the currently generated eventBus
     *
     * @param typeSpec ttype spec of the crruent eventbus
     * @return the Builder
     */
    Builder typeSpec(TypeSpec.Builder typeSpec) {
      this.typeSpec = typeSpec;
      return this;
    }

    public RouteGenerator build() {
      return new RouteGenerator(this);
    }
  }
}
