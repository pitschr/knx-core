/*
 * KNX Link - A library for KNX Net/IP communication
 * Copyright (C) 2019 Pitschmann Christoph
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package li.pitschmann.knx.core.plugin;

import li.pitschmann.knx.core.annotations.Nullable;
import li.pitschmann.knx.core.body.Body;
import li.pitschmann.knx.core.communication.KnxClient;
import li.pitschmann.knx.core.config.Config;
import li.pitschmann.knx.core.config.CoreConfigs;
import li.pitschmann.knx.core.exceptions.KnxPluginException;
import li.pitschmann.knx.core.utils.Closeables;
import li.pitschmann.knx.core.utils.Executors;
import li.pitschmann.knx.core.utils.Preconditions;
import li.pitschmann.knx.core.utils.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Plugin Manager
 * <p>
 * This class is a storage for all plugins that listens to events
 * triggered by the KNX client. An instance of {@link PluginManager}
 * is mutable and allows to add or remove plugins on-demand.
 *
 * @author PITSCHR
 */
public final class PluginManager implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private static final BiConsumer<List<? extends Plugin>, Class<? extends Plugin>> REMOVE_PLUGIN_FUNCTION =
            (pluginList, pluginClass) -> {
                for (var p : pluginList) {
                    if (pluginClass.equals(p.getClass())) {
                        pluginList.remove(p);
                        break;
                    }
                }
            };
    private final List<ObserverPlugin> observerPlugins = Collections.synchronizedList(new LinkedList<>());
    private final List<ExtensionPlugin> extensionPlugins = Collections.synchronizedList(new LinkedList<>());
    private final List<Plugin> allPlugins = Collections.synchronizedList(new LinkedList<>());
    private final ExecutorService pluginExecutor;
    private KnxClient client;

    public PluginManager(final Config config) {
        final var pluginExecutorPoolSize = config.getValue(CoreConfigs.Plugin.EXECUTOR_POOL_SIZE);
        pluginExecutor = Executors.newFixedThreadPool(pluginExecutorPoolSize, true);
        log.debug("Plugin Executor created with size of {}: {}", pluginExecutorPoolSize, pluginExecutor);
    }

    /**
     * Notifies all {@link Plugin} about KNX Client initialization
     * <p>
     * <strong>For internal use only!</strong>
     *
     * @param client the KNX client that should be forwarded to plugin
     */
    public void notifyInitialization(final KnxClient client) {
        this.client = Objects.requireNonNull(client);
        this.client.getConfig().getPlugins().parallelStream().forEach(this::addPlugin);
        log.info("All Plugins: {}", allPlugins);
        log.info("Observer Plugins: {}", observerPlugins);
        log.info("Extension Plugins: {}", extensionPlugins);
    }

    /**
     * Notifies all {@link ObserverPlugin} about incoming {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyIncomingBody(final Body body) {
        notifyPlugins(Objects.requireNonNull(body), observerPlugins, ObserverPlugin::onIncomingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about outgoing {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyOutgoingBody(final Body body) {
        notifyPlugins(Objects.requireNonNull(body), observerPlugins, ObserverPlugin::onOutgoingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about throwable during incoming or outgoing {@link Body}
     *
     * @param cause the cause to be sent to plug-ins
     */
    public void notifyError(final Throwable cause) {
        notifyPlugins(Objects.requireNonNull(cause), observerPlugins, ObserverPlugin::onError);
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication start
     * <p>
     * <strong>For internal use only!</strong>
     */
    public void notifyClientStart() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onStart());
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication shutdown
     * <p>
     * <strong>For internal use only!</strong>
     */
    public void notifyClientShutdown() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onShutdown());
    }

    /**
     * Creates and add the plugin from given URL and fully qualified class name.
     * <p>
     * Example: {@code ~/plugin/my-jar-file-0.0.1.jar} as {@code filePath} and
     * {@code com.mycompany.MyPlugin} as {@code className}.
     *
     * @param filePath  path to the JAR file
     * @param className fully qualified class name
     * @return a new {@link Plugin} instance
     */
    public Plugin addPlugin(final Path filePath, final String className) {
        Preconditions.checkArgument(filePath.getFileName().toString().endsWith(".jar"),
                "File doesn't end with '.jar' extension: {}", filePath);
        Preconditions.checkNonNull(className);
        Preconditions.checkArgument(Files.isReadable(filePath),
                "File doesn't exists or is not readable: {}", filePath);

        log.debug("Try to load plugin '{}' from path: {}", className, filePath);
        final Plugin plugin;
        try (var classLoader = new URLClassLoader(new URL[]{filePath.toUri().toURL()})) {
            plugin = newPluginInstance(classLoader.loadClass(className));
            log.debug("Plugin '{}' loaded from url '{}': {}", className, filePath, plugin);
        } catch (final Exception e) {
            throw new KnxPluginException("Could not load plugin '" + className + "' at: " + filePath, e);
        }

        // all OK here, add plugin to the manager and return the new instance
        return addPlugin(plugin);
    }

    /**
     * Creates and registers the plugin based on {@code pluginClass}.
     * Precondition for that is that the class implements:
     * <ul>
     *     <li>the {@link Plugin} interface</li>
     *     <li>has a public null-arg constructor</li>
     * </ul>
     * <p>
     * It will give plugin 10 seconds time for create. Otherwise, the plugin won't be added nor registered.
     *
     * @param pluginClass the plugin class to create a new instance
     * @return new instance of plugin
     * @throws KnxPluginException if the plugin could not be loaded
     */
    private Plugin newPluginInstance(final Class<?> pluginClass) {
        // validation if the class implements the Plugin interface
        if (!Plugin.class.isAssignableFrom(pluginClass)) {
            throw new KnxPluginException("Seems the given class is not an instance of {}: {}", Plugin.class, pluginClass);
        }

        // validation if the class has a public null-arg constructor
        if (Stream.of(pluginClass.getConstructors()).noneMatch(c -> c.getParameterCount() == 0)) {
            throw new KnxPluginException("There seems be no public null-arg constructor available for: {}", pluginClass);
        }

        // now try to load the plugin
        try {
            final var plugin = (Plugin)pluginClass.getDeclaredConstructor().newInstance();
            log.debug("Created a new Plugin instance: {}", plugin);
            return plugin;
        } catch (final ReflectiveOperationException e) {
            throw new KnxPluginException("Could not load plugin: {}", pluginClass.getName(), e);
        }
    }

    /**
     * Adds the {@link Plugin} to the Plugin Manager
     * <p>
     * It will give plugin some time for initialization. Otherwise, the plugin won't be recognized.
     * This is done to avoid in case the plugin was badly designed.
     * See: {@link CoreConfigs.Plugin#INITIALIZATION_TIMEOUT}
     *
     * @param plugin the plugin to be added
     */
    public <T extends Plugin> T addPlugin(final T plugin) {
        final var pluginClass = plugin.getClass();
        Preconditions.checkArgument(getPlugin(pluginClass) == null,
                "There is already a plugin added for class: {}", pluginClass);

        final var sw = Stopwatch.createStarted();
        try {
            // now perform onInitialization method and give a specific time to start
            final var future = notifyPlugin(client, plugin, Plugin::onInitialization);
            if (future != null) {
                final var timeoutInMs = this.client.getConfig(CoreConfigs.Plugin.INITIALIZATION_TIMEOUT);
                future.get(timeoutInMs, TimeUnit.MILLISECONDS);
                log.debug("Initialization completed for plugin: {}", plugin);
            }
        } catch (final Exception e) {
            throw new KnxPluginException("Could not initialize plugin: {}", plugin, e);
        }

        allPlugins.add(plugin);
        if (plugin instanceof ExtensionPlugin) {
            extensionPlugins.add((ExtensionPlugin) plugin);
            log.debug("Add plugin as Extension Plugin: {}", plugin);
            // for extension plugins, we have a special case:
            // if the KNX Client is already started -> kick in the onStart immediately!
            if (client.isRunning()) {
                notifyPlugin(null, (ExtensionPlugin) plugin, (p, x) -> p.onStart());
            }
        }
        if (plugin instanceof ObserverPlugin) {
            observerPlugins.add((ObserverPlugin) plugin);
            log.debug("Add plugin as Observer Plugin: {}", plugin);
        }

        log.info("Plugin initialized and added in {} ms: {}", sw.elapsed(TimeUnit.MILLISECONDS), plugin);
        return plugin;
    }

    /**
     * Returns an already-added Plugin for given {@code pluginClass}.
     * There can only be a plugin exists with the same class.
     *
     * @param pluginClass the plugin class for look up
     * @param <T>         the instance of {@link Plugin} which is an instance of {@code pluginClass}
     * @return An existing instance of {@link Plugin} if found, otherwise {@code null}
     */
    @Nullable
    public <T extends Plugin> T getPlugin(final Class<T> pluginClass) {
        Preconditions.checkNonNull(pluginClass);
        for (final var plugin : allPlugins) {
            if (pluginClass.equals(plugin.getClass())) {
                return pluginClass.cast(plugin);
            }
        }
        return null;
    }

    /**
     * Removes the plugin by class from the Plugin Manager.
     * There can only one plugin exists with the same class.
     *
     * @param pluginClass the class of plugin to be removed
     * @param <T>         the instance of {@link Plugin} which is an instance of {@code pluginClass}
     * @return plugin instance that has been removed if found, otherwise {@code null}
     */
    @Nullable
    public <T extends Plugin> T removePlugin(final Class<T> pluginClass) {
        final var plugin = getPlugin(pluginClass);
        if (plugin != null) {
            // remove it from all plugin lists
            REMOVE_PLUGIN_FUNCTION.accept(allPlugins, pluginClass);
            REMOVE_PLUGIN_FUNCTION.accept(extensionPlugins, pluginClass);
            REMOVE_PLUGIN_FUNCTION.accept(observerPlugins, pluginClass);
        }
        return plugin;
    }

    /**
     * Notifies the list of plug-ins about {@code object}.
     *
     * @param object   object to be sent to plug-ins (for non-arg method the object may be {@code null})
     * @param plugins  list of plug-ins to be notified
     * @param consumer consumer defining which method should be called
     * @param <P>      the instance of {@link Plugin}
     * @param <T>      the instance of object
     */
    private <T, P extends Plugin> void notifyPlugins(final @Nullable T object,
                                                     final List<P> plugins,
                                                     final BiConsumer<P, T> consumer) {
        for (final var plugin : plugins) {
            notifyPlugin(object, plugin, consumer);
        }
    }

    /**
     * Notifies the plugin about {@code object}
     *
     * @param object   object to be sent to plug-ins (for non-arg method the object may be {@code null})
     * @param plugin   plug-ins to be notified
     * @param consumer consumer defining which method should be called
     * @param <P>      the instance of {@link Plugin}
     * @param <T>      the instance of object
     * @return future for further check
     */
    @Nullable
    private <T, P extends Plugin> Future<Void> notifyPlugin(final @Nullable T object,
                                                            final P plugin,
                                                            final BiConsumer<P, T> consumer) {
        if (this.pluginExecutor.isShutdown()) {
            log.warn("Could not send to plug-in '{}' because plugin executor is shutdown already: {}",
                    plugin, object instanceof Throwable ? ((Throwable) object).getMessage() : object);
            return null;
        } else {
            return CompletableFuture.runAsync(() -> {
                log.trace("Send to plugin: {}", plugin);
                try {
                    consumer.accept(plugin, object);
                } catch (final Exception ex) {
                    log.warn("Exception during notifyPlugins(T, List<Plugin>, BiConsumer): object={}, plugin={}", object, plugin, ex);
                }
            }, this.pluginExecutor);
        }
    }

    @Override
    public void close() {
        Closeables.shutdownQuietly(this.pluginExecutor, 10, TimeUnit.SECONDS);
    }
}
