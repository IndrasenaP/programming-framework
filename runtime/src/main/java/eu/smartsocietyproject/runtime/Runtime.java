package eu.smartsocietyproject.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.typesafe.config.Config;
import eu.smartsocietyproject.pf.*;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Runtime {
    private static ObjectMapper jsonMapper = new  ObjectMapper();
    private final ApplicationContext context;
    private final Application application;
    private final ConcurrentHashMap<UUID, TaskRunnerDescriptor> runnerDescriptors = new ConcurrentHashMap<>();
    private final ExecutorService executor = new ThreadPoolExecutor( //can return both Executor and ExecutorService
     30,// the number of threads to keep active in the pool, even if they are idle
     1000,// the maximum number of threads to allow in the pool. After that, the tasks are queued
     1L, TimeUnit.HOURS,// when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
     new LinkedBlockingQueue<Runnable>()
    );

    public Runtime(ApplicationContext context, Application application) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(application);
        this.context = context;
        this.application = application;
    }

    boolean startTask(TaskDefinition definition) {
        TaskRequest request = application.createTaskRequest(definition);

        TaskRunner runner = application.createTaskRunner(request);
        executor.execute(runner);
        return true;
    }

    Optional<JsonNode> monitor(UUID taskId) {
        return
            Optional
                .ofNullable(runnerDescriptors.get(taskId))
            .map(d->d.getStateDescription());
    }

    void cancel(UUID taskId) {
        TaskRunnerDescriptor descriptor = runnerDescriptors.get(taskId);
        if ( descriptor != null ) {
            descriptor.cancel();
        }
    }

    private static class TaskRunnerDescriptor {
        private final long creationTimestamp = java.time.Instant.now().toEpochMilli();
        private final ExecutorService executor;
        private final TaskDefinition definition;
        private final CollectiveBasedTask cbt;
        private final TaskRunner runner;
        private final Function<Runnable, TaskResult> taskSubmitter;
        private Future<?> runnerFuture=null;

        public TaskRunnerDescriptor(ExecutorService executor, TaskDefinition definition, CollectiveBasedTask cbt, TaskRunner runner) {
            this.executor = executor;
            this.definition = definition;
            this.cbt = cbt;
            this.runner = runner;
            runnerFuture = executor.submit(runner);
            taskSubmitter = r -> {
                try {
                    /* TODO: CHECK HOW TO SYNCHRONIZE THE TWO RUNNERS */
                    executor.submit(runner).wait();
                    return cbt.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeWrapperException("Error on runner runs", e);
                }
            };
        }

        public void cancel() {
            runnerFuture.cancel(true);
            cbt.cancel(true);
        }


        public TaskDefinition getDefinition() {
            return definition;
        }

        public CollectiveBasedTask getCbt() {
            return cbt;
        }

        public TaskRunner getRunner() {
            return runner;
        }

        public long getCreationTimestamp() {
            return creationTimestamp;
        }

        public JsonNode getStateDescription() {
            ObjectNode node = jsonMapper.createObjectNode();
            node.set("applicationState", runner.getStateDescription());
            node.put("cbtState", cbt.getCurrentState().toString());
            return node;
        }
    }

    public static Runtime fromApplication(Config config, SmartSocietyComponents components) throws IOException, InstantiationException {
        Class<? extends Application> applicationClass = getApplicationClass();
        Application application = instantiateApplication(applicationClass);
        SmartSocietyApplicationContext context =
            new SmartSocietyApplicationContext(
                createCollectiveKindRegistry(application),
                components.getPeerManager());

        return new Runtime(context, application);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Application> getApplicationClass() throws IOException {
        ClassLoader cl = new Integer(2).getClass().getClassLoader();
        Set<ClassPath.ClassInfo> classesInPackage = ClassPath.from(cl).getAllClasses();
        List<? extends Class<?>> applicationClasses = classesInPackage
            .stream()
            .map(c -> c.load())
            .filter(c ->
                        !Modifier.isAbstract(c.getModifiers())
                            && Modifier.isInterface(c.getModifiers())
                            && Application.class.isAssignableFrom(c))
            .collect(Collectors.toList());

        Preconditions.checkArgument(
            !applicationClasses.isEmpty(),
            "No concrete implementation of eu.smartsocietyproject.pf.Application found");
        Preconditions.checkArgument(applicationClasses.size() > 1, "" +
            "Too many concrete implementations of eu.smartsocietyproject.pf.Application found");

        return (Class<? extends Application>) applicationClasses.get(0);
    }

    private static Application instantiateApplication(Class<? extends Application> applicationClass)
        throws InstantiationException {
        try {
            return applicationClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                String.format(
                    "Unable to access empty constructor for class %s",
                    applicationClass.getCanonicalName()));
        }
    }

    private static CollectiveKindRegistry createCollectiveKindRegistry(Application application) {
        return
            new CollectiveKindRegistry(
                application
                    .listCollectiveKinds()
                    .stream()
                    .collect(Collectors.toMap(k->k.getId(), Function.identity())));
    }

}
