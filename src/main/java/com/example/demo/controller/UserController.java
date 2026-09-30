package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.common.PageResponse;
import com.example.demo.dto.UserCreateRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getPage(
        @RequestParam (value ="page", defaultValue = "1") int page,
        @RequestParam (value="size", defaultValue = "10") int size
    ){

        PageResponse<User> result=userService.getPage(page, size);
        return ApiResponse.success(toPageResponse(result));
    }


    @GetMapping ("/search")
    public ApiResponse<PageResponse<UserResponse>> searchByName(
        @RequestParam String name,
        @RequestParam (value ="page", defaultValue = "1") int page,
        @RequestParam (value ="size", defaultValue = "10") int size
    ){
        PageResponse<User> result =
                userService.searchByName(name, page, size);
        return ApiResponse.success(toPageResponse(result));
    }


    private PageResponse<UserResponse> toPageResponse(PageResponse<User> page) {
        // TODO Auto-generated method stub
        List<UserResponse> items = page.getItems()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        return new PageResponse<>(
                items,
                page.getPage(),
                page.getSize(),
                page.getTotal(),
                page.getTotalPages()
        );
        //throw new UnsupportedOperationException("Unimplemented method 'toPageResponse'");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable Long id) {
        //向service调用服务
        User user=userService.getById(id).orElseThrow(()->new UserNotFoundException(id));
        //返回apireponse类型
        return ApiResponse.success(UserResponse.from(user));
    }

    @PostMapping 
    public ApiResponse<UserResponse> create( @Valid @RequestBody UserCreateRequest request){
        User user=new User(request.getId(),request.getName(),request.getAge());
        User saveUser=userService.create(user);

        return ApiResponse.success(UserResponse.from(saveUser));
    }

    // @getmapping 
    // public apiresponse<list<userresponse>> getall(){
    //     list<userresponse> users=userservice.getall().stream().map(userresponse::from).collect(collectors.tolist());
    //     return apiresponse.success(users);
    // }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest requests ) {
        
        
        User user=new User(id, requests.getName(),requests.getAge());
        User updateUser =userService.update(id, user).orElseThrow(()->new UserNotFoundException(id));;

        
        return ApiResponse.success(UserResponse.from(updateUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        boolean deleted=userService.deleteById(id);
        if (!deleted) {
            throw new UserNotFoundException(id);
        }
        return ResponseEntity.noContent().build();
    }
}
