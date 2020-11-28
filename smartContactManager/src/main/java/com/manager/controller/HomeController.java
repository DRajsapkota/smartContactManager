package com.manager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manager.dao.UserRepository;
import com.manager.entities.User;
import com.manager.helper.MessageManage;


@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepo;

	@GetMapping("/home")
	public String homePage(Model model) {
		model.addAttribute("title", "Home-smartContactManager");
		return "home";
	}

	@GetMapping("/about")
	public String aboutPage(Model model) {
		model.addAttribute("title", "About-smartContactManager");
		return "about";
	}

	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("title", "Signup-smartContactManager");
		model.addAttribute("user", new User());
		
		return "signup";
	}

	@PostMapping("/do_register")
	public String submitForm(@Valid @ModelAttribute("user") User user,BindingResult result,
			@RequestParam(value = "agreed", defaultValue = "false") Boolean agreement,
			Model model,HttpSession session) {

		try {
			if (!agreement) {
				System.out.println(agreement);
				throw new Exception(" You havent agreed terms & conditions !!");
			}
			
			if (result.hasErrors()) // if we get any errors then we terminated user
			{
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setActivity(true);
			user.setImageUrl("pf_pic.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			this.userRepo.save(user);
			model.addAttribute("title", "Signup-smartContactManager");
			model.addAttribute("user", new User());
			
			session.setAttribute("message", new MessageManage("Registration success","alert-success"));
			return "login";
			
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new MessageManage("Something Went Wrong !! "+e.getMessage(),"alert-danger"));
			return "signup";
			
		}
		
	}

	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login-smartContactManager");
		return "login";
	}
}
