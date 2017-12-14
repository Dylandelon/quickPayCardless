package cardlessQuickPay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class indexController {

	@RequestMapping("/")
	public void index(){
		System.out.println("111");
	}
}
