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

package com.github.mvp4g.nalu.processor.scanner;

import com.github.mvp4g.nalu.client.component.annotation.Controller;
import com.github.mvp4g.nalu.client.handler.annotation.Handler;
import com.github.mvp4g.nalu.processor.ProcessorException;
import com.github.mvp4g.nalu.processor.model.ApplicationMetaModel;
import com.github.mvp4g.nalu.processor.model.intern.ClassNameModel;
import com.github.mvp4g.nalu.processor.scanner.validation.HandlerAnnotationValidator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

public class HandlerAnnotationScanner {

  private ProcessingEnvironment processingEnvironment;
  private RoundEnvironment      roundEnvironment;
  private ApplicationMetaModel  applicationMetaModel;

  @SuppressWarnings("unused")
  private HandlerAnnotationScanner(Builder builder) {
    super();
    this.processingEnvironment = builder.processingEnvironment;
    this.roundEnvironment = builder.roundEnvironment;
    this.applicationMetaModel = builder.applicationMetaModel;
    setUp();
  }

  private void setUp() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public ApplicationMetaModel scan()
    throws ProcessorException {
    // handle ProvidesSelector-annotation
    for (Element element : roundEnvironment.getElementsAnnotatedWith(Handler.class)) {
      // do validation
      HandlerAnnotationValidator.builder()
                                .roundEnvironment(roundEnvironment)
                                .processingEnvironment(processingEnvironment)
                                .handlerElement(element)
                                .build()
                                .validate();
      // save handler class in meta model
      this.applicationMetaModel.getHandlers()
                               .add(new ClassNameModel(element.toString()));
    }
    return this.applicationMetaModel;
  }

  private TypeElement getComponentTypeElement(Controller annotation) {
    try {
      annotation.component();
    } catch (MirroredTypeException exception) {
      return (TypeElement) this.processingEnvironment.getTypeUtils()
                                                     .asElement(exception.getTypeMirror());
    }
    return null;
  }

  private TypeElement getComponentInterfaceTypeElement(Controller annotation) {
    try {
      annotation.componentInterface();
    } catch (MirroredTypeException exception) {
      return (TypeElement) this.processingEnvironment.getTypeUtils()
                                                     .asElement(exception.getTypeMirror());
    }
    return null;
  }

  public static class Builder {

    ProcessingEnvironment processingEnvironment;
    RoundEnvironment      roundEnvironment;
    ApplicationMetaModel  applicationMetaModel;

    public Builder processingEnvironment(ProcessingEnvironment processingEnvironment) {
      this.processingEnvironment = processingEnvironment;
      return this;
    }

    public Builder applicationMetaModel(ApplicationMetaModel applicationMetaModel) {
      this.applicationMetaModel = applicationMetaModel;
      return this;
    }

    public Builder roundEnvironment(RoundEnvironment roundEnvironment) {
      this.roundEnvironment = roundEnvironment;
      return this;
    }

    public HandlerAnnotationScanner build() {
      return new HandlerAnnotationScanner(this);
    }
  }
}
