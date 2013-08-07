# Spring Boot - CLI (Mark Fisher Plugin)

Provides default `@ResponseBody` annotations on any `@RequestMapping` method that does not have `@NoResponseBody`.  E.g. this works

`app.groovy`

```
@Controller
class Example {

    @RequestMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }
    
}
```

Install the JAR file from this project in `${SPRING_HOME}/lib` if you
are running the `spring` script from the ZIP distro of Spring Boot
CLI. Or just use `-cp <path-to-jar>` in your `spring run` command.
