package com.github.danrog303.shelfspace.services.authorization.cognito;

import com.github.danrog303.shelfspace.services.authorization.UserDeleteProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;

/**
 * AWS Cognito implementation of {@link UserDeleteProvider}.
 */
@Service
@RequiredArgsConstructor
public class AwsCognitoUserDeleteProvider implements UserDeleteProvider {
    private final CognitoIdentityProviderClient cognitoClient;

    @Value("${amazon.aws.cognito.pool-id}")
    private String cognitoUserPoolId;

    /**
     * Connects to AWS Cognito service and sends request to delete the specified user.
     */
    @Override
    public void deleteUser(String userId) {
        AdminDeleteUserRequest deleteUserRequest = AdminDeleteUserRequest.builder()
                .username(userId)
                .userPoolId(cognitoUserPoolId)
                .build();

        cognitoClient.adminDeleteUser(deleteUserRequest);
    }
}
