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

import com.github.mvp4g.nalu.processor.test.ApplicationTest;
import com.github.mvp4g.nalu.processor.test.ControllerTest;
import com.github.mvp4g.nalu.processor.test.DebugTest;
import com.github.mvp4g.nalu.processor.test.FilterTest;
import com.github.mvp4g.nalu.processor.test.model.intern.ClassNameModelTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ApplicationTest.class,
                     ClassNameModelTest.class,
                     ControllerTest.class,
                     DebugTest.class,
                     FilterTest.class})
public class AllTests {
}
