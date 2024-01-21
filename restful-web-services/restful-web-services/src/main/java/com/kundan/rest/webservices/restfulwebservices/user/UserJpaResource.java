package com.kundan.rest.webservices.restfulwebservices.user;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.kundan.rest.webservices.restfulwebservices.jpa.PostRepository;
import com.kundan.rest.webservices.restfulwebservices.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	
	private UserDaoService service;
	
	private UserRepository UserRepository;
	
	private PostRepository postRepository;
	
	public UserJpaResource(UserDaoService service, UserRepository UserRepository, PostRepository postRepository) {
		this.service = service;
		this.UserRepository = UserRepository;
		this.postRepository = postRepository;
	}
	
	//GET /users
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return UserRepository.findAll();
	}
	
	//GET /user
//	@GetMapping("/users/{id}")
//	public User findUser(@PathVariable Integer id){
//		User user = service.findOne(id);
//		
//		if(user == null)
//			throw new UserNotFoundException("id:"+id);
//		
//		return user;
//	}
	
	//Implementation of HATEOAS
	
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> findUser(@PathVariable Integer id){
		Optional<User> user = UserRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		EntityModel<User> entityModel = EntityModel.of(user.get());
		
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-users"));
		
		return entityModel;
	}
	
	//POST /users
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = UserRepository.save(user);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
						.path("/{id}")
						.buildAndExpand(savedUser.getId())
						.toUri();   // /user/4 => /users/{id}
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable Integer id){
		UserRepository.deleteById(id);
		
	}
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostsForUser(@PathVariable Integer id){
		Optional<User> user = UserRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		return user.get().getPosts();
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@PathVariable Integer id, @Valid @RequestBody Post post){
		Optional<User> user = UserRepository.findById(id);
		
		if(user.isEmpty())
			throw new UserNotFoundException("id:"+id);
		
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();   // /user/4 => /users/{id}
		return ResponseEntity.created(location).build();
	
	}
	
}
