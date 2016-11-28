package eu.smartsocietyproject.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import com.typesafe.config.Config;
import eu.smartsocietyproject.TaskResponse;
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

    public Runtime init(Config config) {
        application.init(context, config);
        return this;
    }

    public boolean startTask(TaskDefinition definition) {
        TaskRequest request = application.createTaskRequest(definition);

        //todo-sv: why are there two different runners?
        //executor executes and new TaskRunnerDescriptor executes...???
        TaskRunner runner = application.createTaskRunner(request);
        runnerDescriptors.put(definition.getId(), new TaskRunnerDescriptor(executor, definition, runner));
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

    //todo-sv: what is the exact purpose of this run? 
    //just to keep the thread open?
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        runnerDescriptors.values().forEach(r->r.cancel());
    }

    private static class TaskRunnerDescriptor {
        private final long creationTimestamp = java.time.Instant.now().toEpochMilli();
        private final ExecutorService executor;
        private final TaskDefinition definition;
        private final TaskRunner runner;
        private final Function<Runnable, TaskResponse> taskSubmitter;
        private Future<?> runnerFuture=null;

        public TaskRunnerDescriptor(ExecutorService executor, TaskDefinition definition, TaskRunner runner) {
            this.executor = executor;
            this.definition = definition;
            this.runner = runner;
            runnerFuture = executor.submit(runner);
            taskSubmitter = r -> {
                Future<TaskResponse> f = executor.submit(runner);
                while (true) {
                    try {
                        return f.get();
                    } catch (ExecutionException | CancellationException e) {
                        return TaskResponse.FAIL;
                    } catch (InterruptedException e) {
                    }
                }
            };
        }

        public void cancel() {
            runnerFuture.cancel(true);
        }


        public TaskDefinition getDefinition() {
            return definition;
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
            return node;
        }
    }

    public static Runtime fromApplication(Config config, SmartSocietyComponents components) throws IOException, InstantiationException {
        return fromApplication(config, components, getApplicationClass());
    }

    public static Runtime fromApplication(
        Config config,
        SmartSocietyComponents components,
        Class<? extends Application> applicationClass) throws IOException, InstantiationException {
        Application application = instantiateApplication(applicationClass);
        CollectiveKindRegistry registry = createCollectiveKindRegistry(application);
        SmartSocietyApplicationContext context =
            new SmartSocietyApplicationContext(
                registry,
                components.getPeerManagerFactory(),
                components.getSmartComServiceFactory());
        return new Runtime(context, application).init(config);
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
