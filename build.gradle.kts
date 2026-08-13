plugins {
    java
}

// 각 문제(서브프로젝트)의 공통 설정.
// 문제마다 build.gradle.kts 를 따로 쓰지 않고 여기서 한 번에 정의한다.
subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    // impl/ 을 별도 소스셋으로 등록한다.
    //
    // 이유가 둘이다.
    //   1. 스켈레톤 시그니처를 바꾸면 impl 이 조용히 낡는다. 소스셋에 넣으면 컴파일이 잡아준다.
    //   2. 정답 검증을 위해 파일을 src/main 에 덮어썼다가 되돌리는 방식은 위험하다.
    //      중간에 끊기면 학습자 코드 위에 정답이 남는다. 소스셋을 쓰면 src/main 을 건드리지 않는다.
    val sourceSets = extensions.getByType<SourceSetContainer>()
    sourceSets.create("checkImpl") {
        java.setSrcDirs(listOf("impl"))

        // main 출력을 컴파일 클래스패스에 넣는다.
        //
        // 구현체가 여러 개인 문제(스택의 배열/연결, 해시맵의 체이닝/개방주소)에서는
        // 공유 인터페이스가 src/main 에만 있고 impl/ 에는 구현체만 둔다.
        // 이게 없으면 impl 의 ArrayStack 이 Stack 인터페이스를 못 찾는다.
        //
        // impl 에도 있는 클래스는 같은 FQCN 이 클래스패스에 겹치지만,
        // 자바는 소스셋의 소스를 우선하므로 impl 쪽이 컴파일된다.
        compileClasspath += sourceSets["main"].output
    }

    // checkImpl 이 main 의 외부 의존성(implementation)도 쓸 수 있게 한다.
    configurations["checkImplImplementation"].extendsFrom(configurations["implementation"])

    dependencies {
        add("testImplementation", "org.junit.jupiter:junit-jupiter:5.11.4")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    extensions.configure<JavaPluginExtension> {
        toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        // @Timeout 을 선점형으로. 기본 모드는 테스트가 **끝난 뒤에** 시간을 재기 때문에
        // O(n^2) 구현을 넣으면 20초 만에 실패하는 대신 몇 시간을 매달린다.
        // 성능 테스트가 "느리면 빨리 실패한다"를 실제로 하려면 이 설정이 필요하다.
        systemProperty("junit.jupiter.execution.timeout.thread.mode.default", "SEPARATE_THREAD")
        // 그리고 **모든 테스트에 기본 시간 제한**을 건다.
        // 무한 루프에 빠지는 구현(예: 펜윅 트리에서 x -= x & -x 를 잘못 쓰면 x=0 에서 안 끝난다)이
        // run.sh 를 통째로 세우는 대신 그 테스트만 실패하게 만든다.
        // 개별 @Timeout 이 있으면 그쪽이 이긴다.
        systemProperty("junit.jupiter.execution.timeout.testable.method.default", "30s")
        testLogging {
            events("passed", "failed", "skipped")
            showStandardStreams = true
        }
        // 스켈레톤 상태에서는 대부분 실패하는 것이 정상이므로 전체 목록을 보여주고 넘어간다.
        // 회귀 감지가 필요할 때는 -Pstrict 로 exit code 를 살린다.
        ignoreFailures = !providers.gradleProperty("strict").isPresent
    }

    // 같은 테스트를 impl/ 구현으로 돌린다. src/main 은 손대지 않는다.
    // 클래스패스에서 checkImpl 출력을 앞에 두어 스켈레톤 대신 impl 이 로딩되게 한다.
    tasks.register<Test>("checkImplTest") {
        description = "impl/ 의 정답 구현이 테스트를 통과하는지 확인한다"
        group = "verification"

        dependsOn("checkImplClasses", "testClasses")

        testClassesDirs = sourceSets["test"].output.classesDirs

        // 순서가 계약이다.
        //   checkImpl 이 맨 앞  -> impl/ 에 있는 클래스는 impl 것이 로딩된다(스켈레톤을 덮는다)
        //   main 이 그 뒤       -> impl/ 에 없는 클래스(공유 인터페이스 등)를 여기서 가져온다
        // main 을 빼면 다중 구현 문제에서 인터페이스를 못 찾아 테스트가 아예 발견되지 않는다.
        // (03-stack 에서 실제로 그렇게 터졌다. 컴파일은 되는데 런타임에 1개만 발견됐다.)
        classpath = sourceSets["checkImpl"].output +
            sourceSets["test"].output +
            sourceSets["main"].output +
            configurations["testRuntimeClasspath"]

        useJUnitPlatform()
        ignoreFailures = false        // 정답은 반드시 통과해야 한다. 예외 없음.
        reports.junitXml.outputLocation.set(layout.buildDirectory.dir("test-results/checkImplTest"))
    }
}
