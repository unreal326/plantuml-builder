/*-
 * #%L
 * Plantuml builder
 * %%
 * Copyright (C) 2017 Focus IT
 * %%
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * #L%
 */
package ch.ifocusit.plantuml.classdiagram;

import ch.ifocusit.plantuml.PlantUmlBuilder;
import ch.ifocusit.plantuml.test.helper.domain.*;
import ch.ifocusit.plantuml.utils.ClassUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import static org.fest.assertions.Assertions.assertThat;

public class ClassDiagramBuilderTest {

    private static final String CR = PlantUmlBuilder.NEWLINE;

    @Test
    public void buildShouldGenerateDiagram() throws Exception {
        String expected = IOUtils.toString(this.getClass().getResourceAsStream("/class-diagram.plantuml"), Charset.defaultCharset());

        // tag::createSimple[]
        String diagram = new ClassDiagramBuilder()
                .excludes(".*\\.ignored")
                .addClasses(Vehicule.class, Car.class, Driver.class, Price.class, Wheel.class, Devise.class)
                .build();
        // end::createSimple[]

        assertThat(diagram).isEqualTo(expected);
    }

    @Test
    public void buildShouldExportOnlyAnnotatedClassAndField() throws Exception {
        String diagram = new ClassDiagramBuilder()
                .excludes(".*\\.ignored")
                // only annotated
                .addFieldPredicate(attribute -> attribute.getField().isAnnotationPresent(Machine.class))
                .addClasses(Car.class)
                .build();

        assertThat(diagram).isEqualTo("@startuml" + CR + CR +
                "class Car {" + CR +
                "  brand : String" + CR +
                "  model : String" + CR +
                "  wheels : Collection<Wheel>" + CR +
                "}" + CR + CR + CR +
                "@enduml");
    }

    @Test
    public void testOverrideNames() {
        String diagram = new ClassDiagramBuilder()
                .excludes(".*\\.ignored")
                // only annotated
                .addFieldPredicate(attribute -> attribute.getField().isAnnotationPresent(Machine.class))
                .addClasses(Car.class)
                .withNamesMapper(new NamesMapper() {
                    @Override
                    public String getClassNameForDiagram(Class aClass) {
                        return "domain." + ClassUtils.getSimpleName(aClass);
                    }

                    @Override
                    public String getFieldNameForDiagram(Field field) {
                        return "attr." + field.getName();
                    }
                })
                .build();

        assertThat(diagram).isEqualTo("@startuml" + CR + CR +
                "class domain.Car {" + CR +
                "  attr.brand : String" + CR +
                "  attr.model : String" + CR +
                "  attr.wheels : Collection<Wheel>" + CR +
                "}" + CR + CR + CR +
                "@enduml");
    }
}
