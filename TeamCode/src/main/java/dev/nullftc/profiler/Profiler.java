package dev.nullftc.profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.nullftc.profiler.entry.ProfilerEntry;
import dev.nullftc.profiler.entry.ProfilerEntryFactory;
import dev.nullftc.profiler.exporter.ProfilerExporter;

/**
 * Main Profiler class for tracking execution time of various components.
 */
public class Profiler {

    private final ProfilerEntryFactory factory;
    private final ProfilerExporter exporter;
    private final boolean debugLog;
    public static final Logger LOGGER = LoggerFactory.getLogger("FTCProfiler");

    private final Map<String, Long> activeTimers = new HashMap<>();
    private final List<ProfilerEntry> entries = new ArrayList<>();

    /**
     * Constructs a new Profiler.
     *
     * @param factory  the factory to create entries
     * @param exporter the exporter to use
     * @param debugLog whether to log debug information
     */
    private Profiler(ProfilerEntryFactory factory, ProfilerExporter exporter, boolean debugLog) {
        this.factory = factory;
        this.exporter = exporter;
        this.debugLog = debugLog;

        if (debugLog) {
            LOGGER.info("Profiler initialized with factory={} exporter={}", factory.getClass().getSimpleName(), exporter.getClass().getSimpleName());
        }
    }

    /**
     * Starts a timer for the given type.
     *
     * @param type the type of entry to start
     */
    public void start(String type) {
        activeTimers.put(type, System.currentTimeMillis());
        if (debugLog) {
            LOGGER.info("Profiler start: {}", type);
        }
    }

    /**
     * Ends the timer for the given type and records the entry.
     *
     * @param type the type of entry to end
     */
    public void end(String type) {
        Long start = activeTimers.remove(type);
        if (start == null) {
            if (debugLog) {
                LOGGER.warn("Profiler warning: end() called for '{}' with no start()", type);
            }
            return;
        }

        long end = System.currentTimeMillis();
        ProfilerEntry entry = factory.create(type, start, end);
        entries.add(entry);

        if (debugLog) {
            LOGGER.info("Profiler end: {} | Duration={}ms", type, entry.getDeltaTime());
        }
    }

    /**
     * Exports all recorded entries using the configured exporter.
     */
    public void export() {
        if (debugLog) {
            LOGGER.info("Profiler export started | {} entries to export", entries.size());
        }
        exporter.export(new ArrayList<>(entries));
        if (debugLog) {
            LOGGER.info("Profiler export complete | File exported by {}", exporter.getClass().getSimpleName());
        }
    }

    /**
     * Shuts down the profiler.
     */
    public void shutdown() {
        if (debugLog) {
            LOGGER.info("Profiler shutdown called");
        }
    }

    /**
     * Creates a new Builder for the Profiler.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for {@link Profiler}.
     */
    public static class Builder {
        private ProfilerEntryFactory factory;
        private ProfilerExporter exporter;
        private boolean debugLog = false;

        public Builder factory(ProfilerEntryFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder exporter(ProfilerExporter exporter) {
            this.exporter = exporter;
            return this;
        }

        public Builder debugLog(boolean debugLog) {
            this.debugLog = debugLog;
            return this;
        }

        public Profiler build() {
            if (factory == null) {
                throw new IllegalStateException("ProfilerEntryFactory not set");
            }
            if (exporter == null) {
                throw new IllegalStateException("ProfilerExporter not set");
            }
            return new Profiler(factory, exporter, debugLog);
        }
    }
}
