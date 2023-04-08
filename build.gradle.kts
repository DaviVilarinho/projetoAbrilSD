plugins {
    id("java")
    id("application")
    id("com.google.protobuf", ) version "0.9.1"
    // Generate IntelliJ IDEA's .idea & .iml project files
    id("idea")
}

group = "ufu.davigabriel"
version = "1.0-SNAPSHOT"

var grpcVersion = "1.53.0"
var protobufVersion = "3.21.7"
var protocVersion = protobufVersion

repositories {
    mavenCentral()
    maven(url="https://maven-central.storage-download.googleapis.com/maven2/")
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")


    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    // examples/advanced need this for JsonFormat
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")

    runtimeOnly("io.grpc:grpc-netty-shaded:${grpcVersion}")

    testImplementation("io.grpc:grpc-testing:${grpcVersion}")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.4.0")
}

tasks.test {
    useJUnitPlatform()
}

gradle.taskGraph.whenReady {
    val task = this.allTasks.find { it.name.endsWith(".main()") } as? JavaExec // or whatever other method your Main class runs
    task?.let {
        it.setExecutable(it.javaLauncher.get().executablePath.asFile.absolutePath)
    }
}
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}
