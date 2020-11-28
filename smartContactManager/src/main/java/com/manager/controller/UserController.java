package com.manager.controller;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.manager.dao.ContactRepository;
import com.manager.dao.UserRepository;
import com.manager.entities.Contacts;
import com.manager.entities.User;
import com.manager.helper.MessageManage;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userrepo;
	@Autowired
	private ContactRepository contactrepo;

	// home or index controller
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User DashBoard");
		return "normal/dashboard";
	}

	// view contact controller
	@GetMapping("/addContact")
	public String addContact(Model model, Principal principal) {

		model.addAttribute("title", "addContact");
		model.addAttribute("contact", new Contacts());

		return "normal/addContact";
	}

	@PostMapping("/processContact")
	public String processContact(@ModelAttribute Contacts contact, @RequestParam("pf_image") MultipartFile file,
			HttpSession session, Principal p) {

		// System.out.println(contact);

		try {
			String userName = p.getName();
			User user = userrepo.getUserByUserName(userName);

			if (file.isEmpty()) {
				contact.setImage("pf_pic.png");
			} else {
				String pf_pic = file.getOriginalFilename();
				contact.setImage(pf_pic);
				File file2 = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			contact.setUser(user);
			user.getContact().add(contact);
			userrepo.save(user);
			session.setAttribute("message", new MessageManage(" successfully saved contact!! Add More...", "success"));
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new MessageManage("Something Went Wrong !! " + e.getMessage(), "danger"));
		}

		// System.out.println("added");

		return "normal/addContact";
	}

	// show contact handler
	// per page 5
	// current page=page
	@GetMapping("/allContact/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m, Principal p) {
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);
		int user_id = user.getId();
//		user.getContact();
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Contacts> contactlist = contactrepo.findContactsByUser(user_id, pageRequest);
		m.addAttribute("contact", contactlist);
		m.addAttribute("title", "viewContact");
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPage", contactlist.getTotalPages());

		return "normal/showContact";
	}

	// common part for all handler
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String userName = p.getName();

		User user = userrepo.getUserByUserName(userName);

		m.addAttribute("title", "addContact-smartContactManager");
		m.addAttribute("user", user);
	}

	// showing speial contact view
	@GetMapping("/contact/{cId}")
	public String showContactDetails(@PathVariable("cId") Integer cid, Model m, Principal p) {
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);

		Optional<Contacts> contactoptional = contactrepo.findById(cid);
		Contacts contact = contactoptional.get();

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("title", "Contact-Detail");
			m.addAttribute("contact", contact);

		}

		return "normal/contact_detail";

	}

	// showing speial contact view
	@GetMapping("/deleted/{cId}")
	public String deletContactDetails(@PathVariable("cId") Integer cid, Model m, Principal p, HttpSession session) {
		Optional<Contacts> contactoptional = contactrepo.findById(cid);
		Contacts contact = contactoptional.get();
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);
		// int getcId = contact.getcId();
		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			contactrepo.delete(contact);
			session.setAttribute("message", new MessageManage("deleted....", "success"));
		}
		return "redirect:/user/allContact/0";

	}

	// update form handler
	@RequestMapping(value = "/update/{cId}" ,method = RequestMethod.POST)
	public String updateContact(@PathVariable("cId") Integer cid, Model m, Principal p, HttpSession session) {
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);

		Optional<Contacts> contactoptional = contactrepo.findById(cid);
		Contacts contact = contactoptional.get();

		if (user.getId() == contact.getUser().getId()) {
			m.addAttribute("title", "Contact-Detail");
			m.addAttribute("contact", contact);

		}
		return "normal/updateContact";
	}

	// updating
	@RequestMapping(value ="/updateContactList" ,method = RequestMethod.POST)
	public String updateContactList(@ModelAttribute Contacts contact, Principal p,
			@RequestParam("pf_image") MultipartFile file, Model m, HttpSession session) {
		try {
	    //geting old image
	    Contacts oldcontact = contactrepo.findById(contact.getcId()).get();
		String oldpf_pic = oldcontact.getImage();
		
		//getting new image
		String newpf_pic = file.getOriginalFilename();
		
		//getting user
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);
		
       // contact.setUser(user);
        
        contactrepo.save(contact);
			if (!file.isEmpty()) {
				// new pic update
                contact.setImage(newpf_pic);
				contact.setUser(user);
				contactrepo.save(contact);
				File file2 = new ClassPathResource("static/images").getFile();
				Path deletepath = Paths.get(file2.getAbsolutePath() + File.separator + oldpf_pic);
				Path savepath = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.delete(deletepath);
				
				Files.copy(file.getInputStream(), savepath, StandardCopyOption.REPLACE_EXISTING);
				session.setAttribute("message", new MessageManage("updated....", "success"));
			} else {
				// no file then save old one
				contact.setImage(oldpf_pic);
				contact.setUser(user);
				contactrepo.save(contact);
				session.setAttribute("message", new MessageManage("updated !!....", "success"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new MessageManage("sorry", "danger"));
		}

		return "redirect:/user/allContact/0";
	}
	
	//profile handler

	@GetMapping("/profile")
	public String profile(Model m, Principal p) {
		String userName = p.getName();
		User user = userrepo.getUserByUserName(userName);
		m.addAttribute("user", user);
		m.addAttribute("title", "User-Profile");
		return "normal/profile";
	}

}
