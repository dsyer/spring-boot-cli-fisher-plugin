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