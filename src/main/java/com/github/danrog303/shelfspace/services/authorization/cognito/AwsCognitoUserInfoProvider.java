package com.github.danrog303.shelfspace.services.authorization.cognito;

import com.github.danrog303.shelfspace.services.authorization.UserInfo;
import com.github.danrog303.shelfspace.services.authorization.UserInfoProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.util.List;

/**
 * AWS Cognito implementation of {@link UserInfoProvider}.
 */
@Service
@RequiredArgsConstructor
public class AwsCognitoUserInfoProvider implements UserInfoProvider {
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${amazon.aws.cognito.pool-id}")
    private String cognitoUserPoolId;

    /**
     * Connects to AWS Cognito service and retrieves additional information about the specified user.
     */
    @Override
    public UserInfo getUserInfo(String userId) {
        AdminGetUserRequest getUserRequest = AdminGetUserRequest.builder()
                .username(userId)
                .userPoolId(cognitoUserPoolId)
                .build();

        AdminGetUserResponse getUserResponse = cognitoClient.adminGetUser(getUserRequest);
        List<AttributeType> attributes = getUserResponse.userAttributes();

        String email = attributes.stream().filter(attr -> attr.name().equals("email")).findFirst().get().value();
        String nickname = attributes.stream().filter(attr -> attr.name().equals("nickname")).findFirst().get().value();

        return new UserInfo(getUserResponse.username(), nickname, email);
    }
}
