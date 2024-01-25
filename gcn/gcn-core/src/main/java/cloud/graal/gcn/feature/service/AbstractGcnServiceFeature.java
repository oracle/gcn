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
package cloud.graal.gcn.feature.service;

import cloud.graal.gcn.GcnGeneratorContext;
import cloud.graal.gcn.feature.AbstractGcnFeature;
import cloud.graal.gcn.model.GcnCloud;
import cloud.graal.gcn.model.GcnService;
import io.micronaut.core.annotation.NonNull;

import static io.micronaut.starter.feature.FeaturePhase.DEFAULT;

/**
 * Base class for service features.
 *
 * @since 1.0.0
 */
public abstract class AbstractGcnServiceFeature extends AbstractGcnFeature {

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public int getOrder() {
        return DEFAULT.getOrder() + 50;
    }

    /**
     * @return the service enum
     */
    @NonNull
    public abstract GcnService getService();

    /**
     * Set the cloud to NONE in GcnGeneratorContext to apply changes to the
     * "lib" module instead of the feature's cloud module, and switch back
     * after running the runnable.
     *
     * @param generatorContext the generator context
     * @param runnable         the code to run
     */
    protected void applyForLib(GcnGeneratorContext generatorContext,
                               Runnable runnable) {

        GcnCloud cloud = generatorContext.getCloud();

        generatorContext.resetCloud();

        try {
            runnable.run();
        } finally {
            generatorContext.setCloud(cloud);
        }
    }

    /**
     * Set the cloud to the feature's cloud in GcnGeneratorContext to apply
     * changes to the cloud module instead of the "lib" module, and switch back
     * after running the runnable.
     *
     * @param generatorContext the generator context
     * @param runnable         the code to run
     */
    protected void applyForCloud(GcnGeneratorContext generatorContext,
                                 Runnable runnable) {

        GcnCloud cloud = generatorContext.getCloud();

        generatorContext.setCloud(getCloud());

        try {
            runnable.run();
        } finally {
            generatorContext.setCloud(cloud);
        }
    }
}
