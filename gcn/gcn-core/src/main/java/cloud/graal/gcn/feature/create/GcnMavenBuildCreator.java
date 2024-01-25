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
package cloud.graal.gcn.feature.create;

import cloud.graal.gcn.buildtool.GcnMavenBuild;
import cloud.graal.gcn.feature.replaced.template.gcnMvnPluginJTE;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.order.OrderUtil;
import io.micronaut.starter.application.generator.GeneratorContext;
import io.micronaut.starter.build.Repository;
import io.micronaut.starter.build.dependencies.DependencyCoordinate;
import io.micronaut.starter.build.dependencies.Scope;
import io.micronaut.starter.build.maven.MavenBuild;
import io.micronaut.starter.build.maven.MavenBuildCreator;
import io.micronaut.starter.build.maven.MavenPlugin;
import io.micronaut.starter.build.maven.MavenRepository;
import io.micronaut.starter.feature.build.maven.templates.mavenPlugin;
import io.micronaut.starter.template.RockerWritable;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static cloud.graal.gcn.feature.replaced.GcnJTE.JTE_NATIVE_RESOURCES;

/**
 * Extends MavenBuildCreator to add the Oracle Maven repo.
 *
 * @since 1.0.0
 */
@Singleton
public class GcnMavenBuildCreator extends MavenBuildCreator {

    @NonNull
    @Override
    public MavenBuild create(GeneratorContext generatorContext, List<Repository> repositories) {

        MavenBuild build = super.create(generatorContext, repositories);

        List<MavenPlugin> plugins = generatorContext.getBuildPlugins()
                .stream()
                .filter(MavenPlugin.class::isInstance)
                .map(plugin -> clonePlugin((MavenPlugin) plugin))
                .sorted(OrderUtil.COMPARATOR)
                .collect(Collectors.toList());

        return new GcnMavenBuild(
                build.getArtifactId(),
                deduplicate(build.getAnnotationProcessors()),
                deduplicate(build.getTestAnnotationProcessors()),
                build.getDependencies(),
                build.getProperties(),
                plugins,
                MavenRepository.listOf(repositories),
                build.getAnnotationProcessorCombineAttribute(),
                build.getTestAnnotationProcessorCombineAttribute(),
                build.getProfiles(),
                generatorContext.getDependencies().stream().filter(dep -> dep.getScope() == Scope.AOT_PLUGIN).map(DependencyCoordinate::new).toList());
    }

    private List<DependencyCoordinate> deduplicate(List<DependencyCoordinate> annotationProcessors) {
        return new ArrayList<>(new LinkedHashSet<>(annotationProcessors));
    }

    private MavenPlugin clonePlugin(MavenPlugin plugin) {

        if ("jte-maven-plugin".equals(plugin.getArtifactId())) {
            gcnMvnPluginJTE extensionModel = (gcnMvnPluginJTE) ((RockerWritable) plugin.getExtension()).getModel();
            return new MavenPlugin(
                    plugin.getArtifactId(),
                    new RockerWritable(gcnMvnPluginJTE.template(
                            extensionModel.groupId(),
                            extensionModel.artifactId(),
                            JTE_NATIVE_RESOURCES,
                            extensionModel.version(),
                            extensionModel.templateDirectory())), 0);
        }

        mavenPlugin extensionModel = (mavenPlugin) ((RockerWritable) plugin.getExtension()).getModel();
        return new MavenPlugin(
                plugin.getArtifactId(),
                new RockerWritable(mavenPlugin.template(
                        extensionModel.groupId(),
                        extensionModel.artifactId())), 0);
    }
}
