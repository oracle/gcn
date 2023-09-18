/*
 * Copyright 2023 Oracle and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.graal.gcn.feature.replaced;

import cloud.graal.gcn.template.GcnPropertiesTemplate;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.starter.feature.config.Configuration;
import io.micronaut.starter.feature.config.Properties;
import io.micronaut.starter.template.Template;
import jakarta.inject.Singleton;

import java.util.function.Function;

/**
 * Replaces the default feature to customize properties file writing.
 *
 * @since 1.0.0
 */
@Replaces(Properties.class)
@Singleton
public class GcnProperties extends Properties {

    @Override
    public Function<Configuration, Template> createTemplate() {
        return (config) -> new GcnPropertiesTemplate(config.getFullPath("properties"), config);
    }
}
