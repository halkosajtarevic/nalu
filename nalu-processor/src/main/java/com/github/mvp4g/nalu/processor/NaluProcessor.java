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

package com.github.mvp4g.nalu.processor;


import com.github.mvp4g.nalu.client.application.annotation.Application;
import com.github.mvp4g.nalu.client.application.annotation.Debug;
import com.github.mvp4g.nalu.client.component.annotation.Controller;
import com.github.mvp4g.nalu.client.handler.annotation.Handler;
import com.github.mvp4g.nalu.processor.generator.ApplicationGenerator;
import com.github.mvp4g.nalu.processor.model.ApplicationMetaModel;
import com.github.mvp4g.nalu.processor.scanner.ApplicationAnnotationScanner;
import com.github.mvp4g.nalu.processor.scanner.HandlerAnnotationScanner;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NaluProcessor
  extends AbstractProcessor {

  private ProcessorUtils processorUtils;

  private ApplicationAnnotationScanner applicationAnnotationScanner;
  private HandlerAnnotationScanner     handlerAnnotationScanner;

  private ApplicationGenerator applicationGenerator;

  private ApplicationMetaModel applicationMetaModel;

  public NaluProcessor() {
    super();
  }

  // TODO do we need Debug, ProvidesSelector & Controller here?
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return of(Application.class.getCanonicalName(),
              Debug.class.getCanonicalName(),
              Controller.class.getCanonicalName(),
              Handler.class.getCanonicalName()).collect(toSet());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations,
                         RoundEnvironment roundEnv) {
    setUp(roundEnv);
    processorUtils.createNoteMessage("Nalu-Processor triggered: " + System.currentTimeMillis());
    try {
      if (roundEnv.processingOver()) {
        this.generateLastRound();
      } else {
        if (annotations.size() > 0) {
          this.scan(roundEnv);
//        this.validateModels(roundEnv);
          this.generate();
        }
      }
      return true;
    } catch (ProcessorException e) {
      this.processorUtils.createErrorMessage(e.getMessage());
    }
    return true;
  }

  private void setUp(RoundEnvironment roundEnv) {
    this.processorUtils = ProcessorUtils.builder()
                                        .processingEnvironment(processingEnv)
                                        .build();

    this.applicationAnnotationScanner = ApplicationAnnotationScanner.builder()
                                                                    .roundEnvironment(roundEnv)
                                                                    .processingEnvironment(this.processingEnv)
                                                                    .build();

    this.applicationGenerator = ApplicationGenerator.builder()
                                                    .processingEnvironment(this.processingEnv)
                                                    .build();
  }

  private void generateLastRound()
    throws ProcessorException {
    if (!isNull(this.applicationMetaModel)) {
      this.applicationGenerator.generate(this.applicationMetaModel);
    }
  }

  private void scan(RoundEnvironment roundEnv)
    throws ProcessorException {
    this.applicationMetaModel = this.applicationAnnotationScanner.scan();
    // cause we need the meta model, we have to create the Handler scanner here!
    this.handlerAnnotationScanner = HandlerAnnotationScanner.builder()
                                                            .roundEnvironment(roundEnv)
                                                            .processingEnvironment(this.processingEnv)
                                                            .applicationMetaModel(this.applicationMetaModel)
                                                            .build();
    this.applicationMetaModel = this.handlerAnnotationScanner.scan();
  }

  private void generate()
    throws ProcessorException {
  }
}
