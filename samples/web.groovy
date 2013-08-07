@Controller
class Example {

	@Autowired
	private MyService myService;

	@RequestMapping("/")
	public String helloWorld() {
		return myService.sayWorld();
	}

}

@Service
class MyService {

	public String sayWorld() {
		return "World!";
	}
}


