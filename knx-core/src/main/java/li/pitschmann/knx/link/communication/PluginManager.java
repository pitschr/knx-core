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

package li.pitschmann.knx.link.communication;

import com.google.common.base.Preconditions;
import li.pitschmann.knx.link.body.Body;
import li.pitschmann.knx.link.config.Config;
import li.pitschmann.knx.link.plugin.ExtensionPlugin;
import li.pitschmann.knx.link.plugin.ObserverPlugin;
import li.pitschmann.knx.link.plugin.Plugin;
import li.pitschmann.utils.Closeables;
import li.pitschmann.utils.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
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
    private final List<ObserverPlugin> observerPlugins = new LinkedList<>();
    private final List<ExtensionPlugin> extensionPlugins = new LinkedList<>();
    private final List<Plugin> allPlugins = new LinkedList<>();
    private final ExecutorService pluginExecutor;
    private KnxClient client;

    PluginManager(final @Nonnull Config config) {
        Preconditions.checkNotNull(config);

        final var pluginExecutorPoolSize = config.getPluginExecutorPoolSize();
        pluginExecutor = Executors.newFixedThreadPool(pluginExecutorPoolSize, true);
        log.info("Plugin Executor created with size of {}: {}", pluginExecutorPoolSize, pluginExecutor);
    }

    /**
     * Notifies all {@link ExtensionPlugin} and {@link ObserverPlugin} about KNX Client initialization
     * <p/>
     * <strong>For internal use only!</strong>
     */
    void notifyInitialization(final @Nonnull KnxClient client) {
        this.client = Objects.requireNonNull(client);
        this.client.getConfig().getPlugins().stream().forEach(this::registerPluginInternal);
        log.info("Observer Plugins: {}", observerPlugins);
        log.info("Extension Plugins: {}", extensionPlugins);
        notifyPlugins(client, allPlugins, Plugin::onInitialization);
    }

    /**
     * Notifies all {@link ObserverPlugin} about incoming {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyIncomingBody(final @Nonnull Body body) {
        notifyPlugins(body, observerPlugins, ObserverPlugin::onIncomingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about outgoing {@link Body}
     *
     * @param body any KNX body
     */
    public void notifyOutgoingBody(final @Nonnull Body body) {
        notifyPlugins(body, observerPlugins, ObserverPlugin::onOutgoingBody);
    }

    /**
     * Notifies all {@link ObserverPlugin} about throwable during incoming or outgoing {@link Body}
     *
     * @param throwable an instance of {@link Throwable} to be sent to plug-ins
     */
    public void notifyError(final @Nonnull Throwable throwable) {
        notifyPlugins(throwable, observerPlugins, ObserverPlugin::onError);
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication start
     * <p/>
     * <strong>For internal use only!</strong>
     */
    void notifyClientStart() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onStart());
    }

    /**
     * Notifies all {@link ExtensionPlugin} about KNX Client communication shutdown
     * <p/>
     * <strong>For internal use only!</strong>
     */
    void notifyClientShutdown() {
        notifyPlugins(null, extensionPlugins, (p, x) -> p.onShutdown());
    }

    /**
     * Registers the plugin and calls the initialization
     *
     * @param plugin
     */
    public void registerPlugin(final @Nonnull Plugin plugin) {
        registerPluginInternal(plugin);
        notifyPlugins(client, Collections.singletonList(plugin), Plugin::onInitialization);

        // for extension plugins, we have a special case:
        // if the KNX Client is already started -> kick in the onStart immediately!
        if (plugin instanceof ExtensionPlugin && client.isRunning()) {
            notifyPlugins(null, Collections.singletonList((ExtensionPlugin) plugin), (p, x) -> p.onStart());
        }

        log.info("Plugin registered: {}", plugin);
    }

    /**
     * Registers the plugin from given URL and class path
     * <p/>
     * Example: {@code ~/plugin/my-jar-file-0.0.1.jar} as {@code filePath} and
     * {@code com.mycompany.MyPlugin} as {@code classPath}.
     *
     * @param filePath  path to the JAR file
     * @param classPath fully qualified class name
     */
    public void registerPlugin(final @Nonnull Path filePath, final @Nonnull String classPath) {
        Preconditions.checkArgument(filePath.getFileName().toString().endsWith(".jar"),
                "File doesn't end with '.jar' extension: %s", filePath);
        Preconditions.checkNotNull(classPath);

        log.debug("Try to load plugin '{}' from path: {}", classPath, filePath);
        try {
            Preconditions.checkArgument(Files.isReadable(filePath),
                    "File doesn't exists or is not readable: %s", filePath);

            final var classLoader = new URLClassLoader(new URL[]{filePath.toUri().toURL()});
            final var cls = classLoader.loadClass(classPath);
            Preconditions.checkArgument(Plugin.class.isAssignableFrom(cls),
                    "Seems the given plugin is not an instance of %s: %s", Plugin.class, classPath);

            final var plugin = (Plugin) cls.getDeclaredConstructor().newInstance();
            log.debug("Plugin '{}' loaded from url '{}': {}", classPath, filePath, plugin);
            registerPlugin(plugin);
        } catch (final Throwable t) {
            log.error("Could not load plugin '{}' (url: {})", classPath, filePath);
        }
    }

    /**
     * Registers the plugin
     *
     * @param plugin
     */
    private void registerPluginInternal(final @Nonnull Plugin plugin) {
        final var pluginClass = plugin.getClass();
        Preconditions.checkArgument(getPlugin(pluginClass) == null,
                "There is already a plugin registered for class: %s", pluginClass);

        log.debug("Register plugin: {}", plugin);
        allPlugins.add(plugin);
        if (plugin instanceof ExtensionPlugin) {
            extensionPlugins.add((ExtensionPlugin) plugin);
            log.trace("Register plugin as Extension Plugin: {}", plugin);
        }
        if (plugin instanceof ObserverPlugin) {
            observerPlugins.add((ObserverPlugin) plugin);
            log.trace("Register plugin as Observer Plugin: {}", plugin);
        }
    }

    /**
     * Returns an an already-registered Plugin for given {@code pluginClass}
     *
     * @param pluginClass
     * @param <T>
     * @return An existing instance of {@link Plugin} if found, otherwise {@code null}
     */
    @Nullable
    public <T extends Plugin> T getPlugin(final @Nonnull Class<T> pluginClass) {
        Preconditions.checkNotNull(pluginClass);
        for (final var plugin : allPlugins) {
            if (pluginClass.equals(plugin.getClass())) {
                return pluginClass.cast(plugin);
            }
        }
        return null;
    }

    /**
     * De-Registers the plugin
     *
     * @param plugin
     */
    public void unregisterPlugin(final @Nonnull Plugin plugin) {
        final var pluginClass = plugin.getClass();
        Preconditions.checkArgument(getPlugin(pluginClass) != null,
                "No plugin is registered for class: %s", pluginClass);

        // plugin class is registered, remove it from extension and/or observer plugin lists
        for (var extensionPlugin : extensionPlugins) {
            if (pluginClass.equals(extensionPlugin.getClass())) {
                extensionPlugins.remove(extensionPlugin);
                break;
            }
        }
        for (var observerPlugin : observerPlugins) {
            if (pluginClass.equals(observerPlugin.getClass())) {
                observerPlugins.remove(observerPlugin);
                break;
            }
        }
    }

    /**
     * Notifies the registered plug-ins about {@code <O>}.
     *
     * @param obj      object to be sent to plug-ins (for non-arg method the object may be {@code null})
     * @param plugins  list of plug-ins to be notified
     * @param consumer consumer defining which method should be called
     */
    private <O, P extends Plugin> void notifyPlugins(final @Nullable O obj,
                                                     final @Nonnull List<P> plugins,
                                                     final @Nonnull BiConsumer<P, O> consumer) {
        if (this.pluginExecutor.isShutdown()) {
            log.warn("Could not send to plug-ins because plugin executor is shutdown already: {}",
                    obj instanceof Throwable ? ((Throwable) obj).getMessage() : obj);
        } else {
            for (final P plugin : plugins) {
                CompletableFuture.runAsync(() -> {
                    log.trace("Send to plugin: {}", plugin);
                    try {
                        consumer.accept(plugin, obj);
                    } catch (final Exception ex) {
                        log.debug("Exception during notifyPlugins(T, List<Plugin>, BiConsumer)", ex);
                    }
                }, this.pluginExecutor);
            }
        }
    }

    @Override
    public void close() {
        Closeables.shutdownQuietly(this.pluginExecutor, 10, TimeUnit.SECONDS);
    }
}