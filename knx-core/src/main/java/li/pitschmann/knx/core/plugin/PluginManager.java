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

import javax.annotation.Nullable;
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

/**
 * Plugin Manager
 * <p/>
 * This class is mutable and allows register/unregister plugins on-demand.
 *
 * @author PITSCHR
 */
public final class PluginManager implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(PluginManager.class);
    private static final BiConsumer<List<? extends Plugin>, Class<? extends Plugin>> UNREGISTER_PLUGIN_FUNCTION =
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
        log.info("Plugin Executor created with size of {}: {}", pluginExecutorPoolSize, pluginExecutor);
    }

    /**
     * Notifies all {@link Plugin} about KNX Client initialization
     * <p/>
     * <strong>For internal use only!</strong>
     */
    public void notifyInitialization(final KnxClient client) {
        this.client = Objects.requireNonNull(client);
        this.client.getConfig().getPlugins().parallelStream().forEach(this::registerPluginInternal);
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
     * @param throwable an instance of {@link Throwable} to be sent to plug-ins
     */
    public void notifyError(final Throwable throwable) {
        notifyPlugins(Objects.requireNonNull(throwable), observerPlugins, ObserverPlugin::onError);
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication start
     * <p/>
     * <strong>For internal use only!</strong>
     */
    public void notifyClientStart() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onStart());
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication shutdown
     * <p/>
     * <strong>For internal use only!</strong>
     */
    public void notifyClientShutdown() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onShutdown());
    }

    /**
     * Adds and registers the plugin from given URL and class path
     * <p/>
     * Example: {@code ~/plugin/my-jar-file-0.0.1.jar} as {@code filePath} and
     * {@code com.mycompany.MyPlugin} as {@code className}.
     *
     * @param filePath  path to the JAR file
     * @param className fully qualified class name
     * @return a new {@link Plugin} loaded from given URL and fully qualified class name
     */
    public Plugin addPlugin(final Path filePath, final String className) {
        Preconditions.checkArgument(filePath.getFileName().toString().endsWith(".jar"),
                "File doesn't end with '.jar' extension: {}", filePath);
        Preconditions.checkNonNull(className);
        Preconditions.checkArgument(Files.isReadable(filePath),
                "File doesn't exists or is not readable: {}", filePath);

        log.debug("Try to load plugin '{}' from path: {}", className, filePath);
        try (var classLoader = new URLClassLoader(new URL[]{filePath.toUri().toURL()})) {
            final var cls = classLoader.loadClass(className);
            Preconditions.checkArgument(Plugin.class.isAssignableFrom(cls),
                    "Seems the given plugin is not an instance of {}: {}", Plugin.class, className);

            @SuppressWarnings("unchecked") final var plugin = addPlugin((Class<Plugin>) cls);
            log.debug("Plugin '{}' loaded from url '{}': {}", className, filePath, plugin);
            return plugin;
        } catch (final Throwable t) {
            throw new KnxPluginException("Could not load plugin '" + className + "' at: " + filePath, t);
        }
    }

    /**
     * Creates and registers the plugin
     *
     * @param pluginClass
     * @return new instance of {@link Plugin}
     */
    public <T extends Plugin> T addPlugin(final Class<T> pluginClass) {
        final var plugin = registerPluginInternal(pluginClass);

        // for extension plugins, we have a special case:
        // if the KNX Client is already started -> kick in the onStart immediately!
        if (plugin instanceof ExtensionPlugin && client.isRunning()) {
            notifyPluginInternal(null, ExtensionPlugin.class.cast(plugin), (p, x) -> p.onStart());
        }

        return plugin;
    }

    /**
     * Creates and registers the plugin based on {@code pluginClass}
     * <p/>
     * It will give plugin 10 seconds time for create. Otherwise, the plugin won't be registered.
     *
     * @param pluginClass
     * @param <T>
     * @return new instance of plugin
     */
    private <T extends Plugin> T registerPluginInternal(final Class<T> pluginClass) {
        Preconditions.checkArgument(getPlugin(pluginClass) == null,
                "There is already a plugin registered for class: {}", pluginClass);

        final var sw = Stopwatch.createStarted();
        final T plugin;
        try {
            plugin = pluginClass.getDeclaredConstructor().newInstance();
            log.debug("Creation completed for plugin: {}", plugin);

            // now perform onInitialization method and give a specific time to start
            final var future = notifyPluginInternal(client, plugin, Plugin::onInitialization);
            if (future != null) {
                final var timeoutInMs = this.client.getConfig(CoreConfigs.Plugin.INITIALIZATION_TIMEOUT);
                future.get(timeoutInMs, TimeUnit.MILLISECONDS);
                log.debug("Initialization completed for plugin: {}", plugin);
            }
        } catch (final Throwable t) {
            throw new KnxPluginException("Could not load plugin: " + pluginClass.getName(), t);
        }

        allPlugins.add(plugin);
        if (plugin instanceof ExtensionPlugin) {
            extensionPlugins.add((ExtensionPlugin) plugin);
            log.debug("Register plugin as Extension Plugin: {}", plugin);
        }
        if (plugin instanceof ObserverPlugin) {
            observerPlugins.add((ObserverPlugin) plugin);
            log.debug("Register plugin as Observer Plugin: {}", plugin);
        }

        log.info("Plugin created, initialized and registered in {} ms: {}", sw.elapsed(TimeUnit.MILLISECONDS), plugin);

        return plugin;
    }

    /**
     * Returns an an already-registered Plugin for given {@code pluginClass}
     *
     * @param pluginClass
     * @param <T>
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
     * De-Registers the plugin by class
     *
     * @param pluginClass
     * @param <T>
     * @return plugin instance that is being de-registered, otherwise {@link IllegalArgumentException} will be thrown
     * @thows IllegalArgumentException in case the plugin class could not be found
     */
    public <T extends Plugin> T unregisterPlugin(final Class<T> pluginClass) {
        final var plugin = getPlugin(pluginClass);
        Preconditions.checkArgument(plugin != null,
                "No plugin is registered for class: {}", pluginClass);

        // plugin class is registered, remove it from extension and/or observer plugin lists
        UNREGISTER_PLUGIN_FUNCTION.accept(allPlugins, pluginClass);
        UNREGISTER_PLUGIN_FUNCTION.accept(extensionPlugins, pluginClass);
        UNREGISTER_PLUGIN_FUNCTION.accept(observerPlugins, pluginClass);

        return plugin;
    }

    /**
     * Notifies the list of plug-ins about {@code <O>}.
     *
     * @param obj      object to be sent to plug-ins (for non-arg method the object may be {@code null})
     * @param plugins  list of plug-ins to be notified
     * @param consumer consumer defining which method should be called
     * @param <O>
     * @param <P>
     */
    private <O, P extends Plugin> void notifyPlugins(final @Nullable O obj,
                                                     final List<P> plugins,
                                                     final BiConsumer<P, O> consumer) {
        for (final P plugin : plugins) {
            notifyPluginInternal(obj, plugin, consumer);
        }
    }

    /**
     * Notifies the plugin about {@code <O>}
     *
     * @param obj      object to be sent to plug-ins (for non-arg method the object may be {@code null})
     * @param plugin   plug-ins to be notified
     * @param consumer consumer defining which method should be called
     * @param <O>
     * @param <P>
     * @return future for further check
     */
    @Nullable
    private <O, P extends Plugin> Future<Void> notifyPluginInternal(final @Nullable O obj,
                                                                    final P plugin,
                                                                    final BiConsumer<P, O> consumer) {
        if (this.pluginExecutor.isShutdown()) {
            log.warn("Could not send to plug-in '{}' because plugin executor is shutdown already: {}",
                    plugin, obj instanceof Throwable ? ((Throwable) obj).getMessage() : obj);
            return null;
        } else {
            return CompletableFuture.runAsync(() -> {
                log.trace("Send to plugin: {}", plugin);
                try {
                    consumer.accept(plugin, obj);
                } catch (final Exception ex) {
                    log.warn("Exception during notifyPlugins(T, List<Plugin>, BiConsumer): obj={}, plugin={}", obj, plugin, ex);
                }
            }, this.pluginExecutor);
        }
    }

    @Override
    public void close() {
        Closeables.shutdownQuietly(this.pluginExecutor, 10, TimeUnit.SECONDS);
    }
}
