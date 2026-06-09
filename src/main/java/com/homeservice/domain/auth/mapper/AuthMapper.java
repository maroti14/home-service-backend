package com.homeservice.domain.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.homeservice.domain.auth.dto.response.AuthResponse;
import com.homeservice.domain.auth.entity.User;
import com.homeservice.security.UserDetailsImpl;

@Mapper(componentModel = "spring")
public interface AuthMapper {

//    @Mapping(source =  "id", target = "userId")
//    @Mapping(source = "role.name", target = "role")
//    @Mapping(source = "isMobileVerified",
//             target = "mobileVerified")
	@Mapping(source = "user.id", target = "userId")
	@Mapping(source = "user.role.name", target = "role")
	@Mapping(source = "user.isMobileVerified", target = "mobileVerified")
	@Mapping(expression = "java(token)", target = "accessToken")
	@Mapping(expression = "java(refreshToken)", target = "refreshToken")
	@Mapping(expression = "java(\"Bearer\")", target = "tokenType")
	@Mapping(expression = "java(expiresIn)", target = "expiresIn")
	AuthResponse toAuthResponse(User user, String token, String refreshToken, long expiresIn);

	default AuthResponse fromDetails(UserDetailsImpl d, String token, String refreshToken, long expiresIn) {
		return AuthResponse.builder().userId(d.getUserId()).name(d.getName()).email(d.getUsername())
				.mobile(d.getMobile()).role(d.getRole()).mobileVerified(d.isMobileVerified()).accessToken(token)
				.refreshToken(refreshToken).tokenType("Bearer").expiresIn(expiresIn).build();
	}
}