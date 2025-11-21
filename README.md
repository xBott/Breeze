# Breeze Framework

At the moment Breeze is a simple java framework for developing common applications and especially minecraft servers. It provides such things as annotation based Dependency Injection, Module system with jar loaders and some other stuff like central serialization and so on.

## About Modules
1. **breeze-api** - interfaces for all the objects
2. **breeze-core** - realization of the main things from breeze-api
3. **breeze-processor** - annotation processor for breeze-api
4. **breeze-paper** - minecraft related module that adds abstraction above bukkit and provides support for breeze DI and modules.
5. **breeze-admin** - a test module for breeze engine


## So far working on
1. Better minecraft integration
2. Auto-Loadable objects
3. Better configuration system (basic configurations, tree configurations)
4. Event system
5. Scripting


## Future Plans
1. Dependency resolving on the level of modules: evaluating the order of module loading. Dependencies list in @ModuleInfo
2. Cycle-Dependency logging
3. **breeze-data** - centralized data base management with support of multiple data bases and other infrastructure stuff like MySql, PostgresDB, MongoDB, Neo4j, Redis and so.  

### How to integrate into your project?

Repository
```kotlin
maven {
    url = uri("https://repo.the-light.online/releases")
}
```

Dependencies
```kotlin
annotationProcessor("me.bottdev:breeze-processor:<version>")
compileOnly("me.bottdev:breeze-api:<version>")
compileOnly("me.bottdev:breeze-core:<version>")
compileOnly("me.bottdev:breeze-paper:<version>")
```